# PRD — Sutura, fase 2: backend Spring Boot + Oracle ADB

**Projeto:** Sutura (FIAP / parceria Oracle Academy)
**Autor:** Israel Anacleto
**Criado em:** 27/08/2026
**Entrega alvo:** 08/09/2026 (Challenge/Startup One)
**Status:** aprovado, em implementação

---

## 1. Resumo executivo

A fase 1 entregou um protótipo Angular navegável com três telas — Conexões, Identificação
de pacientes e Histórico unificado — cujo estado vive inteiramente em memória. A fase 2
substitui esse estado por um backend real: **Java 21 + Spring Boot 3.5 sobre Oracle
Autonomous Database**, com ingestão de dados clínicos em formato FHIR R4 e um motor de
identificação de pacientes que calcula similaridade **dentro do banco**, usando
`UTL_MATCH.JARO_WINKLER_SIMILARITY`.

O recorte é uma **fatia vertical**: um sistema de origem, um fluxo completo de ingestão →
identificação → costura → histórico unificado, com as duas telas mais importantes
consumindo a API de verdade. Não é um backend completo pela metade — é um caminho estreito
inteiro.

O que isso prova para a banca: que a Sutura não é uma tela bonita, mas uma camada que
recebe dado sujo de um sistema externo, resolve identidade de paciente com critério
auditável, e devolve um histórico costurado.

---

## 2. Quick start / handoff

```bash
# backend
cd F:\PJ\FIAP\Sutura\api
mvn spring-boot:run          # sobe em http://localhost:8080

# front
cd F:\PJ\FIAP\Sutura\web
npm start                    # sobe em http://localhost:4200
```

Configuração sensível (string de conexão, usuário, senha, caminho da wallet) fica em
`api/src/main/resources/application-local.yml`, **fora do git** (`.gitignore`). O
`application.yml` versionado traz apenas placeholders.

Estado da demonstração se restaura com:

```bash
mvn flyway:clean flyway:migrate   # ⚠️ destrói e recria o schema — passo irreversível
```

---

## 3. Objetivos e não-objetivos

### Objetivos

1. Persistir em Oracle ADB os registros vindos de sistemas de origem, preservando o payload
   bruto para rastreabilidade.
2. Calcular pares candidatos a serem o mesmo paciente, com score explicável campo a campo.
3. Registrar toda decisão de costura com usuário, timestamp e justificativa — trilha de
   auditoria exigível por LGPD e por qualquer auditoria clínica.
4. Servir o histórico unificado de um paciente com a origem de cada evento rastreada.
5. Fazer as telas de Identificação e Histórico funcionarem sem nenhum dado fixo no front.

### Não-objetivos (declarados, não esquecidos)

- Deploy em produção — a demonstração de 08/09 roda local contra o ADB.
- Autenticação e autorização. O campo `usuario` da auditoria vem fixo por ora.
- Ingestão dos três ERPs. Um só prova o conceito; os outros são repetição.
- Camada de prevenção de glosa e qualquer IA generativa.
- Tela de Conexões consumindo dados reais (fica com dados servidos, mas sem sincronização
  real).

---

## 4. Contexto e restrições

- **Prazo real de trabalho:** 12 dias corridos, com a **Sprint 5 do AgroSmart em 05/09**
  ocupando o fim de semana anterior à entrega. O cronograma da §8 já congela a Sutura
  nesses dias.
- **Executor único:** Israel. As tarefas não precisam ser paralelizáveis.
- **Stack obrigatória pelo challenge:** ecossistema Oracle. Isso não é preferência, é
  critério de avaliação — daí a decisão de rodar o matching no banco (§5).
- **Dado de saúde é dado sensível (LGPD art. 11).** Mesmo com dados fictícios, o desenho
  precisa mostrar trilha de auditoria e segregação de payload bruto.
- **Ambiente verificado em 27/08:** Java 21.0.11 (Temurin), Maven 3.8.6, Docker 29.7.2,
  Node 22.13.1, Angular CLI 21.2.1.
- **Risco dominante:** primeira conexão JDBC ao ADB (wallet, mTLS, `TNS_ADMIN`). É o
  único item do plano com gatilho de fallback automático — ver §8, F0.

---

## 5. Arquitetura

```
web/  (Angular 21)                     api/  (Spring Boot 3.5, Java 21)
  pages/                                 web/          controllers REST
  core/sutura-store.ts  ── HTTP ──►      service/      regras de costura
      httpResource()                     repository/   JPA + JdbcClient
                                         ingest/       parser FHIR R4
                                         domain/       entidades
                                              │
                                              ▼
                                    Oracle Autonomous Database
                                    schema SUTURA · Flyway
                                    UTL_MATCH.JARO_WINKLER_SIMILARITY
```

### Decisões e por quê

| Decisão | Alternativa descartada | Motivo |
|---|---|---|
| Score calculado em SQL, no Oracle | Calcular em Java com biblioteca de string distance | `UTL_MATCH` é nativo do Oracle. Usar o banco como banco de verdade é o que diferencia "usamos Oracle" de "usamos um banco qualquer". Vale nota no challenge |
| `JdbcClient` para a query de matching, JPA para o resto | Tudo em JPA | A query de identificação é o coração do produto; escondê-la atrás de um ORM torna ilegível justamente o que importa |
| Ingestão de Bundle FHIR R4 (JSON, via Jackson) | HL7 v2 pipe-delimited; HAPI FHIR | FHIR JSON entrega o mesmo argumento de interoperabilidade sem gastar dias em parser. HAPI é peso desnecessário para um bundle de exemplo |
| Flyway | Liquibase; DDL manual | Migration versionada é artefato de entrega, e `flyway:clean` dá o reset da demo de graça |
| `httpResource()` no front | `HttpClient` + `subscribe` manual | Angular 21 idiomático; mantém a superfície de signals que as telas já consomem |

O `SuturaStore` foi escrito na fase 1 como ponto único de acesso a dados justamente para
que esta troca não toque nos componentes. **Nenhuma das três telas muda de estrutura.**

---

## 6. Modelo de dados

> ⚠️ **Passo reversível marcado.** Nenhuma migration roda sem autorização explícita.
> `flyway:migrate` é aditivo; `flyway:clean` **destrói o schema** e só deve ser usado no
> reset da demonstração.

Migration `V1__schema.sql`:

| Tabela | Papel | Campos-chave |
|---|---|---|
| `sistema_origem` | Os ERPs conectados | `codigo` (MV/TASY/LAB/LEGADO), `nome`, `fornecedor`, `unidade`, `protocolo`, `status`, `ultima_sync`, `total_registros` |
| `registro_origem` | O dado como veio, sem normalizar | `sistema_id`, `identificador_origem`, `nome`, `nome_mae`, `cns`, `cpf`, `data_nascimento`, `sexo`, `payload_bruto` (CLOB), `ingerido_em` |
| `paciente_mestre` | A identidade costurada | `nome_canonico`, `cns`, `cpf`, `data_nascimento`, `criado_em` |
| `vinculo_registro` | Liga registro → mestre | `paciente_mestre_id`, `registro_origem_id` **UNIQUE** |
| `evento_clinico` | A linha do tempo | `registro_origem_id`, `paciente_mestre_id`, `data_evento`, `categoria`, `titulo`, `detalhe`, `unidade`, `ciclo` |
| `decisao_identificacao` | **Auditoria** | `registro_a_id`, `registro_b_id`, `score`, `recomendacao`, `decisao`, `usuario`, `justificativa`, `decidido_em` |

O `UNIQUE` em `vinculo_registro.registro_origem_id` é o que impede um registro de pertencer
a dois pacientes mestres — a garantia estrutural contra o prontuário falso que a tela de
Identificação promete evitar.

Migration `V2__seed.sql` carrega os quatro sistemas de origem e os dados que hoje estão
fixos em `web/src/app/core/mock-data.ts`, para que a demonstração continue idêntica.

### O motor de identificação

```sql
-- V3__view_candidatos.sql
CASE WHEN a.cns = b.cns              THEN 50 ELSE 0 END
+ CASE WHEN a.cpf = b.cpf            THEN 30 ELSE 0 END
+ CASE WHEN a.data_nascimento
         = b.data_nascimento         THEN 15 ELSE 0 END
+ CASE WHEN a.nome_mae = b.nome_mae  THEN 10 ELSE 0 END
+ UTL_MATCH.JARO_WINKLER_SIMILARITY(a.nome, b.nome) * 0.25
```

Normalizado para 0–100. Faixas: **≥ 90 costurar · 70–89 revisar · < 70 separar** — os
mesmos cortes que o protótipo já exibe.

O teste real deste motor: o par de homônimos (João Carlos Ferreira, nomes idênticos mas
CNS, CPF e data de nascimento divergentes) precisa sair com score abaixo de 70
**sem nenhum tratamento especial no código**. Se precisar de `if` para acertar esse caso,
a fórmula está errada.

---

## 7. Especificação

| Método | Rota | Devolve | Consumido por |
|---|---|---|---|
| `GET` | `/v1/conexoes` | Lista de `sistema_origem` com contagem de registros | Tela de Conexões |
| `POST` | `/v1/ingest/fhir` | Resumo da ingestão (registros criados, ignorados) | Nenhuma tela — via `curl` na demonstração |
| `GET` | `/v1/candidatos` | Pares candidatos com score, recomendação e comparação campo a campo | Tela de Identificação |
| `POST` | `/v1/candidatos/{id}/decisao` | 204; grava auditoria e cria/atualiza o vínculo | Tela de Identificação |
| `GET` | `/v1/pacientes/{id}` | Ficha, cadastros fragmentados por sistema e timeline unificada | Tela de Histórico |

Payload da decisão:

```json
{ "decisao": "COSTURADO", "usuario": "israel.anacleto", "justificativa": "CNS confere" }
```

O `GET /v1/pacientes/{id}` devolve **as duas visões** num mesmo payload — a timeline
unificada e os cadastros fragmentados por sistema — porque o alternador "Antes da Sutura /
Com a Sutura" precisa das duas sem uma segunda chamada.

---

## 8. Plano de build faseado

> Israel roda todos os builds e testes localmente. Nenhuma fase é dada como concluída sem
> ele rodar e reportar.

### F0 — Conexão ao Oracle ADB · até 29/08 · 🚩 checkpoint duro

Criar a conta OCI pela parceria FIAP/Oracle Academy (passo a passo em
`docs/Criacao-conta-OCI-Parceria-FIAP-Oracle-Academy.pdf`), provisionar o Autonomous
Database Always Free, baixar a wallet e provar a conexão com um `SELECT 1 FROM DUAL` via
JDBC.

**Israel roda e reporta.**

> **Gatilho de fallback — automático, não sugestão.** Se até **30/08** o JDBC não conectar
> ao ADB, o desenvolvimento passa para **Oracle XE 21 em Docker** com o schema idêntico, e
> o ADB volta a ser meta de migração e não bloqueio de caminho crítico. O `UTL_MATCH`
> existe no XE, então o motor de identificação não muda. Docker 29.7.2 já verificado na
> máquina.

### F1 — Schema e seed · até 30/08

Módulo `api/` (Spring Boot 3.5, Java 21, Maven), Flyway configurado, `V1__schema.sql`,
`V2__seed.sql`. **Israel roda `mvn flyway:migrate` e reporta** — migration é passo
reversível e não roda sem o "pode".

### F2 — Ingestão FHIR · até 31/08

Endpoint `POST /v1/ingest/fhir`, parser de Bundle R4 com Jackson, arquivo de exemplo em
`api/src/test/resources/bundle-exemplo.json`. Payload bruto preservado no CLOB.

**Israel roda e reporta.**

### F3 — Motor de identificação · até 02/09

`V3__view_candidatos.sql` e o repositório com `JdbcClient`. Teste de integração que trava
o caso do homônimo abaixo de 70.

**Israel roda `mvn test` e reporta.**

### F4 — Endpoints · até 03/09

Os cinco endpoints da §7, DTOs, tratamento de erro, CORS liberado para `localhost:4200`.

**Israel roda e reporta.**

### 04–05/09 — Sutura congelada

Sprint 5 do AgroSmart. Nenhum trabalho de Sutura nestes dias — está no plano de propósito,
não por esquecimento.

### F5 — Front consumindo a API · 06/09

`provideHttpClient()`, `SuturaStore` reescrito com `httpResource()`, mantendo a mesma
superfície pública. Estados de carregamento e erro nas duas telas.

**Israel roda e reporta.**

### F6 — Material de apresentação · 07/09

Atualizar `docs/apresentacao/roteiro-demo.md` com o fluxo real (agora inclui o `curl` da
ingestão), regerar as capturas, atualizar o slide de fases do deck.

### 08/09 — Entrega do Challenge

---

## 9. Critérios de aceite e testes

| # | Critério | Como se verifica |
|---|---|---|
| 1 | Ingestão grava registros preservando o payload original | `POST /v1/ingest/fhir` com o bundle de exemplo; conferir `payload_bruto` no banco |
| 2 | Score é calculado no banco, não no código | `GET /v1/candidatos` devolve scores; a fórmula está em `V3__view_candidatos.sql` |
| 3 | O homônimo é rejeitado pela fórmula, sem caso especial | Teste de integração `IdentificacaoRepositoryTest#homonimoFicaAbaixoDoCorte` |
| 4 | Decisão gera trilha de auditoria completa | Após `POST .../decisao`, existe linha em `decisao_identificacao` com usuário e timestamp |
| 5 | Um registro nunca pertence a dois mestres | Constraint `UNIQUE`; teste que tenta o vínculo duplicado e espera violação |
| 6 | Histórico unificado traz a origem de cada evento | `GET /v1/pacientes/{id}`; cada evento tem `sistema` |
| 7 | As telas funcionam sem dado fixo | `grep` por `mock-data` nos componentes não retorna nada nas telas de Identificação e Histórico |
| 8 | A demonstração é restaurável | `mvn flyway:clean flyway:migrate` devolve o estado inicial |

---

## 10. Log de decisões

| Data | Decisão | Motivo |
|---|---|---|
| 27/08 | Fatia vertical em vez de backend amplo | 12 dias com a Sprint 5 no meio; um caminho inteiro vale mais que três pela metade |
| 27/08 | ADB na OCI, sem plano B declarado pelo time | Escolha do Israel; mitigado por gatilho automático de fallback em 30/08 |
| 27/08 | Matching em SQL com `UTL_MATCH` | Diferencial de avaliação num challenge Oracle |
| 27/08 | FHIR JSON em vez de HL7 v2 | Custo de parser não se paga no prazo |
| 27/08 | Sem deploy | Demonstração local não custa nota e tira infra do caminho crítico |

## Futuro (fora desta fase)

- Ingestão dos ERPs restantes e do CSV legado, com normalização de layout
- HL7 v2 (ADT/ORU) além do FHIR
- Camada de prevenção de glosa — o terceiro pilar do pitch, ainda não tocado
- OCI Data Safe para mascaramento em ambiente não-produtivo
- Autenticação, e o `usuario` da auditoria vindo de verdade do token
- Deploy em OCI Compute (Ampere Always Free)
