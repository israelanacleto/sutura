# PRD — Sutura, fase 3: prevenção de glosa de cadastro

**Projeto:** Sutura (FIAP / parceria Oracle Academy)
**Autor:** Israel Anacleto
**Criado em:** 04/09/2026
**Entrega alvo:** 13/09/2026 (vídeo pitch para seleção da Banca Final)
**Status:** aprovado, aguardando início

---

## 1. Resumo executivo

O pitch da Sutura tem três pilares: integração, unificação de histórico e automação de
back-office com prevenção de glosa. As fases 1 e 2 entregaram os dois primeiros. **O
terceiro nunca foi construído**, e ele é o único que fala a língua de quem assina o
contrato.

Esta fase constrói a prevenção de glosa **de cadastro** — não de codificação, não de
autorização. A escolha não é de conveniência: é o único tipo de glosa que se apoia no que
já existe. O paciente costurado é a fonte de verdade contra a qual a guia é validada.

**O que isso muda na narrativa do produto:** costurar deixa de ser uma capacidade elegante e
passa a ser o que evita perder dinheiro. Até aqui, a tela de identificação falava com TI e
com a diretoria clínica. A tela de faturamento fala com quem aprova a verba.

---

## 2. Quick start / handoff

```bash
cd F:\PJ\FIAP\Sutura\api && mvn spring-boot:run -Dspring-boot.run.profiles=local
```

```bash
cd F:\PJ\FIAP\Sutura\web && npm start
```

A nova tela vive em `http://localhost:4200/faturamento`.

A preparação do banco, da wallet e das credenciais não muda — está no
[README da raiz](README.md).

---

## 3. Objetivos e não-objetivos

### Objetivos

1. Detectar, antes do envio, guias com risco de glosa por divergência de cadastro.
2. Mostrar o risco em reais, porque é assim que o comprador mede.
3. Permitir corrigir a guia com o dado do cadastro unificado, em um clique, com auditoria.
4. **Ligar os dois módulos:** uma duplicidade não resolvida na fila de identificação vira
   alerta na guia, e resolvê-la faz o alerta desaparecer sem ninguém tocar na guia.

### Não-objetivos, declarados

- **Glosa de codificação** — TUSS errado, procedimento incompatível com CID, quantidade
  fora do previsto. É a glosa mais comum na prática e exigiria tabela TUSS e regra clínica
  que teríamos que inventar. Inventar regra clínica diante de banca de saúde é risco sem
  retorno.
- **Autorização prévia** — senha ausente ou vencida. Módulo paralelo, não usaria nada do
  que já existe.
- **Ingestão de XML TISS** — seria a história mais forte de interoperabilidade, e é um
  parser novo que consome boa parte dos cinco dias. Fica desenhado, não construído.
- **Valor real de procedimento** — os valores do seed são fictícios e plausíveis.
- **Envio da guia à operadora.** A Sutura aponta o risco; quem envia é o sistema de
  faturamento do cliente.

---

## 4. Contexto e restrições

- **Cinco dias úteis**, entre a entrega do Challenge em 08/09 e a gravação do vídeo em
  13/09. Não é prazo para uma fase confortável.
- **Executor único:** Israel. Tarefas maiores, menos cerimônia.
- **Alvo é o vídeo, não a entrega de 08/09.** Camada nova mal-acabada na entrega que vale
  nota é pior que a ausência declarada.
- **Stack inalterada:** Angular 21, Spring Boot 3.5, Oracle Autonomous 26ai. Nenhuma
  dependência nova.
- **LGPD:** guia de faturamento contém dado de saúde e dado financeiro. A correção é
  auditada como a decisão de identidade já é.
- **Dado fictício**, com formato realista. Nada de dado da Saúde One, em nenhuma hipótese.

---

## 5. Arquitetura

Nenhuma camada nova. O padrão da fase 2 se repete, e é isso que permite entregar em cinco
dias.

```
web/pages/faturamento/          tela nova, mesma linguagem visual da identificação
  ↓ httpResource
api/web/GuiaController           GET /v1/guias, GET /v1/guias/{id}, POST /v1/guias/{id}/corrigir
  ↓
api/service/GlosaService         traduz o risco em algo auditável por uma pessoa
  ↓ JdbcClient
Oracle · vw_risco_glosa          as regras rodam aqui, não em Java
```

### A decisão que sustenta o desenho

**A guia carrega uma cópia própria dos dados do paciente.** Nome, CNS, CPF e carteirinha são
gravados na guia no momento da emissão — digitados ou copiados de um sistema qualquer.

É exatamente essa cópia que diverge, e é a divergência que gera glosa. A Sutura tem o
cadastro real — o `paciente_mestre`, costurado — e compara.

**As regras rodam em SQL**, como o motor de identificação. Mesma razão: a comparação
acontece onde o dado está, e a regra fica legível em vez de escondida atrás de um ORM.

---

## 6. Modelo de dados

> ⚠️ **Passo reversível marcado.** Migrations não rodam sem autorização explícita.

### `V9__schema_glosa.sql`

**`guia_faturamento`** — a guia como o sistema de faturamento a emitiu

| Coluna | Papel |
|---|---|
| `id` | |
| `registro_origem_id` | de qual registro de origem a guia nasceu |
| `evento_clinico_id` | o procedimento cobrado |
| `numero_guia` | identificador no sistema de faturamento |
| `convenio`, `carteirinha` | como constam **na guia** |
| `nome_paciente`, `cns_paciente`, `cpf_paciente`, `nascimento_paciente` | a cópia dos dados no momento da emissão |
| `valor` | valor cobrado |
| `data_emissao` | |
| `status` | `RASCUNHO`, `PRONTA`, `ENVIADA` |

As quatro colunas de identificação do paciente **são o ponto do modelo**. Sem elas não há o
que comparar, e a divergência não teria onde existir.

**`regra_glosa`** — as regras, em tabela e não em código

| Coluna | Papel |
|---|---|
| `codigo` | `PACIENTE_NAO_COSTURADO`, `NOME_DIVERGENTE`, … |
| `descricao` | o texto que o operador lê |
| `severidade` | `ALTA`, `MEDIA`, `BAIXA` |
| `ativo` | permite desligar uma regra sem migration |

**`correcao_guia`** — auditoria

| Coluna | Papel |
|---|---|
| `guia_id`, `campo`, `valor_anterior`, `valor_novo` | o que mudou |
| `usuario`, `corrigido_em` | quem e quando |

Uma linha por campo corrigido. Guia com três campos errados gera três linhas — é assim que
se responde "o que exatamente foi alterado nesta guia".

### `V10__seed_guias.sql`

Guias derivadas dos eventos clínicos já existentes, mais problemas plantados:

- guias corretas, para a tela não parecer que tudo está errado
- uma com nome divergente
- uma sem CNS
- uma com carteirinha divergente
- uma cujo paciente não está costurado
- **uma cujo paciente tem par pendente na fila de identificação** — a que liga os módulos

### `V11__view_risco_glosa.sql`

Uma linha por guia e regra violada, com severidade e o valor em risco.

**As sete regras:**

| Código | O que detecta | Severidade |
|---|---|---|
| `PACIENTE_NAO_COSTURADO` | A guia aponta para um registro sem paciente mestre — não há contra o que validar | ALTA |
| `CNS_AUSENTE` | Campo exigido por boa parte dos convênios | ALTA |
| `CNS_DIVERGENTE` | CNS da guia difere do cadastro unificado | ALTA |
| `CARTEIRINHA_DIVERGENTE` | Carteirinha da guia difere do cadastro | ALTA |
| `NASCIMENTO_DIVERGENTE` | Data de nascimento difere | MEDIA |
| `NOME_DIVERGENTE` | Nome difere do canônico, comparado sem acento e com Jaro-Winkler | MEDIA |
| `DUPLICIDADE_PENDENTE` | O paciente tem par não resolvido na fila de identificação | ALTA |

A comparação de nome e a normalização de acento reaproveitam exatamente o que a `V8` já
usa. Nenhuma regra nova de comparação é inventada.

---

## 7. Especificação

### Endpoints

| Método | Rota | Devolve |
|---|---|---|
| `GET` | `/v1/guias` | Guias com risco, ordenadas por severidade e valor, mais o total em risco |
| `GET` | `/v1/guias/{id}` | A guia, os alertas, e a comparação campo a campo contra o cadastro |
| `POST` | `/v1/guias/{id}/corrigir` | Aplica os dados do cadastro unificado, grava auditoria, devolve 204 |

Payload da correção:

```json
{ "campos": ["cns", "carteirinha"], "usuario": "israel.anacleto" }
```

**A correção é seletiva por campo, não um "aceitar tudo".** O operador escolhe o que aplicar
— pelo mesmo motivo que a costura exige decisão humana: o custo do erro em dado de saúde é
assimétrico.

### A tela `/faturamento`

**Topo:** o número que o faturamento entende.

```
R$ 47.320 em risco    ·    12 guias com alerta    ·    de 38 guias prontas para envio
```

**Lista:** uma linha por guia, ordenada por severidade e depois por valor. Cada linha mostra
número da guia, paciente, convênio, valor e as etiquetas de risco.

**Guia aberta:** a comparação campo a campo, **na mesma linguagem visual da tela de
identificação** — valor na guia à esquerda, valor no cadastro unificado à direita, com o
sinal de igual, divergente ou ausente no meio.

Botão: **Corrigir com o cadastro unificado**, com as caixas de seleção por campo.

Quando o alerta for `DUPLICIDADE_PENDENTE`, no lugar do botão de correção vai um link:
**"Resolver na fila de identificação →"**. Porque não há o que corrigir na guia — o problema
está uma camada abaixo.

---

## 8. Plano de build faseado

> Israel roda todos os builds e testes localmente. Nenhuma fase é dada como concluída sem
> ele rodar e reportar.

### F1 — Schema e seed · até 09/09

`V9__schema_glosa.sql`, `V10__seed_guias.sql`. As três tabelas, as sete regras cadastradas,
e as guias derivadas dos eventos com os problemas plantados.

**Israel roda `mvn flyway:migrate` e reporta.** Migration é passo reversível.

### F2 — Motor de regras · até 10/09

`V11__view_risco_glosa.sql`. As sete regras em SQL, reaproveitando a normalização de acento
da `V8`.

**Verificação:** consultar a view direto e conferir que cada problema plantado no seed
aparece com a regra e a severidade certas — e que as guias corretas não aparecem.

**Israel roda e reporta.**

### F3 — Endpoints · até 11/09

`GuiaController`, `GlosaService`, DTOs. Os três endpoints, com a comparação campo a campo
montada no serviço, como já é feito na identificação.

**Israel roda e reporta.**

### F4 — Tela · até 12/09

`pages/faturamento`, o total em risco no topo, a lista e a guia aberta. Reaproveitar os
componentes visuais da tela de identificação — mesma tabela de comparação, mesmos badges.

**Israel roda e reporta.**

### F5 — Material · 13/09

Atualizar o roteiro do vídeo para incluir a glosa, regerar capturas, gravar.

---

## 9. Critérios de aceite

| # | Critério | Como se verifica |
|---|---|---|
| 1 | Guias nascem dos eventos existentes | Cada guia do seed aponta para um `evento_clinico` real |
| 2 | As sete regras disparam | Cada problema plantado aparece na view com a regra certa |
| 3 | Guia correta não gera alerta | Nenhuma linha na view para as guias sem problema |
| 4 | O valor em risco bate | O total da tela é igual à soma dos valores das guias com alerta |
| 5 | Correção funciona e é auditada | Após `POST /corrigir`, o alerta some e há linha em `correcao_guia` com valor anterior e novo |
| 6 | **Os módulos estão ligados** | Resolver a duplicidade na tela de identificação faz o alerta `DUPLICIDADE_PENDENTE` sumir **sem tocar na guia** |
| 7 | Nenhuma regra em Java | As sete estão em `V11`; o serviço só traduz |

**O critério 6 é o mais importante.** É o que prova que a Sutura é um produto e não três
telas independentes — e é o momento do vídeo.

---

## 10. Log de decisões

| Data | Decisão | Motivo |
|---|---|---|
| 04/09 | Glosa de cadastro, não de codificação | É a única que se apoia no que já existe. O paciente costurado é a fonte de verdade |
| 04/09 | Guias derivadas dos eventos + seed | Cinco dias não comportam um parser TISS novo |
| 04/09 | Regras em tabela, executadas em SQL | Mesma razão do motor de identificação, e permite desligar uma regra sem migration |
| 04/09 | Correção seletiva por campo | O custo do erro em dado de saúde é assimétrico; "aceitar tudo" esconde decisão |
| 04/09 | Alvo 13/09, não 08/09 | Com o AgroSmart no meio, sobra um dia útil até a entrega. Camada mal-acabada é pior que ausência declarada |
| 04/09 | Valor em risco como indicador principal | É a única métrica que o comprador identificado no modelo de negócio mede |

## Futuro, fora desta fase

- **XML TISS** — ingerir a guia no padrão da ANS, como já se faz com FHIR. É a evolução
  natural e a mais forte em interoperabilidade.
- **Glosa de codificação** — exige tabela TUSS e regras clínicas.
- **Aprendizado das glosas recebidas** — usar o retorno da operadora para descobrir quais
  divergências realmente geram glosa, em vez de supor as sete regras.
- **Priorização por valor recuperável** — hoje a ordenação é por severidade e valor; poderia
  considerar a probabilidade histórica de a glosa se sustentar.
