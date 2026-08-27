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

---

# O repositório

| Pasta | O que é |
|---|---|
| `api/` | Backend em Java 21 + Spring Boot 3.5 sobre Oracle Autonomous Database |
| `web/` | Front-end em Angular 21 |
| `docs/apresentacao/` | Roteiro da demonstração e capturas das telas |
| `PRD-backend-fase-2.md` | Arquitetura, decisões e critérios de aceite da fase atual |

## O que você precisa instalado

Java 21, Maven 3.8+, Node 22+. Verificado com Temurin 21.0.11, Maven 3.8.6 e Node 22.13.1.

## Antes de rodar pela primeira vez

O backend **não sobe sem banco**. É preciso um Oracle Autonomous Database e as credenciais
locais, que não estão no repositório — e não devem estar.

**1. Provisionar o banco.** Um Autonomous Database Always Free, workload *Transaction
Processing*, versão 26ai, acesso *Secure access from everywhere*. O passo a passo da conta
está em `docs/Criacao-conta-OCI-Parceria-FIAP-Oracle-Academy.pdf`.

**2. Baixar a wallet.** No console: *Database connection → Download wallet*. Descompacte em
`.wallet/` na raiz do projeto. Essa pasta está no `.gitignore` e nunca vai para o git.

**3. Criar o arquivo de credenciais.** Copie o exemplo e preencha a senha do ADMIN:

```bash
cp api/src/main/resources/application-local.yml.exemplo api/src/main/resources/application-local.yml
```

O arquivo também está no `.gitignore`. A senha do ADMIN não trafega pelo repositório —
combine por outro canal com quem for rodar.

**4. Criar o schema.** As migrations rodam sozinhas na subida da aplicação. Para rodar
antes, sem subir o backend:

```bash
cd api && mvn flyway:migrate -Dflyway.locations=filesystem:src/main/resources/db/migration -Dflyway.baselineOnMigrate=true -Dflyway.baselineVersion=0
```

O `baselineVersion=0` é obrigatório: o schema `ADMIN` do Autonomous nunca está vazio, e com
o padrão o Flyway pularia a criação das tabelas.

## Como rodar

```bash
cd api && mvn spring-boot:run -Dspring-boot.run.profiles=local
```

```bash
cd web && npm install && npm start
```

Backend em `http://localhost:8080`, front em `http://localhost:4200`. O front depende do
backend: sem ele, as telas mostram o aviso de falha de conexão.

## Antes de apresentar

Restaure o estado da demonstração com `api/scripts/reset-demo.sql`. O roteiro completo,
com os cliques e as falas, está em
[`docs/apresentacao/roteiro-demo.md`](docs/apresentacao/roteiro-demo.md).

## O que ainda não existe

Autenticação, deploy, ingestão dos outros ERPs e a camada de prevenção de glosa — que é o
terceiro pilar do pitch e segue intocado. Está tudo declarado como fora de escopo no PRD.

## Docs

- `docs/Criacao-conta-OCI-Parceria-FIAP-Oracle-Academy.pdf` — passo a passo de criação da conta na OCI (parceria FIAP-Oracle Academy).
- `docs/Rich-Picture-Mapa-Stakeholders-Tela-Sutura.pptx` — Rich Picture + Mapa de Stakeholders + telas.
