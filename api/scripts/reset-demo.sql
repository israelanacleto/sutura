-- Restaura o estado inicial da demonstração.
--
-- NÃO derruba o schema: apaga apenas o que uma sessão de demonstração produz. É
-- deliberadamente diferente de "flyway:clean", que destrói tudo e obriga a rodar as
-- migrations de novo — aqui as tabelas, os índices e a view permanecem intactos.
--
-- Depois de rodar, o estado volta a ser:
--   · Maria Aparecida vinculada APENAS ao registro do MV (4 eventos na linha do tempo)
--   · nenhuma decisão de identificação registrada
--   · os registros vindos de ingestão FHIR removidos, para que o botão "Sincronizar
--     agora" volte a produzir pares novos
--
-- Uso:
--   sql ADMIN/senha@suturadb_high @reset-demo.sql
-- ou cole no SQL Worksheet do console OCI (Database actions > SQL).

-- Os COMMIT entre os passos NÃO são decorativos.
--
-- decisao_identificacao tem duas chaves estrangeiras para a mesma tabela pai
-- (registro_a_id e registro_b_id). Apagando as filhas e as pais na MESMA transação, o
-- Oracle precisa travar as entradas de índice das duas constraints para as mesmas linhas
-- e acaba esbarrando no próprio bloqueio:
--
--   ORA-12860: deadlock detected while waiting for a sibling row lock
--
-- Não é contenção com a aplicação — acontece igual com tudo parado. Fechar a transação
-- antes de apagar as linhas pai elimina o conflito.

-- 1. Auditoria de decisões
DELETE FROM decisao_identificacao;
COMMIT;

-- 2. Vínculos, menos o que já vem do seed (Maria no MV)
DELETE FROM vinculo_registro WHERE registro_origem_id <> 1;
COMMIT;

-- 3. Eventos que chegaram por ingestão
DELETE FROM evento_clinico
 WHERE registro_origem_id IN (
       SELECT id FROM registro_origem WHERE identificador_origem LIKE 'FHIR/%');
COMMIT;

-- 4. Registros que chegaram por ingestão
DELETE FROM registro_origem WHERE identificador_origem LIKE 'FHIR/%';
COMMIT;

-- 5. Horários de sincronização, para a tela não exibir uma corrida de ontem
UPDATE sistema_origem SET ultima_sync = SYSTIMESTAMP     WHERE codigo <> 'LEGADO';
UPDATE sistema_origem SET ultima_sync = SYSTIMESTAMP - 1 WHERE codigo =  'LEGADO';
COMMIT;

-- Conferência: o esperado é 15 registros, 1 vínculo, 0 decisões e 12 eventos.
SELECT (SELECT COUNT(*) FROM registro_origem)       AS registros,
       (SELECT COUNT(*) FROM vinculo_registro)      AS vinculos,
       (SELECT COUNT(*) FROM decisao_identificacao) AS decisoes,
       (SELECT COUNT(*) FROM evento_clinico)        AS eventos
  FROM dual;
