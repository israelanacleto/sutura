# Relatório de validação técnica

Documento de 13/10/2026. Avalia a viabilidade da solução, os desafios enfrentados na
construção, os riscos técnicos conhecidos e os gargalos encontrados.

> **Todo incidente aqui é real e verificável.** Cada um tem a correção apontada no arquivo
> onde ela está. Nenhum foi reconstruído de memória para ilustrar um ponto — os erros de
> diagnóstico que cometemos no caminho estão incluídos, inclusive os que custaram tempo.

---

## 1. Veredito de viabilidade

**A solução é tecnicamente viável, e isso não é opinião.** O que sustenta a afirmação:

| Pergunta | Resposta | Evidência |
|---|---|---|
| O motor de identificação funciona? | Sim | 19.999 registros, 608.194 pares, **0 falsos positivos** |
| Escala? | Para o porte-alvo, sim | 21,3 s em instância de **1 OCPU** |
| Roda contra infraestrutura real? | Sim | Oracle Autonomous 26ai em `sa-saopaulo-1`, mTLS com wallet |
| Integra com padrão de mercado? | Parcialmente | FHIR R4 implementado; HL7 v2 e CSV não |
| A prevenção de glosa se sustenta? | Sim | 7 regras, 14 guias de demonstração, todas as regras disparando |

**O que a viabilidade NÃO cobre:** não existe autenticação, não existe deploy e não existem
testes automatizados. O sistema é demonstrável, não operável por terceiros. A distinção
está na seção 5.

---

## 2. Desafios de implementação

Em ordem cronológica. O padrão que emerge é relevante por si só: **quase todo problema sério
veio do banco, não da aplicação** — e vários só apareceram porque alguém mediu ou olhou, em
vez de assumir que estava certo.

### 2.1 · Flyway pulando a primeira migration em silêncio · 27/08

O schema `ADMIN` do Autonomous nunca está vazio, então o Flyway recusa migrar sem
`baselineOnMigrate=true`. Ligar só isso, porém, **não basta**: o padrão de `baselineVersion`
é `1`, o que faz o Flyway considerar a `V1` como já aplicada e começar da `V2` — criando um
banco sem tabela nenhuma e com o seed falhando por referência inexistente.

**Correção:** `baselineVersion=0` explícito, documentado no `README.md`.

**Por que importa:** foi uma falha *silenciosa*. O Flyway reportou sucesso. Só o erro
seguinte, no seed, revelou o problema.

### 2.2 · Acentuação: o primeiro diagnóstico estava errado · 27/08

Nomes com acento não casavam na comparação. A primeira tentativa (`V4`) usou
`CONVERT(nome, 'US7ASCII')`, caminho recomendado em boa parte do material sobre Oracle.
**Não funcionou:** produziu `CONCEIC?O` — a cedilha virou literal `?`, e o `?` passou a
fazer parte da string comparada.

**Correção:** `V5` substituiu por `TRANSLATE` com mapa explícito de acentos.

**O registro disso ficou no comentário da própria `V5`**, em vez de a `V4` ser apagada da
história. Migration aplicada não se edita — e tentativa que falhou é informação.

### 2.3 · Deadlock no reset da demonstração · 27/08

O script de reset falhava com `ORA-12860` ao apagar `registro_origem`, depois de esvaziar as
tabelas filhas na mesma transação.

**O que foi observado:**
- acontecia com a aplicação parada, então não era disputa com outra sessão;
- criar os índices que faltavam nas chaves estrangeiras (`V7`) **não resolveu**;
- fechar a transação com `COMMIT` antes de apagar as linhas pai resolveu.

**Hipótese, declarada como hipótese:** `decisao_identificacao` tem duas FKs para a mesma
tabela pai (`registro_a_id` e `registro_b_id`), e resolver as duas na mesma transação em que
as filhas foram apagadas leva a sessão a esbarrar no próprio bloqueio.

**Isto não foi confirmado em trace nem com um DBA.** Chegamos ao `COMMIT` pelo comportamento
observado, e o comentário no `reset-demo.sql` diz exatamente isso. O caminho para investigar
de verdade seria o trace da sessão e `DBA_BLOCKERS` no momento do erro.

### 2.4 · Recomendação de conectividade errada · 27/08

Para evitar a complexidade da wallet, foi recomendado criar o banco com acesso TLS simples,
deixando o mTLS desligado. **A recomendação estava errada:** com "Secure access from
everywhere" e sem lista de IPs permitidos, o console da Oracle só oferece Mutual TLS.

**Correção:** voltamos para wallet com `ojdbc11` + `oraclepki`, que é o que está em uso.

**Custo:** uma rodada de configuração perdida. É o tipo de erro que vale registrar porque
veio de afirmar sem verificar.

### 2.5 · Fuso horário · agosto

A tela mostrava a última sincronização três horas no futuro: o banco grava
`TIMESTAMP WITH TIME ZONE` em UTC e a formatação não convertia.

**Correção:** `atZoneSameInstant(ZoneId.of("America/Sao_Paulo"))` no `ConexaoService`.

**Por que importa:** não quebrou nada. Só mostrava a informação errada — que em tela de
auditoria é pior que quebrar.

### 2.6 · Ponto-e-vírgula dentro de comentário · agosto

Um `;` no meio de uma linha de comentário SQL partiu o script e gerou `ORA-00900`.
Verificado empiricamente: `;` no fim da linha é inofensivo, no meio não é.

**Virou prática:** todo script novo passa por uma checagem de `;` em linha de comentário
antes de rodar. Pegou de novo na `V11`, antes de ela ir para o banco.

### 2.7 · O gargalo de 197× · 03/09

Tratado em detalhe na seção 4.

### 2.8 · O banco parando sozinho · 13/10

Depois de 38 dias sem uso, a aplicação não subiu: `ORA-12514`, serviço não registrado no
listener. Não era credencial nem wallet — **o listener respondeu e recusou**, o que indica
instância fora do ar.

**Causa:** Autonomous Database na camada Always Free **para automaticamente após 7 dias de
inatividade**, e é reclamado após 90.

**Correção:** religar pelo console (leva ~2 minutos). Está registrado como risco R1, porque
no dia de uma apresentação isso é falha crítica.

### 2.9 · Dois bugs que só a tela revelou · 13/10

Ambos na tela de faturamento, ambos passaram pelo compilador com `strictTemplates` ligado:

1. O aviso de falha de conexão e o indicador de carregamento apareciam **ao mesmo tempo** —
   a tela afirmava duas coisas contraditórias.
2. Uma guia com problema corrigível **e** problema de identidade mostrava a caixa "Aplicar
   Carteirinha" e, logo abaixo, "Não há o que corrigir nesta guia", sem botão para agir. A
   condição tratava os dois tipos de alerta como mutuamente exclusivos, e eles não são.

**O segundo era erro de modelagem do domínio, não de código.** Nenhum teste de tipo pegaria.

**Consequência para o roadmap:** é a evidência concreta de por que testes automatizados e
teste com usuário estão priorizados na Fase 7.

---

## 3. Riscos técnicos

Ordenados por quanto doem, não por quanto são prováveis.

| # | Risco | Probabilidade | Impacto | Mitigação |
|---|---|---|---|---|
| R1 | Banco parado por inatividade no dia da apresentação | **Alta** | Crítico | Ligar e conferir na véspera; a prazo, sair do Always Free |
| R2 | Auditoria sem usuário real (sem autenticação) | Certo — já é fato | Alto | P0 da Fase 7. Hoje o sistema não é defensável sob LGPD |
| R3 | Regressão silenciosa por ausência de testes | Alta | Alto | P1 da Fase 7, começando pelo motor e pelas 7 regras |
| R4 | Precisão de 100% não se repetir em dado real | Média | Alto | Medida em carga sintética; só piloto com dado real resolve |
| R5 | Guia com CPF errado passar despercebida | Média | Médio | Falta a regra. É um ramo de `UNION ALL` na `V11` |
| R6 | Dependência de `UTL_MATCH` (função proprietária Oracle) | Certo | Médio | Trade-off aceito conscientemente — ver seção 4 |
| R7 | Migration aplicada ser editada por engano | Baixa | Alto | Já aconteceu uma vez e foi revertido na hora. `flyway:repair` existe, mas o hábito é a defesa |
| R8 | SOUNDEX gerar blocos grandes com nome pouco diverso | Baixa | Médio | Medido, e o número atual é **pessimista** — ver seção 4 |

### Sobre o R6, que é escolha e não acidente

`UTL_MATCH.JARO_WINKLER_SIMILARITY` é função nativa do Oracle. Usá-la amarra o produto ao
Oracle.

**Foi decidido assumir isso**, por três razões: o desempenho de comparar texto no banco em
vez de trazer centenas de milhares de linhas para a JVM; o fato de o projeto nascer numa
parceria com a Oracle; e porque a alternativa — reimplementar Jaro-Winkler em Java — trocaria
uma dependência por um bug novo em código crítico.

**O que torna o risco administrável:** a lógica está concentrada em duas views. Trocar de
banco significaria reescrever `vw_candidato_identificacao` e `vw_risco_glosa`, não a
aplicação.

---

## 4. Gargalos de performance e arquitetura

### 4.1 · O gargalo encontrado, medido e corrigido

A primeira medição, com 4 mil registros, levou **47 segundos**. Alto demais para o volume —
e foi esse estranhamento que motivou olhar o plano de execução em vez de aceitar o número.

**Causa:** o `OR` de quatro colunas no blocking levava o otimizador a `MERGE JOIN` + `FILTER`,
ou seja, a **ignorar os índices**. O blocking virava decoração.

**Correção (`V8`):** expandir o `OR` em quatro ramos de `UNION`. Cada ramo volta a usar hash
join.

| | Antes | Depois |
|---|---|---|
| Tempo | 47.500 ms | **241 ms** |
| Ganho | | **197×** |

O resultado foi conferido par a par antes de a migration ser aplicada: saída idêntica.

**A lição já está aplicada adiante.** A `vw_risco_glosa` nasceu com sete ramos de
`UNION ALL` e com os registros de par pendente calculados **uma vez** num CTE — em vez de um
`EXISTS` correlacionado que reexecutaria o motor de identificação para cada guia. Com 14
guias ninguém notaria; com 14 mil, seria o mesmo erro de novo.

### 4.2 · O que o blocking resolve

| | Pares |
|---|---|
| Produto cartesiano, sem blocking | 199.970.001 |
| Teto com blocking | 1.956.616 |
| Efetivamente avaliados | 608.194 |

**Redução de 99,0%.** É o que torna a comparação O(n²) tratável.

### 4.3 · Onde o custo se concentra, e por que o número é pessimista

O bloco de SOUNDEX responde por **98% do custo**: 1.926.856 dos 1.956.616 pares. A causa é
conhecida — a carga sintética tem apenas **230 nomes distintos** em 20 mil registros, o que
torna os blocos artificialmente grandes.

**Com nome real, mais diverso, esse custo cai.** O número medido é teto, não piso.

### 4.4 · Gargalo de arquitetura, não de performance

**21 segundos é tempo de lote, não de tela.** O uso real seria reavaliação periódica, não
consulta interativa. Se precisasse ser interativo, o caminho é processar incrementalmente só
os registros novos — o que não está implementado.

Na tela de faturamento isso já aparece: a consulta de guias demora perceptivelmente mais que
as outras, porque a regra de duplicidade depende do motor de identificação. Com o volume da
demonstração é tolerável; não seria com volume real sem processamento incremental.

---

## 5. Ajustes feitos no desenho por causa desta validação

Mudanças que não estavam no plano e entraram porque a medição ou o erro obrigou.

| Ajuste | Motivado por |
|---|---|
| Blocking com `UNION` em vez de `OR` | §4.1 — 197× |
| `TRANSLATE` em vez de `CONVERT US7ASCII` | §2.2 |
| `COMMIT` entre passos do reset | §2.3 |
| Índices em todas as FKs novas da fase 3 | §2.3 — não resolveu o deadlock, mas continua certo |
| CTE `pendentes` em vez de `EXISTS` correlacionado | §4.1, aplicando a lição antes de doer |
| Regras de glosa em tabela, com `ativo` | Operadoras recusam coisas diferentes |
| Sem limiar de Jaro-Winkler para nome | Os dados se separam em 49 / 96 / 100; qualquer corte entre 50 e 95 daria o mesmo resultado |
| `reset-demo.sql` desfazendo correções de guia | Sem isso, a segunda demonstração não teria dado errado para mostrar |

---

## 6. O que ainda não foi validado

Declarado, não escondido:

- **Comportamento com dado real.** Toda a medição usou carga sintética com deformações
  conhecidas. Erro de digitação, troca de letra e nome social são mais difíceis.
- **Concorrência.** Nenhum teste com dois operadores decidindo ao mesmo tempo. A restrição
  `UNIQUE (registro_origem_id)` protege o caso pior, mas o comportamento da interface sob
  conflito não foi exercitado.
- **Volume na camada de glosa.** As sete regras foram validadas com 14 guias. A view foi
  escrita com a lição de escala aplicada, mas **não foi medida em volume** como o motor de
  identificação foi.
- **Recuperação de falha.** Não há teste de queda do banco no meio de uma transação de
  costura.
- **Usabilidade.** Em andamento — ver
  [teste-usabilidade.md](apresentacao/teste-usabilidade.md).

---

## Documentos relacionados

- [medicao-escala.md](medicao-escala.md) — a medição completa, com método e reprodução
- [arquitetura.md](arquitetura.md) — camadas, ciclo do dado e decisões
- [roadmap.md](roadmap.md) — o que vem na Fase 7, e em que ordem
