-- Índices para chaves estrangeiras que não tinham nenhum.
--
-- Contexto honesto: estes índices foram criados enquanto se investigava um
-- ORA-12860 no script de reset da demonstração, e NÃO resolveram aquele problema
-- — o que resolveu foi fechar a transação entre os passos do script. A causa raiz
-- do ORA-12860 não chegou a ser confirmada; ver o comentário em
-- scripts/reset-demo.sql, que separa o observado da hipótese.
--
-- Ainda assim eles ficam, porque o problema que resolvem é real e independente:
-- no Oracle, apagar uma linha da tabela pai exige verificar se há filhas
-- referenciando. Sem índice na coluna da FK, o banco não localiza essas linhas por
-- índice e toma um bloqueio mais abrangente na tabela filha. Em um sistema com uso
-- concorrente isso vira contenção — e é um dos motivos clássicos de lentidão
-- inexplicável em bases Oracle.
--
-- Duas colunas estavam descobertas:
--
--   decisao_identificacao.registro_b_id
--     A constraint UNIQUE (registro_a_id, registro_b_id) cria um índice composto, mas
--     ele só serve de caminho de acesso para registro_a_id, que é a coluna líder. Para
--     registro_b_id não há índice utilizável.
--
--   registro_origem.sistema_id
--     Sem índice nenhum. Hoje não se apaga sistema de origem, mas a armadilha estaria
--     armada no dia em que isso passasse a acontecer.

CREATE INDEX ix_decisao_registro_b ON decisao_identificacao (registro_b_id);
CREATE INDEX ix_registro_sistema ON registro_origem (sistema_id);
