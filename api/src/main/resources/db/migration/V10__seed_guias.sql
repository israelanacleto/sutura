-- Guias de faturamento para a demonstração. Valores fictícios e plausíveis.
--
-- ---------------------------------------------------------------------------
-- COMO OS PROBLEMAS FORAM DISTRIBUÍDOS
-- ---------------------------------------------------------------------------
-- No estado inicial da demonstração, apenas o registro 1 — Maria no MV — está costurado
-- a um paciente mestre. Isso cria um arco natural:
--
--   ANTES da costura
--     · as guias do registro 1 mostram problemas específicos e acionáveis
--       (CNS ausente, carteirinha divergente) mais o alerta de duplicidade pendente
--     · as demais mostram apenas "paciente não costurado" — não há contra o que validar
--
--   DEPOIS de costurar a Maria na tela de identificação
--     · o alerta de duplicidade some das guias do registro 1, sem ninguém tocar nelas
--     · as guias do Tasy e do laboratório passam a ser validáveis, e revelam as
--       divergências que estavam escondidas: nome abreviado, CNS trocado, data errada
--
-- Ou seja: resolver identidade não só limpa alertas, como descobre problemas que
-- ninguém conseguia ver antes. É o argumento do produto inteiro, em uma tela.

-- ===========================================================================
-- Registro 1 — MARIA APARECIDA SOUZA no MV (o único costurado no início)
-- ===========================================================================

-- Guia correta. Existe para a tela não parecer que tudo está errado.
INSERT INTO guia_faturamento (id, registro_origem_id, evento_clinico_id, numero_guia, procedimento,
       convenio, carteirinha, nome_paciente, cns_paciente, cpf_paciente, nascimento_paciente,
       valor, data_emissao, status)
VALUES (1, 1, 1, 'G-2026-0101', 'Mamografia bilateral',
       'Saúde Meridiano', '0041 8827 3390 05', 'MARIA APARECIDA SOUZA', '708004288150003', '31748590211', DATE '1964-04-12',
       180.00, DATE '2026-02-21', 'PRONTA');

-- CNS ausente, no procedimento de maior valor. É o pior caso: muito dinheiro parado por
-- um campo em branco que o cadastro unificado tem.
INSERT INTO guia_faturamento (id, registro_origem_id, evento_clinico_id, numero_guia, procedimento,
       convenio, carteirinha, nome_paciente, cns_paciente, cpf_paciente, nascimento_paciente,
       valor, data_emissao, status)
VALUES (2, 1, 2, 'G-2026-0102', 'Mastectomia parcial',
       'Saúde Meridiano', '0041 8827 3390 05', 'MARIA APARECIDA SOUZA', NULL, '31748590211', DATE '1964-04-12',
       8400.00, DATE '2026-03-16', 'PRONTA');

-- Carteirinha divergente: dígito final trocado.
INSERT INTO guia_faturamento (id, registro_origem_id, evento_clinico_id, numero_guia, procedimento,
       convenio, carteirinha, nome_paciente, cns_paciente, cpf_paciente, nascimento_paciente,
       valor, data_emissao, status)
VALUES (3, 1, 3, 'G-2026-0103', 'Ecocardiograma transtorácico',
       'Saúde Meridiano', '0041 8827 3390 50', 'MARIA APARECIDA SOUZA', '708004288150003', '31748590211', DATE '1964-04-12',
       320.00, DATE '2026-05-09', 'PRONTA');

-- Correta.
INSERT INTO guia_faturamento (id, registro_origem_id, evento_clinico_id, numero_guia, procedimento,
       convenio, carteirinha, nome_paciente, cns_paciente, cpf_paciente, nascimento_paciente,
       valor, data_emissao, status)
VALUES (4, 1, 4, 'G-2026-0104', 'Consulta em oncologia clínica',
       'Saúde Meridiano', '0041 8827 3390 05', 'MARIA APARECIDA SOUZA', '708004288150003', '31748590211', DATE '1964-04-12',
       250.00, DATE '2026-07-03', 'PRONTA');

-- ===========================================================================
-- Registro 2 — M. A. SOUZA no Tasy (não costurado no início)
-- O nome vem abreviado porque foi assim que a clínica cadastrou. Depois da costura,
-- isso vira NOME_DIVERGENTE.
-- ===========================================================================

INSERT INTO guia_faturamento (id, registro_origem_id, evento_clinico_id, numero_guia, procedimento,
       convenio, carteirinha, nome_paciente, cns_paciente, cpf_paciente, nascimento_paciente,
       valor, data_emissao, status)
VALUES (5, 2, 8, 'G-2026-0201', 'Infusão de Trastuzumabe 440 mg',
       'Saúde Meridiano', '0041 8827 3390 05', 'M. A. SOUZA', '708004288150003', NULL, DATE '1964-04-12',
       12800.00, DATE '2026-06-20', 'PRONTA');

-- CNS trocado: dois dígitos invertidos no meio. Depois da costura, vira CNS_DIVERGENTE —
-- e é o tipo de erro que faz a operadora recusar a guia inteira.
INSERT INTO guia_faturamento (id, registro_origem_id, evento_clinico_id, numero_guia, procedimento,
       convenio, carteirinha, nome_paciente, cns_paciente, cpf_paciente, nascimento_paciente,
       valor, data_emissao, status)
VALUES (6, 2, 9, 'G-2026-0202', 'Infusão de Trastuzumabe 440 mg',
       'Saúde Meridiano', '0041 8827 3390 05', 'M. A. SOUZA', '708004288510003', NULL, DATE '1964-04-12',
       12800.00, DATE '2026-07-18', 'PRONTA');

INSERT INTO guia_faturamento (id, registro_origem_id, evento_clinico_id, numero_guia, procedimento,
       convenio, carteirinha, nome_paciente, cns_paciente, cpf_paciente, nascimento_paciente,
       valor, data_emissao, status)
VALUES (7, 2, 10, 'G-2026-0203', 'Infusão de Trastuzumabe 440 mg',
       'Saúde Meridiano', '0041 8827 3390 05', 'M. A. SOUZA', '708004288150003', NULL, DATE '1964-04-12',
       12800.00, DATE '2026-08-15', 'PRONTA');

-- ===========================================================================
-- Registro 3 — MARIA APARECIDA DE SOUZA no laboratório (não costurado no início)
-- ===========================================================================

-- Data de nascimento com o ano errado. Depois da costura, vira NASCIMENTO_DIVERGENTE.
INSERT INTO guia_faturamento (id, registro_origem_id, evento_clinico_id, numero_guia, procedimento,
       convenio, carteirinha, nome_paciente, cns_paciente, cpf_paciente, nascimento_paciente,
       valor, data_emissao, status)
VALUES (8, 3, 11, 'G-2026-0301', 'Imuno-histoquímica para HER2',
       'Saúde Meridiano', '0041 8827 3390 05', 'MARIA APARECIDA DE SOUZA', '708004288150003', '31748590211', DATE '1965-04-12',
       950.00, DATE '2026-03-01', 'PRONTA');

INSERT INTO guia_faturamento (id, registro_origem_id, evento_clinico_id, numero_guia, procedimento,
       convenio, carteirinha, nome_paciente, cns_paciente, cpf_paciente, nascimento_paciente,
       valor, data_emissao, status)
VALUES (9, 3, 12, 'G-2026-0302', 'Hemograma completo',
       'Saúde Meridiano', '0041 8827 3390 05', 'MARIA APARECIDA DE SOUZA', '708004288150003', '31748590211', DATE '1964-04-12',
       45.00, DATE '2026-08-08', 'PRONTA');

-- ===========================================================================
-- Outros pacientes, todos com par pendente na fila de identificação.
-- Sem evento clínico associado: nem toda guia nasce de um evento que a Sutura conhece.
-- ===========================================================================

INSERT INTO guia_faturamento (id, registro_origem_id, evento_clinico_id, numero_guia, procedimento,
       convenio, carteirinha, nome_paciente, cns_paciente, cpf_paciente, nascimento_paciente,
       valor, data_emissao, status)
VALUES (10, 10, NULL, 'G-2026-0401', 'Consulta em cardiologia',
       'Saúde Meridiano', '0072 4410 8890 12', 'CARLOS EDUARDO PRADO', '704118833900002', '62230198544', DATE '1977-02-19',
       280.00, DATE '2026-08-13', 'PRONTA');

INSERT INTO guia_faturamento (id, registro_origem_id, evento_clinico_id, numero_guia, procedimento,
       convenio, carteirinha, nome_paciente, cns_paciente, cpf_paciente, nascimento_paciente,
       valor, data_emissao, status)
VALUES (11, 12, NULL, 'G-2026-0501', 'Ultrassonografia abdominal',
       'Saúde Meridiano', '0072 4410 3321 08', 'TEREZA CRISTINA MOURA', '709332044710008', '80411533067', DATE '1969-06-25',
       210.00, DATE '2026-08-06', 'PRONTA');

INSERT INTO guia_faturamento (id, registro_origem_id, evento_clinico_id, numero_guia, procedimento,
       convenio, carteirinha, nome_paciente, cns_paciente, cpf_paciente, nascimento_paciente,
       valor, data_emissao, status)
VALUES (12, 6, NULL, 'G-2026-0601', 'Sessão de quimioterapia',
       'Saúde Meridiano', '0072 4410 5567 33', 'ANA BEATRIZ LIMA', '706441099230004', '22874061509', DATE '1982-11-30',
       4500.00, DATE '2026-08-22', 'PRONTA');

-- Roberto: o caso sem CNS nos dois lados. A guia também sai sem CNS, e é o exemplo de
-- que nem toda guia em risco tem correção automática possível — não há de onde tirar o dado.
INSERT INTO guia_faturamento (id, registro_origem_id, evento_clinico_id, numero_guia, procedimento,
       convenio, carteirinha, nome_paciente, cns_paciente, cpf_paciente, nascimento_paciente,
       valor, data_emissao, status)
VALUES (13, 8, NULL, 'G-2026-0701', 'Consulta em ortopedia',
       'Saúde Meridiano', '0072 4410 9902 41', 'ROBERTO NASCIMENTO', NULL, '15088344720', DATE '1958-07-07',
       240.00, DATE '2026-08-19', 'PRONTA');

INSERT INTO guia_faturamento (id, registro_origem_id, evento_clinico_id, numero_guia, procedimento,
       convenio, carteirinha, nome_paciente, cns_paciente, cpf_paciente, nascimento_paciente,
       valor, data_emissao, status)
VALUES (14, 13, NULL, 'G-2026-0801', 'Colesterol total e frações',
       'Saúde Meridiano', '0072 4410 3321 08', 'T. C. MOURA', '709332044710008', NULL, DATE '1969-06-25',
       68.00, DATE '2026-08-11', 'PRONTA');

COMMIT;

-- Os ids foram informados explicitamente para manter as referências legíveis. Isso deixa a
-- sequência de IDENTITY para trás, então ela é realinhada — sem isso o primeiro INSERT
-- vindo da API colidiria com um id já usado.
ALTER TABLE guia_faturamento MODIFY (id GENERATED BY DEFAULT AS IDENTITY (START WITH LIMIT VALUE));
ALTER TABLE correcao_guia    MODIFY (id GENERATED BY DEFAULT AS IDENTITY (START WITH LIMIT VALUE));
