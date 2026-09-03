-- Expansão do OR do blocking em UNION. Mesmo resultado, 197 vezes mais rápido.
--
-- A medição está em docs/medicao-escala.md. Em resumo:
--
--   O blocking usava OR de quatro condições na junção. Com OR de colunas diferentes,
--   o otimizador do Oracle não consegue escolher um caminho de acesso por índice:
--   ele ordena os dois lados e aplica a condição como FILTER. Na prática, o blocking
--   virava decoração, porque a varredura acontecia de qualquer jeito.
--
--   Comprovado isolando cada caso, com 4 mil registros:
--     a.cns = b.cns .......................... HASH JOIN ............... 38 ms
--     OR de CNS, CPF e nascimento ............ MERGE JOIN + FILTER .... 608 ms
--     OR das quatro, com SOUNDEX ............. MERGE JOIN + FILTER . 47.500 ms
--
--   Cada condição sozinha é uma igualdade simples, e igualdade simples o otimizador
--   resolve com hash join. Separando as quatro em ramos de um UNION, cada ramo volta
--   a ter esse caminho: os mesmos 26.126 pares saem em 241 ms, com quatro HASH JOIN
--   no plano onde antes havia zero.
--
-- O que NÃO muda: pesos, cortes, piso de evidência e as duas exclusões da fila. A
-- saída é idêntica — foi comparada linha a linha, par a par, antes e depois.
--
-- Detalhe de desempenho: o UNION roda sobre registro_origem direto, sem passar pela
-- normalização de acentos. Só o ramo do SOUNDEX precisa do nome normalizado; fazer os
-- quatro ramos lerem uma CTE normalizada obrigaria o banco a montá-la oito vezes.

CREATE OR REPLACE VIEW vw_candidato_identificacao AS
WITH par_bruto AS (
  -- Um ramo por âncora. Cada um é uma igualdade simples, que o otimizador resolve
  -- com hash join. O UNION ainda remove as repetições entre ramos.
  SELECT a.id AS a_id, b.id AS b_id
    FROM registro_origem a
    JOIN registro_origem b ON b.id > a.id AND b.sistema_id <> a.sistema_id
                          AND a.cns = b.cns
  UNION
  SELECT a.id, b.id
    FROM registro_origem a
    JOIN registro_origem b ON b.id > a.id AND b.sistema_id <> a.sistema_id
                          AND a.cpf = b.cpf
  UNION
  SELECT a.id, b.id
    FROM registro_origem a
    JOIN registro_origem b ON b.id > a.id AND b.sistema_id <> a.sistema_id
                          AND a.data_nascimento = b.data_nascimento
  UNION
  SELECT a.id, b.id
    FROM registro_origem a
    JOIN registro_origem b ON b.id > a.id AND b.sistema_id <> a.sistema_id
                          AND SOUNDEX(UPPER(TRANSLATE(a.nome,
                                'ÁÀÂÃÄÉÈÊËÍÌÎÏÓÒÔÕÖÚÙÛÜÇÑáàâãäéèêëíìîïóòôõöúùûüçñ',
                                'AAAAAEEEEIIIIOOOOOUUUUCNaaaaaeeeeiiiiooooouuuucn')))
                            = SOUNDEX(UPPER(TRANSLATE(b.nome,
                                'ÁÀÂÃÄÉÈÊËÍÌÎÏÓÒÔÕÖÚÙÛÜÇÑáàâãäéèêëíìîïóòôõöúùûüçñ',
                                'AAAAAEEEEIIIIOOOOOUUUUCNaaaaaeeeeiiiiooooouuuucn')))
),
normalizado AS (
  SELECT r.id,
         r.cns,
         r.cpf,
         r.data_nascimento,
         UPPER(TRANSLATE(r.nome,
               'ÁÀÂÃÄÉÈÊËÍÌÎÏÓÒÔÕÖÚÙÛÜÇÑáàâãäéèêëíìîïóòôõöúùûüçñ',
               'AAAAAEEEEIIIIOOOOOUUUUCNaaaaaeeeeiiiiooooouuuucn')) AS nome_comparavel,
         UPPER(TRANSLATE(r.nome_mae,
               'ÁÀÂÃÄÉÈÊËÍÌÎÏÓÒÔÕÖÚÙÛÜÇÑáàâãäéèêëíìîïóòôõöúùûüçñ',
               'AAAAAEEEEIIIIOOOOOUUUUCNaaaaaeeeeiiiiooooouuuucn')) AS nome_mae_comparavel
    FROM registro_origem r
),
pares AS (
  SELECT
    pb.a_id AS registro_a_id,
    pb.b_id AS registro_b_id,
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
  FROM par_bruto pb
  JOIN normalizado a ON a.id = pb.a_id
  JOIN normalizado b ON b.id = pb.b_id
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
        SELECT 1
          FROM vinculo_registro va
          JOIN vinculo_registro vb
            ON vb.paciente_mestre_id = va.paciente_mestre_id
         WHERE va.registro_origem_id = p.registro_a_id
           AND vb.registro_origem_id = p.registro_b_id
      );
