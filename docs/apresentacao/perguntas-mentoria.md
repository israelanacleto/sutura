# Perguntas para a mentoria

São 10 minutos de feedback. Não dá para fazer tudo — as três marcadas com **★** são as que
eu faria primeiro se o tempo apertar.

O critério para uma pergunta entrar aqui: **só alguém com experiência de mercado responde**.
Pergunta que o Google responde desperdiça a mentoria.

---

## 1. O risco que pode matar o produto

### ★ Na prática, o hospital consegue liberar a API do MV ou do Tasy?

> "Todo o nosso produto assume que o hospital consegue nos dar acesso ao ERP dele. Na
> experiência de vocês, isso acontece? O fornecedor libera, cobra à parte, ou simplesmente
> empurra com a barriga? E quando libera, quem decide: o hospital ou o fornecedor?"

**Por que perguntar:** é a hipótese sobre a qual o negócio inteiro se apoia, e é a única que
não conseguimos testar sozinhos. Se a resposta for "não liberam", não é ajuste de roadmap —
é mudar de produto.

### Já viram alguém conseguir? Como foi?

Se a resposta anterior for "dá, mas é difícil", esta pergunta transforma a dificuldade em
caminho. Peça o exemplo concreto, não o princípio geral.

---

## 2. A concorrência que talvez não estejamos enxergando

### ★ A RNDS não resolve isso?

> "O Ministério da Saúde está construindo a Rede Nacional de Dados em Saúde, também em
> FHIR, com a proposta de unificar dados do cidadão. Isso nos torna redundantes em três
> anos, ou vira um canal a mais para nós? Como vocês veem essa sobreposição?"

**Por que perguntar:** é a objeção mais forte que existe contra a Sutura, e é melhor levantá-la
nós mesmos do que ser pegos por ela. Perguntar demonstra que conhecemos o cenário; esperar
que perguntem demonstra o contrário.

### Por que os próprios ERPs não fizeram isso ainda?

> "Unificar identidade de paciente entre sistemas parece do interesse do próprio MV. Se eles
> não fizeram, é porque não é prioridade, porque não é do interesse comercial deles, ou
> porque é mais difícil do que parece?"

**Por que perguntar:** as três respostas possíveis levam a estratégias completamente
diferentes. A terceira é a mais perigosa para nós.

---

## 3. Modelo de negócio

### Como se cobra por isso?

> "Assinatura por leito, por paciente unificado, por integração ativa, por volume de
> registros? O que o hospital aceita e o que ele rejeita na hora?"

**Por que perguntar:** temos "SaaS por assinatura" no pitch, o que na prática não diz nada.
Quem vendeu para hospital sabe qual métrica passa no comitê de compras e qual trava.

### Quem assina o contrato, e quanto tempo leva?

> "Numa clínica de médio porte, quem é o dono dessa decisão — TI, faturamento, diretoria
> clínica? E qual é o ciclo de venda realista: três meses, um ano?"

**Por que perguntar:** muda completamente o que precisa existir no produto. Venda para TI
pede documentação de integração; venda para faturamento pede ROI em reais.

---

## 4. Produto

### ★ Nossa aposta foi a identificação de pacientes. Foi a aposta certa?

> "Dos três pilares do pitch — integração, unificação de histórico e automação de
> back-office — construímos primeiro a identificação, porque nos pareceu o problema mais
> difícil e o mais defensável. Se vocês fossem vender, começariam por aí ou pela glosa, que
> dá para mostrar em reais?"

**Por que perguntar:** é a maior decisão que já tomamos, e a única em que ainda dá tempo de
mudar antes da entrega de 08/09.

### Existe quem revise a fila de identificação?

> "Nosso desenho assume que alguém no hospital olha os casos duvidosos e decide. Esse papel
> existe de verdade? É do faturamento, do SAME, da TI? Se não existir, o produto precisa
> decidir sozinho — o que é bem mais arriscado com dado clínico."

**Por que perguntar:** essa suposição está embutida em cada tela e nunca foi validada com
ninguém que trabalhe em hospital.

### O caso do homônimo assusta ou tranquiliza?

> "Mostramos o sistema recusando unir dois pacientes de mesmo nome. Isso passa segurança,
> ou levanta o medo de que em algum outro caso ele vá unir errado?"

**Por que perguntar:** foi a decisão de design de que mais nos orgulhamos. Vale saber se ela
comunica o que a gente acha que comunica.

---

## 5. Feedback direto sobre a apresentação

### O que não ficou claro?

Pergunta aberta, feita cedo no tempo de feedback — não no último minuto, quando já não dá
para aprofundar.

### Se vocês fossem o comprador, que pergunta fariam que a gente não soube responder?

**Por que perguntar:** é a forma mais rápida de descobrir o buraco do pitch. Muito melhor
ouvir isso hoje do que na banca final.

---

## 6. Próxima fase

### Faltam 12 dias até 08/09. Onde eles rendem mais?

> "As opções são: conectar um segundo ERP, começar a camada de prevenção de glosa, ou
> aprofundar a identificação com dado mais realista. O que vocês acham que fortalece mais a
> entrega?"

**Por que perguntar:** é a pergunta mais acionável da lista. A resposta vira o backlog de
amanhã.

---

## O que NÃO perguntar

- **"O que vocês acharam?"** — convida elogio educado, não informação.
- **"Vocês usariam?"** — todo mundo diz que sim numa mentoria, e não custa nada dizer.
- **Qualquer coisa que o Google responda** — o que é FHIR, o que é LGPD, quanto vale o
  mercado de healthtech. Gasta o tempo escasso deles com o que a gente consegue sozinho.
- **Perguntas sobre a nota** — a mentoria é para o produto.

---

## Uma dica sobre como ouvir

Quando a resposta for desconfortável, **anote em vez de defender**. O reflexo natural é
explicar por que o mentor está enganado — e isso encerra a conversa exatamente onde ela
ficaria útil. "Interessante, me conta mais" extrai três vezes mais que "é, mas a gente
pensou nisso porque...".
