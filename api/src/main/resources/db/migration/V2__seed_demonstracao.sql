-- Dados de demonstração. Fictícios, com formato válido (CNS de 15 dígitos, CPF de 11).
-- Nenhum dado de pessoa real.
--
-- Migra o que hoje está fixo em web/src/app/core/mock-data.ts.
--
-- Decisão de montagem importante: a Maria Aparecida entra com TRÊS registros de origem,
-- mas o paciente mestre nasce vinculado APENAS ao registro do MV. Os vínculos com o Tasy
-- e com o laboratório são o que a demonstração costura ao vivo — e a linha do tempo cresce
-- de 4 para 12 eventos na frente de quem está assistindo.

-- ---------------------------------------------------------------- sistemas de origem

INSERT INTO sistema_origem (id, codigo, nome, fornecedor, unidade, protocolo, status, ultima_sync, observacao)
VALUES (1, 'MV', 'MV SOUL', 'MV Sistemas', 'Hospital Santa Clara', 'REST + HL7 v2', 'CONECTADO', SYSTIMESTAMP, 'Prontuário, agenda e faturamento sincronizados.');

INSERT INTO sistema_origem (id, codigo, nome, fornecedor, unidade, protocolo, status, ultima_sync, observacao)
VALUES (2, 'TASY', 'Philips Tasy', 'Philips', 'Clínica OncoVida', 'REST + FHIR R4', 'CONECTADO', SYSTIMESTAMP, 'Quimioterapia e prescrições sincronizadas.');

INSERT INTO sistema_origem (id, codigo, nome, fornecedor, unidade, protocolo, status, ultima_sync, observacao)
VALUES (3, 'LAB', 'Lab Alpha (LIS)', 'Alpha Diagnósticos', 'Rede laboratorial', 'HL7 v2 (ORU)', 'CONECTADO', SYSTIMESTAMP, 'Resultados liberados em até 15 min.');

INSERT INTO sistema_origem (id, codigo, nome, fornecedor, unidade, protocolo, status, ultima_sync, observacao)
VALUES (4, 'LEGADO', 'SGH Legado', 'Interno (planilha)', 'Faturamento', 'Importação CSV manual', 'ATENCAO', SYSTIMESTAMP - 1, '3 linhas rejeitadas por layout fora do padrão.');

-- ---------------------------------------------------------------- registros de origem

-- Maria Aparecida Souza — a mesma pessoa, três grafias, três sistemas
INSERT INTO registro_origem (id, sistema_id, identificador_origem, nome, nome_mae, cns, cpf, data_nascimento, sexo)
VALUES (1, 1, 'Prontuário 448211', 'MARIA APARECIDA SOUZA', 'BENEDITA SOUZA', '708004288150003', '31748590211', DATE '1964-04-12', 'F');

INSERT INTO registro_origem (id, sistema_id, identificador_origem, nome, nome_mae, cns, cpf, data_nascimento, sexo)
VALUES (2, 2, 'Cadastro 90233', 'M. A. SOUZA', 'BENEDITA SOUZA', '708004288150003', NULL, DATE '1964-04-12', 'F');

INSERT INTO registro_origem (id, sistema_id, identificador_origem, nome, nome_mae, cns, cpf, data_nascimento, sexo)
VALUES (3, 3, 'Requisição 77120', 'MARIA APARECIDA DE SOUZA', 'BENEDITA SOUZA', '708004288150003', '31748590211', DATE '1964-04-12', 'F');

-- João Carlos Ferreira — homônimos que NÃO são a mesma pessoa.
-- Nome idêntico, e todo o resto diverge. É o caso que prova que o motor não é ingênuo.
INSERT INTO registro_origem (id, sistema_id, identificador_origem, nome, nome_mae, cns, cpf, data_nascimento, sexo)
VALUES (4, 1, 'Prontuário 210984', 'JOÃO CARLOS FERREIRA', 'ROSA FERREIRA', '898001177420001', '40522911830', DATE '1971-09-03', 'M');

INSERT INTO registro_origem (id, sistema_id, identificador_origem, nome, nome_mae, cns, cpf, data_nascimento, sexo)
VALUES (5, 2, 'Cadastro 88410', 'JOÃO CARLOS FERREIRA', 'IVONE FERREIRA', '702553311800007', '98166420752', DATE '1989-01-28', 'M');

-- Ana Beatriz Lima — nome abreviado na origem laboratorial
INSERT INTO registro_origem (id, sistema_id, identificador_origem, nome, nome_mae, cns, cpf, data_nascimento, sexo)
VALUES (6, 2, 'Cadastro 91007', 'ANA BEATRIZ LIMA', NULL, '706441099230004', '22874061509', DATE '1982-11-30', 'F');

INSERT INTO registro_origem (id, sistema_id, identificador_origem, nome, nome_mae, cns, cpf, data_nascimento, sexo)
VALUES (7, 3, 'Requisição 77455', 'ANA B. LIMA', NULL, '706441099230004', NULL, DATE '1982-11-30', 'F');

-- Roberto Nascimento — pouca evidência comparável: sem CNS dos dois lados.
-- O motor não tem como decidir sozinho; vai para revisão humana pela regra de evidência.
INSERT INTO registro_origem (id, sistema_id, identificador_origem, nome, nome_mae, cns, cpf, data_nascimento, sexo)
VALUES (8, 1, 'Prontuário 331902', 'ROBERTO NASCIMENTO', NULL, NULL, '15088344720', DATE '1958-07-07', 'M');

INSERT INTO registro_origem (id, sistema_id, identificador_origem, nome, nome_mae, cns, cpf, data_nascimento, sexo)
VALUES (9, 4, 'Linha CSV 1.204', 'ROBERTO NASCIMENTO', NULL, NULL, NULL, DATE '1958-07-07', 'M');

-- Carlos Eduardo Prado — sobrenomes invertidos entre os sistemas
INSERT INTO registro_origem (id, sistema_id, identificador_origem, nome, nome_mae, cns, cpf, data_nascimento, sexo)
VALUES (10, 1, 'Prontuário 452880', 'CARLOS EDUARDO PRADO', NULL, '704118833900002', '62230198544', DATE '1977-02-19', 'M');

INSERT INTO registro_origem (id, sistema_id, identificador_origem, nome, nome_mae, cns, cpf, data_nascimento, sexo)
VALUES (11, 2, 'Cadastro 91882', 'CARLOS PRADO EDUARDO', NULL, '704118833900002', '62230198544', DATE '1977-02-19', 'M');

-- Tereza Cristina Moura — iniciais no laboratório
INSERT INTO registro_origem (id, sistema_id, identificador_origem, nome, nome_mae, cns, cpf, data_nascimento, sexo)
VALUES (12, 1, 'Prontuário 449017', 'TEREZA CRISTINA MOURA', NULL, '709332044710008', '80411533067', DATE '1969-06-25', 'F');

INSERT INTO registro_origem (id, sistema_id, identificador_origem, nome, nome_mae, cns, cpf, data_nascimento, sexo)
VALUES (13, 3, 'Requisição 77902', 'T. C. MOURA', NULL, '709332044710008', NULL, DATE '1969-06-25', 'F');

-- Estes dois existem APENAS no MV. O par de cada um chega pela ingestão FHIR durante a
-- demonstração — é o que faz aparecerem candidatos novos na fila ao vivo.
INSERT INTO registro_origem (id, sistema_id, identificador_origem, nome, nome_mae, cns, cpf, data_nascimento, sexo)
VALUES (14, 1, 'Prontuário 453001', 'SEBASTIÃO ROCHA MARTINS', 'CONCEIÇÃO ROCHA', '705229944180006', '73920184455', DATE '1953-11-08', 'M');

INSERT INTO registro_origem (id, sistema_id, identificador_origem, nome, nome_mae, cns, cpf, data_nascimento, sexo)
VALUES (15, 1, 'Prontuário 453118', 'LÚCIA HELENA BARROS', 'MARIA BARROS', '701883350020009', '48277361190', DATE '1988-05-21', 'F');

-- ---------------------------------------------------------------- paciente mestre

INSERT INTO paciente_mestre (id, nome_canonico, cns, cpf, data_nascimento, convenio, carteirinha, diagnostico)
VALUES (1, 'Maria Aparecida Souza', '708004288150003', '31748590211', DATE '1964-04-12',
        'Saúde Meridiano', '0041882733900 5', 'Neoplasia de mama HER2+ — protocolo de 8 ciclos');

-- Só o registro do MV nasce costurado. Tasy e laboratório são costurados na demonstração.
INSERT INTO vinculo_registro (id, paciente_mestre_id, registro_origem_id) VALUES (1, 1, 1);

-- ---------------------------------------------------------------- eventos clínicos

-- Origem MV — o hospital: diagnóstico, cirurgia e acompanhamento
INSERT INTO evento_clinico (id, registro_origem_id, data_evento, categoria, titulo, detalhe, ciclo)
VALUES (1, 1, DATE '2026-02-20', 'EXAME', 'Mamografia bilateral', 'BI-RADS 5 — encaminhada para biópsia.', NULL);
INSERT INTO evento_clinico (id, registro_origem_id, data_evento, categoria, titulo, detalhe, ciclo)
VALUES (2, 1, DATE '2026-03-15', 'CIRURGIA', 'Mastectomia parcial', 'Procedimento sem intercorrências. Alta em 48 h.', NULL);
INSERT INTO evento_clinico (id, registro_origem_id, data_evento, categoria, titulo, detalhe, ciclo)
VALUES (3, 1, DATE '2026-05-08', 'EXAME', 'Ecocardiograma', 'Fração de ejeção 62% — dentro do previsto para o protocolo.', NULL);
INSERT INTO evento_clinico (id, registro_origem_id, data_evento, categoria, titulo, detalhe, ciclo)
VALUES (4, 1, DATE '2026-07-02', 'CONSULTA', 'Consulta de oncologia clínica', 'Resposta parcial ao tratamento. Manter protocolo.', NULL);

-- Origem Tasy — a clínica de oncologia: as infusões, exatos 28 dias entre elas.
-- É este padrão que hoje se perde: cada aplicação vira um atendimento isolado.
INSERT INTO evento_clinico (id, registro_origem_id, data_evento, categoria, titulo, detalhe, ciclo)
VALUES (5, 2, DATE '2026-03-27', 'INFUSAO', 'Infusão de Trastuzumabe 440 mg', 'Primeira dose de ataque. Boa tolerância.', 'Ciclo 1 de 8');
INSERT INTO evento_clinico (id, registro_origem_id, data_evento, categoria, titulo, detalhe, ciclo)
VALUES (6, 2, DATE '2026-04-24', 'INFUSAO', 'Infusão de Trastuzumabe 440 mg', 'Sem intercorrências.', 'Ciclo 2 de 8');
INSERT INTO evento_clinico (id, registro_origem_id, data_evento, categoria, titulo, detalhe, ciclo)
VALUES (7, 2, DATE '2026-05-22', 'INFUSAO', 'Infusão de Trastuzumabe 440 mg', 'Sem intercorrências.', 'Ciclo 3 de 8');
INSERT INTO evento_clinico (id, registro_origem_id, data_evento, categoria, titulo, detalhe, ciclo)
VALUES (8, 2, DATE '2026-06-19', 'INFUSAO', 'Infusão de Trastuzumabe 440 mg', 'Sem intercorrências.', 'Ciclo 4 de 8');
INSERT INTO evento_clinico (id, registro_origem_id, data_evento, categoria, titulo, detalhe, ciclo)
VALUES (9, 2, DATE '2026-07-17', 'INFUSAO', 'Infusão de Trastuzumabe 440 mg', 'Sem intercorrências.', 'Ciclo 5 de 8');
INSERT INTO evento_clinico (id, registro_origem_id, data_evento, categoria, titulo, detalhe, ciclo)
VALUES (10, 2, DATE '2026-08-14', 'INFUSAO', 'Infusão de Trastuzumabe 440 mg', 'Sem intercorrências. Próximo ciclo previsto em 28 dias.', 'Ciclo 6 de 8');

-- Origem laboratório
INSERT INTO evento_clinico (id, registro_origem_id, data_evento, categoria, titulo, detalhe, ciclo)
VALUES (11, 3, DATE '2026-02-28', 'EXAME', 'Biópsia — imuno-histoquímica', 'HER2 positivo (3+). Base da indicação do protocolo.', NULL);
INSERT INTO evento_clinico (id, registro_origem_id, data_evento, categoria, titulo, detalhe, ciclo)
VALUES (12, 3, DATE '2026-08-07', 'EXAME', 'Hemograma completo', 'Neutrófilos 2.100/mm³ — liberada para o próximo ciclo.', NULL);

COMMIT;

-- Os ids acima foram informados explicitamente para manter as referências legíveis.
-- Isto deixa as sequências de IDENTITY para trás, então elas são realinhadas aqui —
-- sem isso, o primeiro INSERT vindo da API colidiria com um id já usado.
ALTER TABLE sistema_origem        MODIFY (id GENERATED BY DEFAULT AS IDENTITY (START WITH LIMIT VALUE));
ALTER TABLE registro_origem       MODIFY (id GENERATED BY DEFAULT AS IDENTITY (START WITH LIMIT VALUE));
ALTER TABLE paciente_mestre       MODIFY (id GENERATED BY DEFAULT AS IDENTITY (START WITH LIMIT VALUE));
ALTER TABLE vinculo_registro      MODIFY (id GENERATED BY DEFAULT AS IDENTITY (START WITH LIMIT VALUE));
ALTER TABLE evento_clinico        MODIFY (id GENERATED BY DEFAULT AS IDENTITY (START WITH LIMIT VALUE));
ALTER TABLE decisao_identificacao MODIFY (id GENERATED BY DEFAULT AS IDENTITY (START WITH LIMIT VALUE));
