# Teste e validação do MVP — Fase 7

Parte 2 da atividade. **Mínimo de três usuários do público-alvo.**

> **Os registros deste documento são preenchidos com o que aconteceu de verdade.** As
> tabelas estão vazias de propósito. Na Fase 6 o professor reparou nos formulários em branco
> e descontou 0,3 — preencher com depoimento inventado trocaria um desconto por uma fraude,
> e o enunciado avisa que plágio zera para o grupo inteiro.

---

## O que mudou em relação à Fase 6

Esta fase pede **outra coisa**, e o roteiro anterior não serve como está:

| | Fase 6 | Fase 7 |
|---|---|---|
| Participantes | 2 | **3, do público-alvo** |
| O que registrar | feedback e melhorias | dificuldades, feedback **e ajustes feitos depois** |
| Pergunta de fundo | a interface é clara? | **o MVP resolve o problema identificado na Fase 5?** |

O item decisivo é o terceiro: **"ajustes realizados após os testes"**. Não basta coletar —
é preciso corrigir algo e registrar que foi corrigido por causa do teste. Isso está previsto
na seção 5.

---

## 1 · Quem convidar

**Público-alvo** aqui significa gente que lida com cadastro, conferência ou cobrança —
não qualquer pessoa. Em ordem de preferência:

1. **Quem trabalha com faturamento, recepção ou cadastro em saúde.** Enxerga o problema de
   dentro e reclama das coisas certas.
2. **Quem trabalha com cobrança, conciliação ou back-office administrativo** em qualquer
   setor. A mecânica de conferir dado divergente é a mesma.
3. **Qualquer pessoa administrativa** que lide com planilha e conferência no dia a dia.

**Quem NÃO convidar:** ninguém do grupo, e ninguém que já tenha visto o projeto. Quem
conhece a solução não consegue mais se perder nela — e é o se perder que a gente mede.

> ⚠️ **Se convidar alguém de uma empresa de saúde:** a pessoa participa como usuária de
> teste, nada além disso. **Não abra, não cite e não colete nenhum dado de cliente, paciente
> ou sistema do empregador dela.** A base da demonstração é fictícia e assim permanece. No
> registro, a pessoa aparece só como "Participante 1", sem nome e sem empresa.

---

## 2 · Preparar o ambiente

**O Autonomous Database precisa estar ligado.** Ele para sozinho após 7 dias de
inatividade. Conferir em `cloud.oracle.com` → Autonomous Database → `suturadb` →
`AVAILABLE`.

```bash
cd F:\PJ\FIAP\Sutura\api && mvn spring-boot:run -Dspring-boot.run.profiles=local
```

```bash
cd F:\PJ\FIAP\Sutura\web && npm start
```

**Rodar `api/scripts/reset-demo.sql` antes de CADA participante.** Sem isso o segundo pega a
tela já mexida pelo primeiro, e as sessões deixam de ser comparáveis. A conferência esperada
é `15 / 1 / 1 / 0 / 12 / 14 / 0 / 14`.

Zoom do navegador em 110%, notificações silenciadas, começar em `/conexoes`.

**Combinar com a pessoa:** 15 minutos, tela compartilhada, **ela controla o mouse**. Pedir
autorização para gravar; se recusar, anotar à mão.

---

## 3 · A regra que faz o teste valer

**Não ajude.** O instinto de socorrer é forte e destrói o dado.

Quando a pessoa perguntar *"é aqui que eu clico?"*, devolva sempre:

> **"O que você faria?"**

Quando ela travar, conte até dez em silêncio antes de intervir. O silêncio é desconfortável
para você e informativo para o projeto.

Peça para **pensar em voz alta**: *"vai narrando o que você está procurando, mesmo que pareça
óbvio"*. O que ela diz enquanto procura vale mais que o clique final.

**Não use as palavras "costurar", "glosa" ou "identificação"** ao ler as tarefas. Se a
interface não ensinar esses conceitos sozinha, é informação que falta na interface.

---

## 4 · As tarefas · ~12 minutos

### Tarefa 1 — O dinheiro do dia *(2 min)*
> "Você acabou de assumir o setor de faturamento deste hospital. Olhando o sistema, me diz:
> tem algum dinheiro em risco hoje? Quanto, e por quê?"

**Observar:** acha a tela sozinha? O número grande comunica? Entende *por que* está em risco,
ou só lê o valor?

### Tarefa 2 — O pior caso *(2 min)*
> "Entre essas cobranças com problema, acha a de maior valor e me explica o que está errado
> nela."

**Observar:** a ordenação ajuda ou atrapalha? Entende a comparação lado a lado? "Na guia" e
"no cadastro unificado" significam algo para ela?

### Tarefa 3 — Resolver *(2 min)*
> "Agora resolve esse problema."

**Observar:** percebe que precisa marcar o campo antes de o botão habilitar? Isso irrita ou
tranquiliza? Repara que fica registrado com usuário e hora?

### Tarefa 4 — O caso sem saída *(3 min)*
> "Olha essa outra cobrança aqui, a da Ana Beatriz Lima, de R$ 4.500. Essa não tem botão de
> corrigir. Por que você acha que não tem, e o que você faria?"

**Observar:** entende que o problema está na identidade do paciente, e não na cobrança? Acha
o caminho para a fila? **É a tarefa mais difícil do roteiro, de propósito.**

### Tarefa 5 — O efeito cruzado *(3 min)*
> "Resolve aquele outro assunto que o sistema apontou, e depois volta para a tela do
> dinheiro. Mudou alguma coisa?"

**Observar:** percebe que alertas sumiram sem ela ter tocado nas cobranças? Consegue explicar
a ligação entre as duas telas? **É a tese do produto inteiro — se não passar aqui, não passa
para um cliente.**

---

## 5 · Depois das tarefas · as quatro perguntas

As três primeiras medem compreensão. **A quarta é a que a Fase 7 exige.**

1. "Com suas palavras, o que esse sistema faz?"
2. "Se você discordasse de uma sugestão dele, o que faria?"
3. "O que faltou, ou o que te confundiu?"
4. **"No seu trabalho, cadastro errado de paciente causa problema? Esse sistema ajudaria
   nisso — e como?"**

A pergunta 1 é a mais reveladora: se as três pessoas descreverem o produto de maneiras muito
diferentes, o problema não é delas. A pergunta 4 é a que responde se o MVP resolve o problema
da Fase 5.

---

## 6 · Registro — Participante 1

| | |
|---|---|
| Perfil (sem nome, sem empresa) | |
| Data e duração | |

| Tarefa | Concluiu? | Tempo | Onde hesitou ou errou |
|---|---|---|---|
| 1 — O dinheiro do dia | | | |
| 2 — O pior caso | | | |
| 3 — Resolver | | | |
| 4 — O caso sem saída | | | |
| 5 — O efeito cruzado | | | |

**Frases ditas** (transcrever literalmente, inclusive as negativas):

-

**Respostas às quatro perguntas:**

1.
2.
3.
4.

---

## 7 · Registro — Participante 2

| | |
|---|---|
| Perfil (sem nome, sem empresa) | |
| Data e duração | |

| Tarefa | Concluiu? | Tempo | Onde hesitou ou errou |
|---|---|---|---|
| 1 — O dinheiro do dia | | | |
| 2 — O pior caso | | | |
| 3 — Resolver | | | |
| 4 — O caso sem saída | | | |
| 5 — O efeito cruzado | | | |

**Frases ditas:**

-

**Respostas às quatro perguntas:**

1.
2.
3.
4.

---

## 8 · Registro — Participante 3

| | |
|---|---|
| Perfil (sem nome, sem empresa) | |
| Data e duração | |

| Tarefa | Concluiu? | Tempo | Onde hesitou ou errou |
|---|---|---|---|
| 1 — O dinheiro do dia | | | |
| 2 — O pior caso | | | |
| 3 — Resolver | | | |
| 4 — O caso sem saída | | | |
| 5 — O efeito cruzado | | | |

**Frases ditas:**

-

**Respostas às quatro perguntas:**

1.
2.
3.
4.

---

## 9 · Principais dificuldades encontradas

Preencher depois das três sessões, consolidando.

| # | Dificuldade | Em quantos participantes | Onde, na interface |
|---|---|---|---|
| 1 | | | |
| 2 | | | |
| 3 | | | |

**Critério de leitura:** problema que apareceu em **dois ou mais** é problema da interface.
Em um só, anotar sem priorizar — pode ser da pessoa, e uma amostra de três não distingue.

---

## 10 · Ajustes realizados após os testes

**Esta é a seção que a Fase 7 exige e a Fase 6 não pedia.** Não basta coletar: é preciso
corrigir e registrar.

| # | O que o teste mostrou | Ajuste feito | Onde (arquivo/tela) | Quando |
|---|---|---|---|---|
| 1 | | | | |
| 2 | | | | |

**O que não deu para ajustar agora, e por quê:**

| # | O que o teste mostrou | Por que ficou para depois | Prioridade no roadmap |
|---|---|---|---|
| 1 | | | |

> Ajuste pequeno de interface — rótulo confuso, botão escondido, texto ambíguo — **dá para
> corrigir no mesmo dia**. Mudança de fluxo ou de modelo de dados não; essa vai para o
> roadmap com a justificativa.

---

## 11 · O MVP resolve o problema da Fase 5?

A conclusão, escrita depois dos testes e **ancorada neles**, não na nossa opinião.

**Evidência técnica** (independe do teste):
- O histórico fragmentado vira único — demonstrável em execução.
- A validação de guia passa a ser possível depois da identidade resolvida.
- Medido em volume: 608 mil pares, zero falsos positivos.

**Evidência de uso** (só o teste produz):

| Pergunta | Resposta, com base nas três sessões |
|---|---|
| As pessoas entenderam o que o sistema faz? | |
| Conseguiram concluir as tarefas sem ajuda? | |
| Reconheceram o problema como algo do trabalho delas? | |
| Perceberam a ligação entre identidade e dinheiro (Tarefa 5)? | |

**Conclusão:**

> *(escrever aqui, em até cinco linhas, respondendo diretamente: o MVP resolve, resolve
> parcialmente ou não resolve o problema da Fase 5 — e com base em quê.)*

---

## Se o tempo apertar

Versão curta, **8 minutos por pessoa**, mantendo o essencial:

- **Corte** as Tarefas 2 e 3 (acham e corrigem o problema).
- **Mantenha** a Tarefa 1 (o número comunica?), a Tarefa 5 (a tese do produto) e as
  perguntas 1 e 4.

Três sessões de 8 minutos cabem em meia hora. **Três participantes com roteiro curto valem
mais que dois com roteiro completo** — o mínimo do enunciado é três.
