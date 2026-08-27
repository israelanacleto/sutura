-- Refinamento do motor de identificação, a partir de dois defeitos encontrados testando
-- contra o banco real.
--
-- 1) ACENTUAÇÃO
--    "CONCEIÇÃO ROCHA" e "CONCEICAO ROCHA" eram tratados como nomes diferentes. Isso não
--    é um caso de laboratório: sistema de saúde brasileiro gravando sem acento é regra,
--    não exceção — legado em ASCII, integração que perde diacrítico, importação de
--    planilha. O efeito era grave: um par com CNS, CPF e data de nascimento idênticos
--    caía de 99 para 89 e ia parar na revisão humana por causa de uma cedilha.
--    A comparação passa a ser feita sobre a forma sem acento.
--
-- 2) PARES JÁ COSTURADOS
--    Depois de costurar A-B e A-C, o par B-C continuava na fila, embora os três já
--    fossem o mesmo paciente mestre. Na tela isso aparece como "costurar a mesma pessoa
--    de novo". Agora o par sai da fila quando ambos os registros já apontam para o mesmo
--    paciente.
--
-- O que NÃO mudou: os pesos, os cortes e a regra de piso de evidência. O caso do
-- homônimo continua sendo o critério de aceite e continua saindo em 10.

CREATE OR REPLACE VIEW vw_candidato_identificacao AS
WITH normalizado AS (
  SELECT r.id,
         r.sistema_id,
         r.cns,
         r.cpf,
         r.data_nascimento,
         -- CONVERT para US7ASCII remove o diacrítico: Ç -> C, Ã -> A, Ú -> U
         UPPER(CONVERT(r.nome, 'US7ASCII')) AS nome_comparavel,
         UPPER(CONVERT(r.nome_mae, 'US7ASCII')) AS nome_mae_comparavel
    FROM registro_origem r
),
pares AS (
  SELECT
    a.id AS registro_a_id,
    b.id AS registro_b_id,
      CASE WHEN a.cns IS NOT NULL AND b.cns IS NOT NULL THEN 45 ELSE 0 END
    + CASE WHEN a.cpf IS NOT NULL AND b.cpf IS NOT NULL THEN 20 ELSE 0 END
    + CASE WHEN a.data_nascimento IS NOT NULL AND b.data_nascimento IS NOT NULL THEN 15 ELSE 0 END
    + CASE WHEN a.nome_mae_comparavel IS NOT NULL AND b.nome_mae_comparavel IS NOT NULL THEN 10 ELSE 0 END
    + 10 AS peso_comparavel,
      CASE WHEN a.cns = b.cns THEN 45 ELSE 0 END
    + CASE WHEN a.cpf = b.cpf THEN 20 ELSE 0 END
    + CASE WHEN a.data_nascimento = b.data_nascimento THEN 15 ELSE 0 END
    + CASE WHEN a.nome_mae_comparavel = b.nome_mae_comparavel THEN 10 ELSE 0 END
    + (UTL_MATCH.JARO_WINKLER_SIMILARITY(a.nome_comparavel, b.nome_comparavel) * 0.10) AS peso_obtido,
    UTL_MATCH.JARO_WINKLER_SIMILARITY(a.nome_comparavel, b.nome_comparavel) AS similaridade_nome
  FROM normalizado a
  JOIN normalizado b
    ON b.id > a.id
   AND b.sistema_id <> a.sistema_id
   AND (
        a.cns = b.cns
     OR a.cpf = b.cpf
     OR a.data_nascimento = b.data_nascimento
     OR SOUNDEX(a.nome_comparavel) = SOUNDEX(b.nome_comparavel)
   )
)
SELECT
  p.registro_a_id,
  p.registro_b_id,
  p.peso_comparavel,
  p.similaridade_nome,
  ROUND(p.peso_obtido / p.peso_comparavel * 100, 0) AS score,
  CASE
    WHEN p.peso_comparavel < 60 THEN 'REVISAR'
    WHEN ROUND(p.peso_obtido / p.peso_comparavel * 100, 0) >= 90 THEN 'COSTURAR'
    WHEN ROUND(p.peso_obtido / p.peso_comparavel * 100, 0) >= 70 THEN 'REVISAR'
    ELSE 'SEPARAR'
  END AS recomendacao
FROM pares p
WHERE NOT EXISTS (
        SELECT 1 FROM decisao_identificacao d
         WHERE (d.registro_a_id = p.registro_a_id AND d.registro_b_id = p.registro_b_id)
            OR (d.registro_a_id = p.registro_b_id AND d.registro_b_id = p.registro_a_id)
      )
  AND NOT EXISTS (
        -- já pertencem ao mesmo paciente mestre: não há o que decidir
        SELECT 1
          FROM vinculo_registro va
          JOIN vinculo_registro vb
            ON vb.paciente_mestre_id = va.paciente_mestre_id
         WHERE va.registro_origem_id = p.registro_a_id
           AND vb.registro_origem_id = p.registro_b_id
      );
