-- Motor de identificação de pacientes (record linkage), calculado no banco.
--
-- Por que em SQL e não em Java: UTL_MATCH é nativo do Oracle. Trazer 128 mil registros
-- para a JVM só para comparar strings seria desperdiçar exatamente o que o banco faz bem.
--
-- Como o score funciona:
--   Cada campo identificador tem um peso. Um campo só entra na conta quando é COMPARÁVEL,
--   isto é, quando os dois lados o possuem. CPF ausente num dos sistemas não é evidência
--   contra o vínculo — é ausência de evidência, e penalizar isso classificaria como
--   "pessoas diferentes" justamente o caso mais comum na saúde: cadastro incompleto.
--
--   score = peso_obtido / peso_comparavel * 100
--
--   Consequência importante: um par com POUCO campo comparável pode atingir score alto
--   com pouquíssima evidência (dois "Roberto Nascimento" com a mesma data de nascimento e
--   nenhum documento). Por isso a recomendação não olha só o score: abaixo de 60 pontos de
--   evidência comparável, o par vai para revisão humana independentemente do score.

CREATE OR REPLACE VIEW vw_candidato_identificacao AS
WITH pares AS (
  SELECT
    a.id AS registro_a_id,
    b.id AS registro_b_id,
    -- peso comparável: soma dos pesos dos campos presentes dos DOIS lados
      CASE WHEN a.cns IS NOT NULL AND b.cns IS NOT NULL THEN 45 ELSE 0 END
    + CASE WHEN a.cpf IS NOT NULL AND b.cpf IS NOT NULL THEN 20 ELSE 0 END
    + CASE WHEN a.data_nascimento IS NOT NULL AND b.data_nascimento IS NOT NULL THEN 15 ELSE 0 END
    + CASE WHEN a.nome_mae IS NOT NULL AND b.nome_mae IS NOT NULL THEN 10 ELSE 0 END
    + 10 AS peso_comparavel,   -- o nome sempre é comparável
    -- peso obtido: dos campos comparáveis, quantos de fato conferem
      CASE WHEN a.cns = b.cns THEN 45 ELSE 0 END
    + CASE WHEN a.cpf = b.cpf THEN 20 ELSE 0 END
    + CASE WHEN a.data_nascimento = b.data_nascimento THEN 15 ELSE 0 END
    + CASE WHEN a.nome_mae = b.nome_mae THEN 10 ELSE 0 END
    + (UTL_MATCH.JARO_WINKLER_SIMILARITY(UPPER(a.nome), UPPER(b.nome)) * 0.10) AS peso_obtido,
    UTL_MATCH.JARO_WINKLER_SIMILARITY(UPPER(a.nome), UPPER(b.nome)) AS similaridade_nome
  FROM registro_origem a
  JOIN registro_origem b
    ON b.id > a.id
   AND b.sistema_id <> a.sistema_id
   -- blocking: só compara pares que já têm alguma âncora em comum.
   -- Sem isto o custo seria o produto cartesiano da base inteira.
   AND (
        a.cns = b.cns
     OR a.cpf = b.cpf
     OR a.data_nascimento = b.data_nascimento
     OR SOUNDEX(a.nome) = SOUNDEX(b.nome)
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
  -- pares já decididos saem da fila
  SELECT 1 FROM decisao_identificacao d
   WHERE (d.registro_a_id = p.registro_a_id AND d.registro_b_id = p.registro_b_id)
      OR (d.registro_a_id = p.registro_b_id AND d.registro_b_id = p.registro_a_id)
);

COMMENT ON TABLE vw_candidato_identificacao IS 'Pares candidatos a serem a mesma pessoa, com score explicável campo a campo';
