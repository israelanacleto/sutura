# Teste de usabilidade — roteiro e registro

Fase 6 do Enterprise Challenge, Parte 5. Mínimo de dois participantes.

> **Os resultados deste documento são preenchidos com dados reais.** As seções de registro
> estão vazias de propósito. Inventar feedback de usuário invalida a atividade inteira — e
> o enunciado avisa que plágio zera para todo o grupo.

---

## Antes de começar

**Quem convidar.** Em ordem de preferência:

1. Alguém que trabalhe com faturamento, recepção ou cadastro em saúde — enxerga o problema
   de dentro e vai reclamar das coisas certas.
2. Alguém administrativo de qualquer área — lida com cadastro, planilha e conferência.
3. Qualquer pessoa não técnica.

**Quem NÃO convidar:** ninguém do grupo, e ninguém que já tenha visto o projeto. Quem
conhece a solução não consegue mais se perder nela, e é justamente o se perder que a gente
quer medir.

**Preparar o ambiente** (uns 10 minutos antes):

```bash
cd F:\PJ\FIAP\Sutura\api && mvn spring-boot:run -Dspring-boot.run.profiles=local
```

```bash
cd F:\PJ\FIAP\Sutura\web && npm start
```

Rodar `api/scripts/reset-demo.sql` antes de **cada** participante — senão o segundo pega a
tela já mexida pelo primeiro e as duas sessões não são comparáveis.

Zoom do navegador em 125%, notificações silenciadas, começar em `/conexoes`.

**Combinar com a pessoa:** 15 minutos, tela compartilhada, ela controla o mouse. Pedir
autorização para gravar; se recusar, anotar à mão. Não coletar nenhum dado pessoal dela — o
registro é anônimo, identificado só por "Participante 1" e "Participante 2".

---

## A regra que faz o teste valer

**Não ajude.** O instinto de socorrer é forte e destrói o dado.

Quando a pessoa perguntar "é aqui que eu clico?", responda sempre com uma devolutiva:

> "O que você faria?"

Quando ela travar, conte até dez em silêncio antes de intervir. O silêncio é
desconfortável para você e informativo para o projeto.

Peça para ela **pensar em voz alta**: "vai narrando o que você está procurando, mesmo que
pareça óbvio". O que ela diz enquanto procura vale mais que o clique final.

---

## As tarefas

Leia cada uma em voz alta, sem explicar a tela antes. Não use as palavras "costurar",
"glosa" ou "identificação" — se a interface não ensinar esses conceitos sozinha, é
informação que falta na interface.

### Tarefa 1 — Entender o problema do dia
> "Você acabou de assumir o setor de faturamento deste hospital. Olhando o sistema, me diz:
> tem algum dinheiro em risco hoje? Quanto, e por quê?"

**O que observar:** ela acha a tela de Faturamento sozinha? O número grande comunica
sozinho? Ela entende *por que* o valor está em risco, ou só lê o número?

### Tarefa 2 — Encontrar o pior caso
> "Entre essas cobranças com problema, encontre a de maior valor e me explica o que está
> errado nela."

**O que observar:** a ordenação por gravidade ajuda ou atrapalha? Ao abrir, ela entende a
comparação lado a lado? As palavras "na guia" e "no cadastro unificado" significam algo
para ela?

### Tarefa 3 — Resolver
> "Agora resolve esse problema."

**O que observar:** ela percebe que precisa marcar o campo antes de o botão habilitar? Isso
irrita ou tranquiliza? Ela repara que a ação fica registrada com usuário e hora?

### Tarefa 4 — O caso sem saída
> "Olha essa outra cobrança aqui (a da Ana Beatriz Lima, R$ 4.500). Essa não tem botão de
> corrigir. Por que você acha que não tem, e o que você faria?"

**O que observar:** ela entende que o problema está na identidade do paciente, e não na
cobrança? Ela acha o caminho para a fila de identificação? Essa é a tarefa mais difícil do
roteiro, e é de propósito.

### Tarefa 5 — O efeito cruzado
> "Resolve aquele outro assunto que o sistema apontou, e depois volta para a tela do
> dinheiro. Mudou alguma coisa?"

**O que observar:** ela percebe que alertas sumiram sem ela ter tocado nas cobranças? Ela
consegue explicar a ligação entre as duas telas? Essa é a tese do produto inteiro — se não
passar aqui, não passa para um cliente.

---

## Depois das tarefas

Três perguntas, nessa ordem:

1. "Com suas palavras, o que esse sistema faz?"
2. "Se você discordasse de uma sugestão dele, o que faria?"
3. "O que faltou, ou o que te confundiu?"

A pergunta 1 é a mais importante. Se as duas pessoas descreverem o produto de maneiras
muito diferentes, o problema não é delas.

---

## Registro — Participante 1

| | |
|---|---|
| Perfil (sem identificar a pessoa) | |
| Data e duração | |

| Tarefa | Concluiu? | Tempo | Onde hesitou ou errou |
|---|---|---|---|
| 1 — Entender o problema | | | |
| 2 — Encontrar o pior caso | | | |
| 3 — Resolver | | | |
| 4 — O caso sem saída | | | |
| 5 — O efeito cruzado | | | |

**Frases ditas** (transcrever literalmente, inclusive as negativas):

-

**Respostas às três perguntas:**

1.
2.
3.

---

## Registro — Participante 2

| | |
|---|---|
| Perfil (sem identificar a pessoa) | |
| Data e duração | |

| Tarefa | Concluiu? | Tempo | Onde hesitou ou errou |
|---|---|---|---|
| 1 — Entender o problema | | | |
| 2 — Encontrar o pior caso | | | |
| 3 — Resolver | | | |
| 4 — O caso sem saída | | | |
| 5 — O efeito cruzado | | | |

**Frases ditas:**

-

**Respostas às três perguntas:**

1.
2.
3.

---

## Melhorias identificadas

Preencher **depois** das duas sessões. Só entra aqui o que foi observado — não o que a
gente já achava antes do teste.

| # | O que observamos | Melhoria | Entra na Fase 7? |
|---|---|---|---|
| 1 | | | |
| 2 | | | |
| 3 | | | |

**Critério:** um problema que apareceu nos **dois** participantes é problema da interface.
Um que apareceu em um só pode ser da pessoa — anotar, mas não priorizar sem mais evidência.
