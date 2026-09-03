-- Remove tudo que a carga sintética criou.
--
-- Seguro para rodar a qualquer momento: nenhuma dessas tabelas é usada pela
-- aplicação. Elas existem só para medir o motor de identificação em volume, e
-- ficam ocupando espaço à toa depois que a medição termina.

DROP TABLE carga_registro_origem PURGE;
DROP TABLE carga_nome PURGE;

-- Conferência: as duas devem sumir.
SELECT table_name FROM user_tables WHERE table_name LIKE 'CARGA%';
