# Sutura

Projeto de curso (FIAP / parceria Oracle Academy) — healthtech.

## O pivot

A gente para de tentar ser "mais um sistema de saúde com IA" e vira a camada que se conecta por cima dos ERPs de saúde que já existem. A gente não substitui ninguém — conecta, organiza e automatiza o que esses sistemas fazem mal ou não fazem. Tipo: "plugue nos seus sistemas e tenha os dados unificados e o back-office no automático."

### O que a gente é (o coração)

- **Conector via API pros ERPs de saúde** (MV, Tasy e cia) — interoperabilidade é o centro de tudo.
- **Unificação do histórico do paciente** — costurar prontuários espalhados (ex.: injeção a cada 4 semanas registrada em lugares diferentes) num histórico único.
- **Motor de automação** — cargas de tabela, cadastros, padronização de planilha, prevenção de glosa (parte já automatizada no trabalho, economizando +50h/mês).

### O que a gente NÃO é

Não somos um raciocinador clínico — isso é a Sofya. Nossa IA é complemento, de bastidor: achar inconsistência, prever glosa, sugerir correção de cadastro. Não é IA de consultório.

### A sacada

Em vez de competir com a Sofya, a gente alimenta ela com dado limpo e unificado. Viramos a "tubulação" do ecossistema.

**Modelo:** SaaS por assinatura — versão simples pra clínica, robusta pra hospital.

## Rich Picture (3 blocos)

- **Esquerda (dores de hoje):** sistemas ilhados que não conversam, prontuário por atendimento, planilha na mão, equipe administrativa sufocada, glosa.
- **Centro (nossa camada):** hub de integração — vários ERPs entrando por API, convergindo pro conector → base unificada (Oracle) → motor de automação → IA como módulo lateral, não o centro. Várias setas de ERPs diferentes apontando pra gente (mostra que conectamos tudo, não somos um sistema único).
- **Direita (resultado):** histórico unificado, cargas automáticas, menos glosa, equipe liberada. Seta nova saindo pra cima: "dado limpo alimenta ferramentas clínicas (tipo a Sofya)" — parceria, não briga.

Ver `docs/Rich-Picture-Mapa-Stakeholders-Tela-Sutura.pptx`.

## Mapa de Stakeholders

- **Clínica/Hospital** — quem paga; quer eficiência e menos glosa.
- **Equipe administrativa/faturamento** — quem mais ganha com a automação.
- **Médico/equipe assistencial** — ganha os dados unificados na mão.
- **Paciente** — beneficiário final, não repete exame nem reconta história.
- **Operadora/convênio** — recebe faturamento limpo.
- **TI/fornecedor do ERP** — precisa liberar a integração; parceiro ou dor de cabeça.
- **ERPs de saúde** (MV, Tasy…) — o que a gente conecta.
- **Soluções de IA clínica** (tipo Sofya) — parceira, não concorrente.
- **Reguladores** — ANS/TISS, LGPD.
- **Oracle** — parceira de tecnologia.
- **A gente** — o time que constrói.

### O que mudou da versão anterior do Rich Picture

Entraram dois novos: **TI/fornecedor do ERP** (a integração depende deles) e a **IA clínica como parceira** (mata o argumento de "ah, isso já existe").

## Docs

- `docs/Criacao-conta-OCI-Parceria-FIAP-Oracle-Academy.pdf` — passo a passo de criação da conta na OCI (parceria FIAP-Oracle Academy).
- `docs/Rich-Picture-Mapa-Stakeholders-Tela-Sutura.pptx` — Rich Picture + Mapa de Stakeholders + telas.
