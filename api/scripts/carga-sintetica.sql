-- Carga sintética para medir o motor de identificação em volume.
--
-- Responde com número, e não com opinião, a pergunta "isso escala?".
--
-- ---------------------------------------------------------------------------
-- ISOLAMENTO
-- ---------------------------------------------------------------------------
-- Nada aqui toca as tabelas da aplicação. Os dados vão para carga_registro_origem,
-- que replica a estrutura e os índices de registro_origem. A demonstração continua
-- vendo apenas seus 15 registros, e a tela de Conexões não muda.
--
-- Para desfazer tudo: scripts/carga-sintetica-limpar.sql
--
-- ---------------------------------------------------------------------------
-- COMO OS DADOS SÃO GERADOS
-- ---------------------------------------------------------------------------
-- Registro sintético só serve se estressar o motor do jeito que dado real estressa.
-- Cada "pessoa" gera de 1 a 3 registros, em sistemas diferentes, com as deformações
-- que aparecem na prática:
--
--   · nome abreviado          MARIA APARECIDA SOUZA -> M. A. SOUZA
--   · acento perdido          CONCEIÇÃO -> CONCEICAO
--   · CPF ausente             comum em cadastro de laboratório
--   · CNS ausente             comum em sistema legado
--
-- E, de propósito, o nome se repete entre pessoas diferentes — homônimos existem, e
-- é justamente com eles que o motor precisa não errar.

-- =========================================================================
-- 1. Pools de nomes
-- =========================================================================

DROP TABLE carga_nome PURGE;

CREATE TABLE carga_nome (
  tipo    VARCHAR2(10) NOT NULL,
  posicao NUMBER       NOT NULL,
  valor   VARCHAR2(40) NOT NULL
);

INSERT INTO carga_nome (tipo, posicao, valor)
SELECT 'PRENOME', ROWNUM - 1, coluna FROM (
  SELECT 'MARIA' coluna FROM dual UNION ALL SELECT 'JOSE' FROM dual UNION ALL
  SELECT 'ANA' FROM dual UNION ALL SELECT 'JOAO' FROM dual UNION ALL
  SELECT 'ANTONIO' FROM dual UNION ALL SELECT 'FRANCISCA' FROM dual UNION ALL
  SELECT 'CARLOS' FROM dual UNION ALL SELECT 'PAULO' FROM dual UNION ALL
  SELECT 'PEDRO' FROM dual UNION ALL SELECT 'LUCAS' FROM dual UNION ALL
  SELECT 'LUIZ' FROM dual UNION ALL SELECT 'MARCOS' FROM dual UNION ALL
  SELECT 'LUIS' FROM dual UNION ALL SELECT 'GABRIEL' FROM dual UNION ALL
  SELECT 'RAFAEL' FROM dual UNION ALL SELECT 'DANIEL' FROM dual UNION ALL
  SELECT 'MARCELO' FROM dual UNION ALL SELECT 'BRUNO' FROM dual UNION ALL
  SELECT 'EDUARDO' FROM dual UNION ALL SELECT 'FELIPE' FROM dual UNION ALL
  SELECT 'RAIMUNDO' FROM dual UNION ALL SELECT 'RODRIGO' FROM dual UNION ALL
  SELECT 'MANOEL' FROM dual UNION ALL SELECT 'THIAGO' FROM dual UNION ALL
  SELECT 'JULIANA' FROM dual UNION ALL SELECT 'PATRICIA' FROM dual UNION ALL
  SELECT 'ADRIANA' FROM dual UNION ALL SELECT 'FERNANDA' FROM dual UNION ALL
  SELECT 'SANDRA' FROM dual UNION ALL SELECT 'CAMILA' FROM dual UNION ALL
  SELECT 'AMANDA' FROM dual UNION ALL SELECT 'BRUNA' FROM dual UNION ALL
  SELECT 'JESSICA' FROM dual UNION ALL SELECT 'LETICIA' FROM dual UNION ALL
  SELECT 'VANESSA' FROM dual UNION ALL SELECT 'MARCIA' FROM dual UNION ALL
  SELECT 'KELLY' FROM dual UNION ALL SELECT 'TEREZA' FROM dual UNION ALL
  SELECT 'SEBASTIAO' FROM dual UNION ALL SELECT 'ROBERTO' FROM dual
);

INSERT INTO carga_nome (tipo, posicao, valor)
SELECT 'SOBRENOME', ROWNUM - 1, coluna FROM (
  SELECT 'SILVA' coluna FROM dual UNION ALL SELECT 'SANTOS' FROM dual UNION ALL
  SELECT 'OLIVEIRA' FROM dual UNION ALL SELECT 'SOUZA' FROM dual UNION ALL
  SELECT 'RODRIGUES' FROM dual UNION ALL SELECT 'FERREIRA' FROM dual UNION ALL
  SELECT 'ALVES' FROM dual UNION ALL SELECT 'PEREIRA' FROM dual UNION ALL
  SELECT 'LIMA' FROM dual UNION ALL SELECT 'GOMES' FROM dual UNION ALL
  SELECT 'COSTA' FROM dual UNION ALL SELECT 'RIBEIRO' FROM dual UNION ALL
  SELECT 'MARTINS' FROM dual UNION ALL SELECT 'CARVALHO' FROM dual UNION ALL
  SELECT 'ALMEIDA' FROM dual UNION ALL SELECT 'LOPES' FROM dual UNION ALL
  SELECT 'SOARES' FROM dual UNION ALL SELECT 'FERNANDES' FROM dual UNION ALL
  SELECT 'VIEIRA' FROM dual UNION ALL SELECT 'BARBOSA' FROM dual UNION ALL
  SELECT 'ROCHA' FROM dual UNION ALL SELECT 'DIAS' FROM dual UNION ALL
  SELECT 'NASCIMENTO' FROM dual UNION ALL SELECT 'MOREIRA' FROM dual UNION ALL
  SELECT 'NUNES' FROM dual UNION ALL SELECT 'MENDES' FROM dual UNION ALL
  SELECT 'FREITAS' FROM dual UNION ALL SELECT 'CARDOSO' FROM dual UNION ALL
  SELECT 'RAMOS' FROM dual UNION ALL SELECT 'ARAUJO' FROM dual
);

COMMIT;

-- =========================================================================
-- 2. Tabela de carga, espelhando registro_origem
-- =========================================================================

DROP TABLE carga_registro_origem PURGE;

CREATE TABLE carga_registro_origem (
  id                   NUMBER        NOT NULL,
  sistema_id           NUMBER        NOT NULL,
  identificador_origem VARCHAR2(80)  NOT NULL,
  nome                 VARCHAR2(200) NOT NULL,
  nome_mae             VARCHAR2(200),
  cns                  VARCHAR2(20),
  cpf                  VARCHAR2(14),
  data_nascimento      DATE,
  pessoa_id            NUMBER        NOT NULL,
  CONSTRAINT pk_carga_registro PRIMARY KEY (id)
);

-- Os mesmos índices de registro_origem, para a medição valer alguma coisa.
CREATE INDEX ix_carga_cns ON carga_registro_origem (cns);
CREATE INDEX ix_carga_cpf ON carga_registro_origem (cpf);
CREATE INDEX ix_carga_nascimento ON carga_registro_origem (data_nascimento);

-- pessoa_id não existe em registro_origem: é o gabarito. Guarda quem é quem de
-- verdade, para medir depois se o motor acertou — e nunca entra na comparação.
CREATE INDEX ix_carga_pessoa ON carga_registro_origem (pessoa_id);

-- =========================================================================
-- 3. Geração
-- =========================================================================
-- Troque 2000 pela quantidade de PESSOAS desejada. Cada pessoa vira de 1 a 3
-- registros, então a tabela fica com cerca de 2x esse número.

INSERT INTO carga_registro_origem
  (id, sistema_id, identificador_origem, nome, nome_mae, cns, cpf, data_nascimento, pessoa_id)
WITH pessoas AS (
  SELECT LEVEL AS p FROM dual CONNECT BY LEVEL <= 2000
),
base AS (
  SELECT p,
         pr.valor  AS prenome,
         s1.valor  AS sobrenome1,
         s2.valor  AS sobrenome2,
         mm.valor  AS mae_prenome,
         DATE '1940-01-01' + MOD(p * 97, 25000)                     AS nascimento,
         CASE WHEN MOD(p, 7) = 0 THEN NULL
              ELSE LPAD(TO_CHAR(700000000000000 + p), 15, '0') END  AS cns,
         CASE WHEN MOD(p, 4) = 0 THEN NULL
              ELSE LPAD(TO_CHAR(MOD(p * 7919, 100000000000)), 11, '0') END AS cpf
    FROM pessoas
    JOIN carga_nome pr ON pr.tipo = 'PRENOME'   AND pr.posicao = MOD(p * 7,  40)
    JOIN carga_nome s1 ON s1.tipo = 'SOBRENOME' AND s1.posicao = MOD(p * 13, 30)
    JOIN carga_nome s2 ON s2.tipo = 'SOBRENOME' AND s2.posicao = MOD(p * 29, 30)
    JOIN carga_nome mm ON mm.tipo = 'PRENOME'   AND mm.posicao = MOD(p * 11, 40)
),
variacoes AS (
  SELECT LEVEL AS v FROM dual CONNECT BY LEVEL <= 3
)
SELECT
  (b.p - 1) * 3 + v.v                                            AS id,
  v.v                                                            AS sistema_id,
  CASE v.v WHEN 1 THEN 'Prontuário ' WHEN 2 THEN 'Cadastro '
           ELSE 'Requisição ' END || TO_CHAR(b.p)                AS identificador_origem,
  CASE v.v
    -- sistema 1: o cadastro completo
    WHEN 1 THEN b.prenome || ' ' || b.sobrenome1 || ' ' || b.sobrenome2
    -- sistema 2: nome abreviado, como cadastro de clínica costuma gravar
    WHEN 2 THEN SUBSTR(b.prenome, 1, 1) || '. ' || b.sobrenome2
    -- sistema 3: sobrenomes invertidos, erro comum de digitação
    ELSE b.prenome || ' ' || b.sobrenome2 || ' ' || b.sobrenome1
  END                                                            AS nome,
  CASE WHEN v.v = 2 THEN NULL
       ELSE b.mae_prenome || ' ' || b.sobrenome1 END              AS nome_mae,
  b.cns                                                          AS cns,
  -- laboratório costuma não ter CPF
  CASE WHEN v.v = 3 THEN NULL ELSE b.cpf END                      AS cpf,
  b.nascimento                                                   AS data_nascimento,
  b.p                                                            AS pessoa_id
FROM base b
CROSS JOIN variacoes v
WHERE v.v <= CASE WHEN MOD(b.p, 3) = 0 THEN 3
                  WHEN MOD(b.p, 3) = 1 THEN 1
                  ELSE 2 END;

COMMIT;

-- Estatísticas: sem isso o otimizador decide no escuro e a medição não vale nada.
BEGIN DBMS_STATS.GATHER_TABLE_STATS(USER, 'CARGA_REGISTRO_ORIGEM'); END;
