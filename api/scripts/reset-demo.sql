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

-- Os COMMIT entre os passos NÃO são decorativos. Sem eles, o script falha em:
--
--   ORA-12860: deadlock detected while waiting for a sibling row lock
--
-- O que foi observado, e é verificável repetindo os passos:
--   · o erro ocorre sempre no DELETE das linhas de registro_origem, depois que as tabelas
--     filhas foram esvaziadas na mesma transação;
--   · acontece igualmente com a aplicação parada, então não é contenção com outra sessão;
--   · criar os índices que faltavam nas chaves estrangeiras (migration V7) NÃO resolveu;
--   · fechar a transação antes de apagar as linhas pai resolve.
--
-- A hipótese para a causa — e é hipótese, não diagnóstico: decisao_identificacao tem duas
-- chaves estrangeiras apontando para a mesma tabela pai (registro_a_id e registro_b_id),
-- e resolver as duas na mesma transação em que as filhas foram apagadas leva a sessão a
-- esbarrar no próprio bloqueio. Não confirmamos isso em trace nem com um DBA — chegamos
-- ao COMMIT pelo comportamento observado.
--
-- Se algum dia isso for investigado a sério, o caminho é o trace da sessão e a
-- DBA_BLOCKERS/V$LOCK no momento do erro.

-- 1. Auditoria de decisões
DELETE FROM decisao_identificacao;
COMMIT;

-- 2. Vínculos, menos o que já vem do seed (Maria no MV)
DELETE FROM vinculo_registro WHERE registro_origem_id <> 1;
COMMIT;

-- 3. Pacientes mestres criados durante a demonstração.
--    Costurar dois registros que ainda não pertenciam a ninguém cria um paciente mestre
--    novo. Sem esta limpeza eles sobram como órfãos, um a cada rodada de demonstração.
--    Só o mestre 1 (Maria Aparecida) vem do seed e permanece.
DELETE FROM paciente_mestre WHERE id <> 1;
COMMIT;

-- 4. Eventos que chegaram por ingestão
DELETE FROM evento_clinico
 WHERE registro_origem_id IN (
       SELECT id FROM registro_origem WHERE identificador_origem LIKE 'FHIR/%');
COMMIT;

-- 5. Registros que chegaram por ingestão
DELETE FROM registro_origem WHERE identificador_origem LIKE 'FHIR/%';
COMMIT;

-- 6. Horários de sincronização, para a tela não exibir uma corrida de ontem
UPDATE sistema_origem SET ultima_sync = SYSTIMESTAMP     WHERE codigo <> 'LEGADO';
UPDATE sistema_origem SET ultima_sync = SYSTIMESTAMP - 1 WHERE codigo =  'LEGADO';
COMMIT;

-- Conferência: o esperado é 15 registros, 1 vínculo, 1 paciente mestre, 0 decisões e 12 eventos.
SELECT (SELECT COUNT(*) FROM registro_origem)       AS registros,
       (SELECT COUNT(*) FROM vinculo_registro)      AS vinculos,
       (SELECT COUNT(*) FROM paciente_mestre)       AS mestres,
       (SELECT COUNT(*) FROM decisao_identificacao) AS decisoes,
       (SELECT COUNT(*) FROM evento_clinico)        AS eventos
  FROM dual;
