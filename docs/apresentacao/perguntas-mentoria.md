# Perguntas para a mentoria — banca da Oracle

São 10 minutos de feedback. As três marcadas com **★** são as primeiras se o tempo apertar.

O critério: **perguntar à Oracle o que só a Oracle responde.** Pergunta de mercado
genérica desperdiça uma banca que domina banco de dados, cloud e — desde a compra do
Cerner — sistema de saúde.

---

## Antes de perguntar, diga isto

O slide da stack lista Autonomous Database, AI Vector Search, Object Storage, OML, OCI
Generative AI, Document Understanding, OCI Language, ORDS, APEX, API Gateway, Data Safe e
Terraform. **Implementado: Autonomous Database.**

Diga antes que perguntem:

> "Esse slide é a arquitetura alvo, não o que está implementado. Hoje roda Autonomous
> Database; o resto é o desenho para onde vamos."

Uma frase transforma uma pegadinha em maturidade de engenharia. Guardar silêncio sobre isso
diante de quem fabrica esses produtos é o pior caminho possível.

---

## 1. Posicionamento — a pergunta que só eles respondem

### ★ A Oracle é dona do Oracle Health. Onde uma camada como a nossa se encaixa?

> "Desde a aquisição do Cerner, a Oracle não é só o banco: é também um sistema de saúde.
> Uma camada que costura dados por cima de ERPs de terceiros faz sentido no ecossistema de
> vocês, ou vocês veem esse problema sendo resolvido dentro do próprio produto?"

**Por que perguntar:** é o único lugar onde essa banca tem uma visão que ninguém mais tem —
eles estão dos dois lados do problema. E a resposta diz se a Sutura é parceira ou
redundante no mundo Oracle.

---

## 2. Engenharia — falar a língua deles

### ★ O record linkage dentro do banco se sustenta em escala?

> "Calculamos o score com `UTL_MATCH.JARO_WINKLER_SIMILARITY` dentro do Oracle, com blocking
> por CNS, CPF, data de nascimento e `SOUNDEX`. Funciona bem no nosso volume. Numa rede
> hospitalar com milhões de registros, isso se sustenta — ou a partir de certo ponto vocês
> partiriam para outra abordagem?"

**Por que perguntar:** é pergunta de engenheiro para engenheiro, sobre a decisão técnica
central do projeto. E abre espaço para eles sugerirem o que a gente não conhece.

### ★ Onde o AI Vector Search entraria de verdade?

> "Colocamos AI Vector Search no desenho e não usamos. Se fôssemos usar nesse problema, ele
> entraria na similaridade de nomes — ou em outra parte que a gente não está enxergando?"

**Por que perguntar:** converte o serviço prometido e não entregue em pergunta legítima, em
vez de dívida escondida. E a resposta pode redirecionar a fase 3.

### Dos serviços que listamos, quais fazem sentido e quais são ruído?

> "Listamos doze serviços da Oracle no slide de arquitetura. Sendo francos: quantos desses
> resolvem um problema que a gente tem de verdade, e quantos só engordam o slide?"

**Por que perguntar:** ninguém melhor para dizer que a Sutura não precisa de metade deles.
E a pergunta demonstra que a gente sabe a diferença entre arquitetura e vitrine.

---

## 3. Operação e conformidade

### Como se mascara dado de saúde fora de produção?

> "Dado de saúde é sensível pela LGPD. Colocamos Data Safe no desenho para mascaramento em
> ambiente não-produtivo. Na prática, é isso mesmo que se usa, e o que costuma dar errado?"

### Do Always Free para produção, qual é o caminho?

> "Hoje rodamos em Autonomous Always Free. Para uma clínica real com dado de paciente, qual
> é o salto — de configuração, de custo e de conformidade?"

**Por que perguntar:** é a ponte entre projeto acadêmico e produto, e eles sabem exatamente
onde as pessoas tropeçam.

---

## 4. Feedback direto

### O que não ficou claro?

Faça cedo no tempo de feedback, não no último minuto.

### Que pergunta vocês fariam que a gente não soube responder?

A forma mais rápida de achar o buraco do pitch — muito melhor ouvir hoje que na banca final.

---

## 5. Próxima fase

### Faltam 12 dias até 08/09. Onde eles rendem mais?

> "As opções são: conectar um segundo ERP, começar a camada de prevenção de glosa, ou
> aprofundar a identificação. O que fortalece mais a entrega?"

A resposta vira o backlog de amanhã.

---

## Guardar para outra banca

Estas são boas perguntas, mas **de mercado** — a banca da Oracle não é o melhor público para
elas. Guarde para a mentoria com perfil de negócio:

- Se o hospital consegue de fato liberar a API do MV ou do Tasy
- Se a RNDS torna a Sutura redundante
- Como se cobra: por leito, por paciente, por integração
- Quem assina o contrato e qual o ciclo de venda

---

## O que não perguntar

- **"O que vocês acharam?"** — convida elogio educado, não informação.
- **"Vocês usariam?"** — todo mundo diz que sim numa mentoria.
- **O que a documentação da Oracle responde** — o que é Autonomous, o que é OCI. Gasta o
  tempo escasso deles com o que a gente lê sozinho.
- **Perguntas sobre a nota.**

---

## Como ouvir

Quando a resposta for desconfortável, **anote em vez de defender**. O reflexo de explicar
por que o mentor está enganado encerra a conversa exatamente onde ela ficaria útil.
"Interessante, me conta mais" extrai três vezes mais que "é, mas a gente pensou nisso
porque...".

Designem **uma pessoa para anotar**, e que não seja quem está respondendo.
