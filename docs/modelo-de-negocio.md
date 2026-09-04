# Modelo de negócio

Rascunho de 04/09/2026, para a entrega de 08/09 e para o vídeo pitch de 13/09.

Cobre os três buracos apontados por duas fontes independentes — o mentor da Oracle
("por que investir na gente") e o roteiro do vídeo pitch ("monetização", "mercado",
"fechamento").

> **Regra deste documento:** número sem fonte não entra. Onde falta dado, está marcado
> como `[PREENCHER]` com a indicação de onde buscar. Pitch com número inventado morre na
> primeira pergunta de quem conhece o setor — e a banca da Oracle conhece.

---

## 1. O que a Sutura é, com o nome que o mercado usa

A Sutura é um **EMPI — Enterprise Master Patient Index** — combinado com uma camada de
integração.

Isso não é preciosismo de vocabulário. Muda a conversa em três pontos:

- **O problema não precisa ser vendido.** EMPI é uma categoria estabelecida, com
  fornecedores próprios. Ninguém pergunta "isso é problema mesmo?" para uma categoria que
  já tem mercado.
- **Existem comparáveis** para falar de preço e de posicionamento sem chutar.
- **Muda quem vocês são no slide:** de "startup com uma ideia" para "entrante numa
  categoria conhecida, com um recorte próprio".

O recorte próprio é o que segue: EMPI costuma ser produto de rede hospitalar grande, caro e
de implantação longa. A Sutura mira **clínicas e hospitais de médio porte**, que têm o
mesmo problema e não têm o mesmo orçamento.

`[PREENCHER]` — levantar dois ou três fornecedores de EMPI e como se posicionam. Serve para
a pergunta "quem mais faz isso?", que virá.

---

## 2. Quem paga

Não é "o hospital". Hospital não assina nada — pessoas assinam.

| Papel | O que sente a dor | O que ele quer ouvir |
|---|---|---|
| **Faturamento / receita** | Glosa e retrabalho. É quem perde dinheiro visível | Quanto deixa de ser glosado |
| **TI** | Integração que ninguém mantém, planilha que alguém importa na mão | Menos chamado, menos gambiarra |
| **Diretoria clínica** | Paciente que repete exame, histórico partido | Segurança do paciente |
| **Diretoria executiva** | Assina o contrato | Retorno em meses, não em anos |

**A porta de entrada é o faturamento**, porque é o único que consegue colocar a dor em
reais dentro de uma reunião de orçamento. Segurança do paciente convence, mas não aprova
verba sozinha.

Isso tem consequência prática no produto: a tela que fala com o comprador é a de **glosa**
— que ainda não existe. A de identificação de pacientes fala com TI e com a diretoria
clínica, que influenciam mas não assinam.

---

## 3. Como cobrar

### O modelo recomendado: plataforma + conector

```
Assinatura de plataforma      valor fixo mensal por unidade de saúde
+ por conector ativo          valor mensal por sistema integrado
```

**Por que este:**

- O comprador entende. "Pago por sistema que eu integro" é uma frase que passa em comitê.
- Cresce com a adoção sem punir o cliente por usar. Integrou o laboratório? Paga mais um
  conector. Não integrou? Não paga.
- Receita previsível, que é o que importa para quem avalia um SaaS.
- O custo real de vocês está no conector — cada ERP novo é trabalho de integração e
  manutenção. O preço acompanha o custo.

### Os modelos descartados, e por quê

| Modelo | Por que não |
|---|---|
| **Por leito** | Padrão em software hospitalar, mas o valor da Sutura não tem relação com leito. Clínica de oncologia com 6 leitos pode ter mais fragmentação de dado que hospital de 200 |
| **Por paciente unificado** | Alinha com o valor entregue, mas é conta variável e imprevisível. Comprador de saúde rejeita conta que não sabe estimar |
| **Por registro processado** | Mesmo problema, pior: parece cobrança de nuvem, e assusta |
| **Percentual da glosa evitada** | Sedutor e péssimo. Exige auditoria conjunta do que teria sido glosado, gera disputa todo mês, e trava a venda em jurídico |

`[PREENCHER]` — as faixas de valor. Só faz sentido depois de fechar as contas da seção 4.

---

## 4. A conta que sustenta o preço

O caminho é **de baixo para cima**. Dizer "o mercado de healthtech vale X bilhões e a gente
pega 1%" é o erro clássico de pitch de aluno: quem ouve desconta na hora, porque esse 1%
nunca tem justificativa.

A conta que se sustenta é a de um cliente só:

```
Guias emitidas por mês                    [PREENCHER]
× taxa de glosa                           [PREENCHER]  % das guias
× valor médio da guia                     [PREENCHER]  R$
= perda mensal com glosa                  R$ ____

× parcela da glosa que é de cadastro      [PREENCHER]  %
= perda endereçável pela Sutura           R$ ____
```

Ao lado disso, o segundo eixo:

```
Horas/mês da equipe em trabalho manual    [PREENCHER]
× custo/hora da equipe administrativa     [PREENCHER]
= custo do trabalho manual                R$ ____
```

**A regra do preço:** a assinatura precisa custar uma fração pequena e óbvia da soma dos
dois. Se o cliente economiza R$ 10 e paga R$ 1, a venda se explica sozinha.

### A taxa de glosa: número público, e o que ele significa de verdade

**Fonte: Observatório Anahp 2025**, da Associação Nacional de Hospitais Privados, edição de
abril de 2025.

> A média de **glosa inicial gerencial** dos hospitais associados à Anahp passou de
> **11,89% em 2023 para 15,89% em 2024**.

No mesmo período, dois indicadores que contam a mesma história:

| Indicador Anahp | 2023 | 2024 |
|---|---|---|
| Glosa inicial gerencial | 11,89% | 15,89% |
| Índice de recebimento | 91,27% | 88,61% |
| Inadimplência das operadoras | 49,96% | 61,53% |

**Seja preciso sobre o que esse número mede**, porque alguém do setor vai perguntar: *glosa
inicial* é o valor retido pela operadora **ainda em fase de negociação** — não é perda
definitiva. Boa parte volta depois de recurso.

Uma outra série da Anahp, com denominador diferente — percentual sobre a **receita
líquida** — registrou 3,63% em 2021 e 4,51% em 2022. Números diferentes porque medem
coisas diferentes. **Citar sempre qual série e qual ano**, senão a comparação não se
sustenta.

`[PREENCHER]` — o índice de **glosa aceita** (o que permanece perdido após os recursos) não
foi confirmado em fonte primária. Buscar direto no portal de indicadores da Anahp antes de
usar qualquer número dessa natureza.

### O argumento que esses dados sustentam — e é melhor que "perda de bilhões"

A glosa inicial **quase dobrou em dois anos**, de 11,89% para 15,89%. Independentemente de
quanto volta depois do recurso, uma coisa é certa: **o trabalho de contestar explodiu no
mesmo ritmo.**

Cada guia glosada vira alguém conferindo cadastro, juntando documento e reenviando. É
trabalho manual, repetitivo e movido a erro de dado — exatamente o que a Sutura ataca.

Isso é mais forte que dizer "o setor perde bilhões" por três motivos:

1. **É verificável.** Fonte, ano e série citados.
2. **Não depende de a glosa ser perda definitiva.** Mesmo revertida, ela custa trabalho.
3. **É uma tendência, não uma foto.** Problema que piora vende melhor que problema estável.

E o contexto reforça: o índice de recebimento caiu e a inadimplência das operadoras subiu de
50% para 61%. O hospital está sendo espremido dos dois lados — recebe menos e recebe mais
tarde. Quem está sendo espremido compra solução.

### As outras variáveis

- **Guias/mês e valor médio** — dimensionar por porte do cliente alvo.
- **Fatia da glosa que é erro de cadastro**, e não questão clínica — é a parcela que a
  Sutura endereça de fato. `[PREENCHER]`, e vale perguntar ao mentor da Oracle: ele é da
  área e essa é uma pergunta que ele responde melhor que qualquer relatório.
- **Horas em trabalho manual** — o Israel tem experiência direta: a automação que ele fez
  economizou mais de 50 horas por mês. **É experiência profissional dele, não dado de
  cliente da Saúde One** — e deve ser apresentada exatamente assim.

---

## 5. Mercado, contado de baixo para cima

O tamanho não vem de um relatório citado de cabeça. Vem de **contar clientes possíveis**,
com fonte pública que qualquer um pode conferir.

### O parque hospitalar brasileiro

| Dado | Valor | Referência |
|---|---|---|
| Hospitais gerais e especializados | ~6.500 | CNES, fim de 2024 |
| Natureza privada (inclui filantrópicos) | ~60% | CNES, fim de 2024 |
| Leitos no país | ~506 mil | CNES, fim de 2024 |
| Leitos não-SUS | 33% | CNES, fim de 2024 |
| **Hospitais com até 50 leitos** | **68,1% das unidades**, 25,9% dos leitos | CNES, 2024 |

### O funil, e o número que ele produz

```
6.500   hospitais no Brasil
× ~60%  de natureza privada                     ≈ 3.900
× ~32%  acima de 50 leitos (o inverso dos 68,1%) ≈ 1.250 hospitais
```

**A ordem de grandeza do núcleo duro é de mil a dois mil hospitais.** Some a isso as
clínicas especializadas com mais de um sistema — oncologia, diagnóstico por imagem,
laboratórios de rede — que é o perfil da Clínica OncoVida da demonstração.

`[PREENCHER]` — filtrar o CNES por tipo de estabelecimento para dimensionar as clínicas.
O portal é público e permite esse recorte.

```
clientes endereçáveis  ×  ticket anual (seção 3)  =  mercado endereçável
```

Com mil e quinhentos clientes possíveis e um ticket de, digamos, R$ 5 mil por mês, o
endereçável fica na casa de R$ 90 milhões por ano. **Número pequeno perto de "o mercado de
healthtech vale bilhões" — e infinitamente mais defensável**, porque cada fator tem fonte.

### O dado que contraria a estratégia, e é melhor encarar

**68,1% dos hospitais brasileiros têm até 50 leitos.** O segmento que a Sutura mira —
médio porte, com dinheiro e mais de um sistema — é a **minoria** do parque, não a maioria.

Isso não invalida a estratégia, mas muda duas coisas:

1. **Não digam "a maioria dos hospitais sofre com isso e a gente atende a maioria".** É
   falso e verificável em trinta segundos.
2. **O mercado é estreito e de ticket relativamente alto**, não largo e barato. Isso tem
   consequência: venda consultiva, ciclo mais longo, poucos clientes valendo muito. Um
   modelo de autoatendimento não funciona aqui.

Dizer isso antes de perguntarem mostra que vocês olharam o dado. Ser pego dizendo o
contrário mostra o oposto.

### O crescimento não vem do tamanho do mercado

E sim de três movimentos, que vocês já têm:

1. **Cada cliente cresce sozinho.** Começa com dois sistemas conectados e vai somando
   conectores. A receita por cliente sobe sem venda nova.
2. **A interoperabilidade virou obrigação.** RNDS, TISS e a agenda regulatória empurram o
   setor para trocar dados. É demanda que independe de convencer alguém.
3. **O dado limpo vira insumo.** Depois de unificar, a mesma base sustenta automação de
   back-office, prevenção de glosa e alimentação de ferramentas clínicas de IA. É expansão
   dentro de quem já paga.

O item 2 tem um lado incômodo que é melhor vocês levantarem: **se a RNDS resolver
interoperabilidade nacional, parte do problema desaparece.** A resposta é que a RNDS
transporta dado entre instituições; não resolve identidade de paciente dentro de uma que
usa cinco sistemas, nem back-office. Mas digam isso antes de perguntarem.

**O argumento de crescimento** não é "o mercado cresce X% ao ano". É estrutural, e vocês já
o têm:

1. **Cada cliente cresce sozinho.** Começa com dois sistemas conectados e vai somando
   conectores. A receita por cliente sobe sem venda nova.
2. **A interoperabilidade virou obrigação.** RNDS, TISS e a agenda regulatória empurram o
   setor para trocar dados. Isso cria demanda que independe de convencer alguém.
3. **O dado limpo vira insumo.** Depois de unificar, a mesma base sustenta automação de
   back-office, prevenção de glosa e alimentação de ferramentas clínicas de IA. É expansão
   dentro do cliente que já paga.

O item 2 tem um lado incômodo que é melhor vocês levantarem: **se a RNDS resolver
interoperabilidade nacional, parte do problema desaparece.** A resposta é que RNDS
transporta dado entre instituições; não resolve identidade de paciente dentro de uma que
usa cinco sistemas, nem back-office. Mas digam isso antes de perguntarem.

---

## 6. Por que investir nesta equipe

Diferencial é o que o concorrente não copia em três meses. Separando o que é de verdade do
que só enche slide:

### É diferencial

**Vivência real de dentro.** Um dos fundadores trabalha com ERP de saúde e conhece SINAN,
internação, faturamento e glosa por dentro. Isso não se lê em relatório — e aparece no
produto: o caso do homônimo e o caso do acento não foram inventados numa reunião, saíram de
saber como sistema brasileiro grava dado.

**Decisão de identidade auditável.** O produto não costura sozinho: recomenda, exige
decisão humana e grava quem decidiu, quando e contrariando ou não a máquina. Para dado de
saúde isso não é recurso, é condição de entrada.

**Motor no banco, medido.** O record linkage roda dentro do Oracle com `UTL_MATCH`, e há
medição: 20 mil registros, 608 mil pares, 21 segundos, zero falso positivo. E um gargalo
encontrado e corrigido, com 197x de ganho. Poucos projetos de aluno têm número medido no
lugar de opinião.

**Postura de não fingir.** O motor recomenda **não** unir dois pacientes de mesmo nome, e
manda para revisão humana quando falta evidência. Vender cautela é mais difícil que vender
automação — e é o que ganha confiança de quem responde por risco clínico.

### Não é diferencial, e não deve ser apresentado como se fosse

- **"Usamos Oracle Cloud."** Qualquer um usa.
- **"Temos IA."** Todo pitch de 2026 tem. O que vocês têm é um algoritmo específico com
  resultado medido — isso sim vale, e é outra frase.
- **"O time é dedicado."** Todo time diz isso.

---

## 7. O que ainda não tem resposta

Honestidade sobre os buracos, porque a banca vai achar de qualquer jeito:

- **Preço em reais.** Depende dos números da seção 4.
- **Se o ERP libera a API.** É o maior risco do negócio e continua sem validação de campo.
  Era a pergunta prioritária para uma banca de mercado, e ainda vale fazer.
- **Custo de aquisição e ciclo de venda.** Ninguém do grupo vendeu para hospital.
- **A camada de glosa não existe.** É o pilar que fala com o comprador, e está fora do
  escopo entregue.

---

## Fontes

- **Glosa e indicadores financeiros hospitalares** — Observatório Anahp 2025, edição de
  abril de 2025, Associação Nacional de Hospitais Privados. Reportado em
  <https://medicinasa.com.br/observatorio-anahp-2025/>
- **Série de glosa sobre receita líquida** — Indicadores Hospitalares Anahp, dados de
  2021-2022. Reportado em <https://medicinasa.com.br/indicadores-glosa/>
- **Parque hospitalar, leitos e natureza jurídica** — CNES, dados de fim de 2024,
  compilados em estudo setorial da Moody's Local Brasil, janeiro de 2025
- **Distribuição de hospitais por porte** — CNES 2024, via Observatório Hospitalar da
  Fiocruz, <https://observatoriohospitalar.fiocruz.br/>
- **Base primária** — CNES/DATASUS, <https://cnes.datasus.gov.br>

Antes de usar qualquer número no vídeo, **confira na fonte primária**. Os dados aqui vieram
de reportagens que citam as publicações; para um pitch avaliado por gente do setor, cite a
publicação, não a reportagem.
