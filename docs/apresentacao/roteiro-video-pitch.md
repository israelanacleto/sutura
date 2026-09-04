# Roteiro do vídeo pitch — 13/09

Vídeo de até 5 minutos, para seleção da Banca Final do Challenge. Segue a sequência
sugerida pela coordenação: público, problema, oportunidade, solução, demonstração,
diferencial, monetização, mercado, fechamento.

> **A vantagem que vocês têm aqui:** vídeo é gravado. O problema da mentoria de 03/09 — cinco
> minutos que viraram doze — simplesmente não existe neste formato. Grava, cronometra,
> regrava. Não entreguem um segundo acima.

---

## Orçamento de tempo

Meta de **4min45**, não 5min00. Quinze segundos de folga cobrem respiração, corte mal
feito e a tendência de todo mundo falar mais devagar na gravação final.

| # | Bloco | Tempo | Acumulado |
|---|---|---|---|
| 1 | Abertura, público e problema | 40s | 0:40 |
| 2 | Oportunidade | 20s | 1:00 |
| 3 | Solução | 30s | 1:30 |
| 4 | **Demonstração** | **85s** | 2:55 |
| 5 | Diferencial | 35s | 3:30 |
| 6 | Monetização | 30s | 4:00 |
| 7 | Mercado | 20s | 4:20 |
| 8 | Fechamento | 25s | 4:45 |

**A demonstração é o maior bloco de todos, e é proposital.** Vocês têm um produto que
funciona de verdade contra um Oracle real — a maioria dos concorrentes vai mostrar tela
estática. Isso é vantagem e merece o tempo.

---

## Como gravar

**Grave em três peças separadas e junte na edição.** Não tente fazer tudo de uma vez.

1. **Narração** — áudio limpo, lendo o roteiro, quantas tomadas precisar
2. **Captura de tela** — a aplicação rodando, sem áudio, com os cliques do roteiro
3. **Câmera** — só na abertura e no fechamento, se quiserem aparecer

Isso resolve três problemas de uma vez: ninguém precisa narrar e clicar ao mesmo tempo, dá
para cortar as esperas de carregamento, e um erro numa peça não obriga a refazer o resto.

**Duas vozes, não três.** Cada troca de narrador custa segundos e quebra o ritmo. A divisão
natural: **Matheus no negócio** (blocos 1, 2, 3, 6, 7, 8) e **Israel na parte técnica**
(blocos 4 e 5), que é onde a vivência dele em ERP de saúde aparece. O terceiro integrante
entra na abertura e no fechamento em câmera, com o grupo.

**Antes de capturar a tela:** rodar `api/scripts/reset-demo.sql`, subir backend e front,
zoom do navegador em 125%, notificações silenciadas.

---

## 1 · Abertura, público e problema — 40s

> "Um paciente não tem um histórico. Ele tem pedaços de histórico espalhados por sistemas
> que não se falam.
>
> O hospital usa um ERP. A clínica de oncologia usa outro. O laboratório tem o próprio
> sistema. Cada um enxerga um pedaço — ninguém enxerga a pessoa.
>
> Quem paga essa conta é o hospital de médio porte: exame repetido, história recontada, e
> uma equipe de faturamento gastando dezenas de horas por mês costurando dado na mão."

**Imagem:** o grupo em câmera, ou a tela "Antes da Sutura" com os três cadastros da mesma
paciente. Se usarem a tela, ela sozinha já conta o problema.

---

## 2 · Oportunidade — 20s

> "E o problema está piorando. Segundo o Observatório Anahp 2025, a glosa inicial dos
> hospitais privados passou de 11,89% em 2023 para 15,89% em 2024. No mesmo período, a
> inadimplência das operadoras subiu de 50% para 61%.
>
> Cada guia glosada vira alguém conferindo cadastro e reenviando. O hospital recebe menos,
> recebe mais tarde, e trabalha mais para receber."

**Imagem:** número na tela — `11,89% → 15,89%` — com a fonte escrita embaixo.

> ⚠️ **Diga "glosa inicial", não "perda".** Glosa inicial é valor retido em negociação;
> parte volta depois de recurso. Quem é do setor sabe disso, e a imprecisão derruba a
> credibilidade do resto.

---

## 3 · Solução — 30s

> "A Sutura é uma camada que se conecta por cima dos ERPs que o hospital já usa. Não
> substitui nenhum deles.
>
> Ela recebe os dados de cada sistema, descobre quais registros são a mesma pessoa, e
> devolve um histórico único — mantendo a origem de cada informação rastreada.
>
> Na indústria isso tem nome: é um Master Patient Index. A diferença é que os que existem
> são feitos para grandes redes, e a gente é feito para quem não tem esse orçamento."

**Imagem:** o diagrama das camadas, de `docs/arquitetura.md`.

---

## 4 · Demonstração — 85s

**Tudo é captura de tela editada.** Corte cada espera; ninguém precisa ver carregamento.

### 4a · Ingestão real — 20s

**Tela:** Conexões. Clique em **Sincronizar agora** no cartão do Tasy.

> "Isso aqui está rodando de verdade, contra um Oracle Autonomous na nuvem. Esse botão
> envia um pacote de dados no padrão FHIR — o formato que sistemas de saúde usam para
> trocar informação — e o sistema grava cada registro **preservando o documento original**,
> para auditoria."

*(a faixa aparece: 2 registros novos, 3 eventos)*

### 4b · O caso que mostra o cuidado — 30s

**Tela:** fila de identificação. Abra o card do **João Carlos Ferreira**, score 10.

> "Dois registros com o nome escrito exatamente igual. Um sistema ingênuo juntaria os dois
> — e criaria um prontuário falso, misturando o histórico de duas pessoas. Isso não é erro
> de cadastro, é risco clínico.
>
> A Sutura compara CNS, CPF, data de nascimento e nome da mãe, vê que **tudo** diverge, e
> recomenda **manter separado**. E a decisão fica gravada com usuário, data e hora."

### 4c · A costura e o resultado — 35s

**Tela:** costure os dois pares da Maria Aparecida. Vá para o histórico.

> "Agora o contrário. Esta paciente existe em três sistemas, com três grafias diferentes do
> nome. Ao costurar..."

*(corte para o histórico)*

> "...o histórico dela vai de quatro para doze eventos. Seis infusões com intervalo de 28
> dias, que antes estavam espalhadas: as aplicações no sistema da clínica, a cirurgia e as
> consultas no do hospital, os exames no do laboratório. Nenhum sistema, sozinho, enxergava
> o tratamento inteiro."

**Tela:** clique em **Antes da Sutura**.

> "É assim que ela existe hoje."

*(segure 2 segundos nas três colunas, em silêncio — a imagem fala sozinha)*

---

## 5 · Diferencial — 35s

> "Três coisas nos separam.
>
> **A primeira é de onde viemos.** Nosso time trabalha com ERP de saúde. O caso do nome sem
> acento, o caso do homônimo — não foram inventados numa reunião, saíram de saber como
> sistema brasileiro grava dado.
>
> **A segunda é que a gente não finge.** O sistema recomenda, e a pessoa decide. Quando
> falta evidência, ele manda para revisão humana em vez de chutar.
>
> **A terceira é que a gente mediu.** Vinte mil registros, seiscentos e oito mil pares
> comparados, vinte e um segundos, e **zero falsos positivos**. E medindo, achamos um
> gargalo e corrigimos: o mesmo resultado passou a sair 197 vezes mais rápido."

---

## 6 · Monetização — 30s

> "Cobramos assinatura mensal por unidade de saúde, mais um valor por sistema integrado.
>
> O cliente entende a conta: paga pelo que conecta. E ela cresce junto com o uso — integrou
> o laboratório, entra mais um conector.
>
> A conta que sustenta o preço é simples: quanto o hospital deixa de perder com glosa de
> cadastro, mais o custo das horas que a equipe gasta refazendo trabalho manual. A
> assinatura precisa custar uma fração pequena e óbvia disso."

> ⚠️ `[PREENCHER]` — as faixas de valor. Ver `docs/modelo-de-negocio.md`.

---

## 7 · Mercado — 20s

> "O Brasil tem cerca de seis mil e quinhentos hospitais, dos quais aproximadamente 60% são
> privados. Nosso núcleo é o de médio e grande porte, mais as clínicas especializadas com
> mais de um sistema — algo entre mil e dois mil clientes possíveis.
>
> É um mercado estreito e de ticket alto, não largo e barato. E cresce por dentro: cada
> cliente vai somando conectores."

**Imagem:** o funil de `docs/modelo-de-negocio.md`.

---

## 8 · Fechamento — 25s

> "A interoperabilidade deixou de ser diferencial e virou obrigação regulatória. Quem
> resolver identidade de paciente com rastreabilidade vai ser infraestrutura desse setor.
>
> A gente já tem o motor funcionando, medido, sobre a nuvem da Oracle. E conhece a dor por
> dentro, porque trabalha nela.
>
> A Sutura não é mais um sistema. É a costura entre eles."

**Imagem:** o grupo em câmera, ou a marca com o nome dos integrantes.

---

## O que cortar, se estourar

Corte nesta ordem — do menos custoso ao mais:

1. **A frase sobre Master Patient Index**, no bloco 3. Ganha 8s. É bom posicionamento, mas
   não é essencial para quem assiste.
2. **A inadimplência das operadoras**, no bloco 2. Ganha 6s. A glosa sozinha já sustenta.
3. **O terceiro diferencial**, a medição. Ganha 12s. Dói, mas os dois primeiros são mais
   difíceis de copiar.
4. **O bloco 4a**, a ingestão. Ganha 20s. Só se for a última alternativa — sem ele o vídeo
   perde a prova de que o sistema funciona de verdade.

**Nunca corte:** o bloco 4c, que é a única parte em que o espectador *vê* o valor
acontecendo; e o "zero falsos positivos", que é o número mais forte que vocês têm.

---

## Antes de enviar

- [ ] Duração abaixo de 5:00, conferida no arquivo final
- [ ] Números conferidos contra `docs/modelo-de-negocio.md` — nenhum número sem fonte
- [ ] "Glosa inicial", nunca "perda", no bloco 2
- [ ] Áudio audível sem fone
- [ ] Texto na tela legível em celular
- [ ] Formulário preenchido — o vídeo sozinho não conta como entrega
