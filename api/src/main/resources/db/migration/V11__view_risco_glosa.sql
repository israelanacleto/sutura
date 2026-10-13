-- O motor de regras de glosa. Uma linha por guia E regra violada.
--
-- Guia sem problema nenhum não aparece aqui. Guia com três problemas aparece três vezes.
-- Quem consome soma, agrupa e ordena — a view não decide apresentação.
--
-- ---------------------------------------------------------------------------
-- TRÊS DECISÕES QUE VALE EXPLICAR
-- ---------------------------------------------------------------------------
--
-- 1. UNION ALL em vez de um bloco de CASE WHEN.
--    A V8 mostrou, com plano de execução, que o otimizador do Oracle lida muito melhor
--    com ramos separados do que com condições alternativas no mesmo predicado — foi o que
--    fez a consulta de candidatos cair de 47,5 s para 241 ms. O mesmo raciocínio vale aqui:
--    cada regra é um ramo que o otimizador resolve com o índice que lhe convém.
--
-- 2. Os registros com par pendente são calculados UMA vez, no CTE `pendentes`.
--    A alternativa óbvia — um EXISTS contra vw_candidato_identificacao dentro da regra —
--    reexecutaria o motor de identificação inteiro para cada guia. Com 14 guias ninguém
--    nota. Com 14 mil, é o mesmo erro que a V8 corrigiu.
--
-- 3. A view carrega a EVIDÊNCIA, não só o veredito.
--    `valor_na_guia` e `valor_no_cadastro` viajam junto com o alerta. Assim a tela mostra
--    a comparação campo a campo sem uma segunda consulta, e o endpoint de correção sabe
--    exatamente o que gravar. É o mesmo princípio da tela de identificação: nunca dizer
--    "está errado" sem mostrar lado a lado o que se comparou.

CREATE OR REPLACE VIEW vw_risco_glosa AS
WITH base AS (
  SELECT g.id                  AS guia_id,
         g.numero_guia,
         g.registro_origem_id,
         g.procedimento,
         g.convenio,
         g.valor,
         g.data_emissao,
         g.status,
         g.nome_paciente,
         g.cns_paciente,
         g.carteirinha,
         g.nascimento_paciente,
         v.paciente_mestre_id,
         p.nome_canonico,
         p.cns             AS cns_cadastro,
         p.carteirinha     AS carteirinha_cadastro,
         p.data_nascimento AS nascimento_cadastro,
         -- Mesma normalização da V8: acento removido por TRANSLATE (CONVERT US7ASCII foi
         -- tentado na V4 e produziu '?' no lugar da letra), caixa alta, espaço duplicado
         -- colapsado. Nenhuma regra de comparação nova é inventada nesta fase.
         REGEXP_REPLACE(TRIM(UPPER(TRANSLATE(g.nome_paciente,
           'ÁÀÂÃÄÉÈÊËÍÌÎÏÓÒÔÕÖÚÙÛÜÇÑáàâãäéèêëíìîïóòôõöúùûüçñ',
           'AAAAAEEEEIIIIOOOOOUUUUCNaaaaaeeeeiiiiooooouuuucn'))), ' {2,}', ' ')
                           AS nome_guia_comparavel,
         REGEXP_REPLACE(TRIM(UPPER(TRANSLATE(p.nome_canonico,
           'ÁÀÂÃÄÉÈÊËÍÌÎÏÓÒÔÕÖÚÙÛÜÇÑáàâãäéèêëíìîïóòôõöúùûüçñ',
           'AAAAAEEEEIIIIOOOOOUUUUCNaaaaaeeeeiiiiooooouuuucn'))), ' {2,}', ' ')
                           AS nome_cadastro_comparavel
    FROM guia_faturamento g
    LEFT JOIN vinculo_registro v ON v.registro_origem_id = g.registro_origem_id
    LEFT JOIN paciente_mestre  p ON p.id = v.paciente_mestre_id
),
-- Todo registro que ainda tem par não resolvido na fila de identificação.
-- UNION, e não UNION ALL: um registro que aparece nos dois lados de pares diferentes
-- precisa constar uma vez só.
pendentes AS (
  SELECT registro_a_id AS registro_id FROM vw_candidato_identificacao
  UNION
  SELECT registro_b_id                FROM vw_candidato_identificacao
)

-- 1. Sem cadastro unificado não há contra o que validar. Nenhuma outra regra é avaliada
--    para esta guia — seria ruído: o problema está uma camada abaixo.
SELECT b.guia_id, b.numero_guia, b.registro_origem_id, b.paciente_mestre_id,
       b.nome_paciente, b.convenio, b.procedimento, b.valor, b.data_emissao, b.status,
       r.codigo AS codigo_regra, r.descricao, r.orientacao, r.severidade,
       CASE r.severidade WHEN 'ALTA' THEN 1 WHEN 'MEDIA' THEN 2 ELSE 3 END AS ordem_severidade,
       CAST(NULL AS VARCHAR2(40))  AS campo,
       CAST(NULL AS VARCHAR2(200)) AS valor_na_guia,
       CAST(NULL AS VARCHAR2(200)) AS valor_no_cadastro,
       CAST(NULL AS NUMBER)        AS similaridade_nome
  FROM base b
  JOIN regra_glosa r ON r.codigo = 'PACIENTE_NAO_COSTURADO' AND r.ativo = 1
 WHERE b.paciente_mestre_id IS NULL

UNION ALL

-- 2. Costurado, mas ainda há registro na fila que pode ser a mesma pessoa.
--    Não há o que corrigir na guia: por isso `campo` é nulo. A tela troca o botão de
--    correção por um link para a fila de identificação.
SELECT b.guia_id, b.numero_guia, b.registro_origem_id, b.paciente_mestre_id,
       b.nome_paciente, b.convenio, b.procedimento, b.valor, b.data_emissao, b.status,
       r.codigo, r.descricao, r.orientacao, r.severidade,
       CASE r.severidade WHEN 'ALTA' THEN 1 WHEN 'MEDIA' THEN 2 ELSE 3 END,
       CAST(NULL AS VARCHAR2(40)),
       CAST(NULL AS VARCHAR2(200)),
       CAST(NULL AS VARCHAR2(200)),
       CAST(NULL AS NUMBER)
  FROM base b
  JOIN pendentes pd ON pd.registro_id = b.registro_origem_id
  JOIN regra_glosa r ON r.codigo = 'DUPLICIDADE_PENDENTE' AND r.ativo = 1
 WHERE b.paciente_mestre_id IS NOT NULL

UNION ALL

-- 3. CNS em branco na guia, e o cadastro tem o número. Correção de um clique.
SELECT b.guia_id, b.numero_guia, b.registro_origem_id, b.paciente_mestre_id,
       b.nome_paciente, b.convenio, b.procedimento, b.valor, b.data_emissao, b.status,
       r.codigo, r.descricao, r.orientacao, r.severidade,
       CASE r.severidade WHEN 'ALTA' THEN 1 WHEN 'MEDIA' THEN 2 ELSE 3 END,
       'cns',
       CAST(NULL AS VARCHAR2(200)),
       CAST(b.cns_cadastro AS VARCHAR2(200)),
       CAST(NULL AS NUMBER)
  FROM base b
  JOIN regra_glosa r ON r.codigo = 'CNS_AUSENTE' AND r.ativo = 1
 WHERE b.paciente_mestre_id IS NOT NULL
   AND b.cns_paciente IS NULL
   AND b.cns_cadastro IS NOT NULL

UNION ALL

-- 4. Os dois lados têm CNS e eles diferem. Diferente de ausente: aqui pode ser guia
--    emitida para outra pessoa, e a correção automática seria perigosa.
SELECT b.guia_id, b.numero_guia, b.registro_origem_id, b.paciente_mestre_id,
       b.nome_paciente, b.convenio, b.procedimento, b.valor, b.data_emissao, b.status,
       r.codigo, r.descricao, r.orientacao, r.severidade,
       CASE r.severidade WHEN 'ALTA' THEN 1 WHEN 'MEDIA' THEN 2 ELSE 3 END,
       'cns',
       CAST(b.cns_paciente AS VARCHAR2(200)),
       CAST(b.cns_cadastro AS VARCHAR2(200)),
       CAST(NULL AS NUMBER)
  FROM base b
  JOIN regra_glosa r ON r.codigo = 'CNS_DIVERGENTE' AND r.ativo = 1
 WHERE b.cns_paciente IS NOT NULL
   AND b.cns_cadastro IS NOT NULL
   AND b.cns_paciente <> b.cns_cadastro

UNION ALL

-- 5. Carteirinha divergente.
SELECT b.guia_id, b.numero_guia, b.registro_origem_id, b.paciente_mestre_id,
       b.nome_paciente, b.convenio, b.procedimento, b.valor, b.data_emissao, b.status,
       r.codigo, r.descricao, r.orientacao, r.severidade,
       CASE r.severidade WHEN 'ALTA' THEN 1 WHEN 'MEDIA' THEN 2 ELSE 3 END,
       'carteirinha',
       CAST(b.carteirinha AS VARCHAR2(200)),
       CAST(b.carteirinha_cadastro AS VARCHAR2(200)),
       CAST(NULL AS NUMBER)
  FROM base b
  JOIN regra_glosa r ON r.codigo = 'CARTEIRINHA_DIVERGENTE' AND r.ativo = 1
 WHERE b.carteirinha IS NOT NULL
   AND b.carteirinha_cadastro IS NOT NULL
   AND b.carteirinha <> b.carteirinha_cadastro

UNION ALL

-- 6. Data de nascimento divergente. Formatada como texto para caber na mesma coluna das
--    outras evidências — quem lê a view compara strings, não datas.
SELECT b.guia_id, b.numero_guia, b.registro_origem_id, b.paciente_mestre_id,
       b.nome_paciente, b.convenio, b.procedimento, b.valor, b.data_emissao, b.status,
       r.codigo, r.descricao, r.orientacao, r.severidade,
       CASE r.severidade WHEN 'ALTA' THEN 1 WHEN 'MEDIA' THEN 2 ELSE 3 END,
       'nascimento',
       CAST(TO_CHAR(b.nascimento_paciente, 'DD/MM/YYYY') AS VARCHAR2(200)),
       CAST(TO_CHAR(b.nascimento_cadastro, 'DD/MM/YYYY') AS VARCHAR2(200)),
       CAST(NULL AS NUMBER)
  FROM base b
  JOIN regra_glosa r ON r.codigo = 'NASCIMENTO_DIVERGENTE' AND r.ativo = 1
 WHERE b.nascimento_paciente IS NOT NULL
   AND b.nascimento_cadastro IS NOT NULL
   AND b.nascimento_paciente <> b.nascimento_cadastro

UNION ALL

-- 7. Nome divergente.
--
--    Não há limiar de Jaro-Winkler aqui, e isso é deliberado. Medido contra o seed, o
--    escore se separa em três grupos limpos: 49 para nome abreviado ('M. A. SOUZA'), 96
--    para variação de grafia ('MARIA APARECIDA DE SOUZA') e 100 para idêntico. Qualquer
--    corte entre 50 e 96 classificaria exatamente os mesmos casos — ou seja, o número
--    escolhido seria arbitrário e daria falsa impressão de calibragem.
--
--    Então a regra dispara para QUALQUER diferença após normalizar, e o escore viaja junto
--    em `similaridade_nome`. 49 e 96 são problemas de natureza diferente: um é abreviação,
--    o outro é grafia. Quem opera precisa enxergar essa diferença para priorizar, e um
--    limiar inventado jogaria justamente essa informação fora.
SELECT b.guia_id, b.numero_guia, b.registro_origem_id, b.paciente_mestre_id,
       b.nome_paciente, b.convenio, b.procedimento, b.valor, b.data_emissao, b.status,
       r.codigo, r.descricao, r.orientacao, r.severidade,
       CASE r.severidade WHEN 'ALTA' THEN 1 WHEN 'MEDIA' THEN 2 ELSE 3 END,
       'nome',
       CAST(b.nome_paciente AS VARCHAR2(200)),
       CAST(b.nome_canonico AS VARCHAR2(200)),
       UTL_MATCH.JARO_WINKLER_SIMILARITY(b.nome_guia_comparavel, b.nome_cadastro_comparavel)
  FROM base b
  JOIN regra_glosa r ON r.codigo = 'NOME_DIVERGENTE' AND r.ativo = 1
 WHERE b.paciente_mestre_id IS NOT NULL
   AND b.nome_guia_comparavel <> b.nome_cadastro_comparavel;

COMMENT ON TABLE vw_risco_glosa IS 'Uma linha por guia e regra violada, com a evidência da comparação. Guia sem problema não aparece.';
