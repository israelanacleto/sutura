# Pitch final — Sutura

**Fase 7 · Vídeo de até 7 minutos, com demonstração real do sistema em funcionamento.**

> **Meta de 6min40, não 7min00.** Vinte segundos de folga cobrem respiração, corte mal feito
> e a tendência de falar mais devagar na gravação final. Na mentoria de setembro, cinco
> minutos viraram doze — e custou o feedback do mentor da Oracle. Aqui o formato é gravado:
> cronometra, regrava, não entrega um segundo acima.

A ordem abaixo segue **exatamente** a lista do enunciado. Não reordene: quem corrige confere
item por item.

---

## Orçamento de tempo

| # | Bloco | Tempo | Acumulado |
|---|---|---|---|
| 1 | Problema | 40s | 0:40 |
| 2 | Solução | 30s | 1:10 |
| 3 | **Demonstração do MVP** | **120s** | 3:10 |
| 4 | Mercado | 30s | 3:40 |
| 5 | Modelo de negócio | 35s | 4:15 |
| 6 | Tecnologia utilizada (Oracle) | 35s | 4:50 |
| 7 | Diferencial competitivo | 35s | 5:25 |
| 8 | Estratégia de crescimento | 25s | 5:50 |
| 9 | Projeções | 25s | 6:15 |
| 10 | Time e fechamento | 25s | **6:40** |

**A demonstração é quase um terço do vídeo, e é proposital.** O enunciado exige demonstração
real do sistema em funcionamento — é o entregável que a maioria dos grupos não tem.

---

## Antes de gravar

**O banco precisa estar ligado.** O Autonomous Database da camada gratuita para sozinho após
7 dias de inatividade. Conferir em `cloud.oracle.com` → Autonomous Database → `suturadb` →
estado `AVAILABLE`.

Depois:

```bash
cd F:\PJ\FIAP\Sutura\api && mvn spring-boot:run -Dspring-boot.run.profiles=local
```

```bash
cd F:\PJ\FIAP\Sutura\web && npm start
```

E rodar `api/scripts/reset-demo.sql` — sem isso a fila já está resolvida e **não há o que
costurar na frente da câmera**. A conferência esperada é `15 / 1 / 1 / 0 / 12 / 14 / 0 / 14`.

Zoom do navegador em 110%, notificações silenciadas, começar em `/conexoes`.

**Grave em peças separadas e junte na edição:** narração limpa de um lado, captura de tela
de outro. Ninguém consegue narrar e clicar bem ao mesmo tempo, e assim um erro numa peça não
obriga a refazer o resto.

---

## 1 · Problema — 40s

> "Um paciente não tem um histórico. Ele tem pedaços de histórico espalhados por sistemas
> que não se falam.
>
> O hospital usa um ERP. A clínica de oncologia usa outro. O laboratório tem o próprio
> sistema. Cada um enxerga um pedaço — ninguém enxerga a pessoa.
>
> Isso custa de dois jeitos. Custa clinicamente: exame repetido, história recontada, decisão
> tomada com metade da informação. E custa dinheiro: cadastro divergente vira guia glosada,
> que vira retrabalho.
>
> E está piorando. Segundo o Observatório Anahp de 2025, a glosa inicial dos hospitais
> privados passou de 11,89% em 2023 para 15,89% em 2024. Quase dobrou em dois anos."

**Tela:** a marca, ou a tela "Antes da Sutura" com os três cadastros da mesma paciente.

> ⚠️ **Diga "glosa inicial", não "perda".** Glosa inicial é valor retido em negociação; parte
> volta depois de recurso. Quem é do setor sabe, e a imprecisão derruba a credibilidade do
> resto do pitch.

---

## 2 · Solução — 30s

> "A Sutura é uma camada que se conecta por cima dos ERPs que o hospital já usa. Não
> substitui nenhum deles.
>
> Ela recebe os dados de cada sistema, descobre quais registros são a mesma pessoa, devolve
> um histórico único — e, a partir dessa identidade resolvida, valida as guias de
> faturamento antes do envio à operadora.
>
> Na indústria isso tem nome: Master Patient Index. A diferença é que os que existem são
> feitos para grandes redes americanas, e a gente é feito para o hospital brasileiro de
> médio porte."

**Tela:** o diagrama das três camadas, de `docs/arquitetura.md`.

---

## 3 · Demonstração do MVP — 120s

**Tudo é captura de tela editada.** Corte cada espera de carregamento.

### 3a · Ingestão real — 20s

**Tela:** Conexões. Clicar em **Sincronizar agora** no cartão do Tasy.

> "Isso está rodando de verdade, contra um Oracle Autonomous Database na nuvem. Esse botão
> envia um pacote no padrão FHIR R4 — o formato que sistemas de saúde usam para trocar
> informação — e o sistema grava cada registro preservando o documento original, para
> auditoria."

*(a faixa aparece: registros novos e eventos ingeridos)*

### 3b · O caso que mostra o cuidado — 30s

**Tela:** fila de identificação. Abrir o card do **João Carlos Ferreira**, score 10.

> "Dois registros com o nome escrito exatamente igual. Um sistema ingênuo juntaria os dois e
> criaria um prontuário falso, misturando o histórico de duas pessoas. Isso não é erro de
> cadastro, é risco clínico.
>
> A Sutura compara CNS, CPF, data de nascimento e nome da mãe, vê que tudo diverge, e
> recomenda manter separado. Sem nenhuma exceção escrita para esse caso."

### 3c · A costura e o histórico — 30s

**Tela:** costurar os dois pares da Maria Aparecida. Ir para o histórico.

> "Agora o contrário. Esta paciente existe em três sistemas, com três grafias do nome. Ao
> costurar..."

*(corte para o histórico)*

> "...o histórico dela vai de quatro para doze eventos. Seis infusões com intervalo de 28
> dias que antes estavam espalhadas. Nenhum sistema, sozinho, enxergava o tratamento
> inteiro."

### 3d · O dinheiro — 40s

**Tela:** Faturamento.

> "E aqui está a parte que fala com quem paga a conta. Cinquenta e três mil reais em guias
> prontas para envio, todas com algum problema de cadastro."

**Tela:** abrir a guia **G-2026-0103**.

> "O sistema não diz só que está errado — ele mostra o quê. A carteirinha na guia termina em
> cinquenta; no cadastro unificado, em zero-cinco. Dígitos trocados. Essa guia seria
> glosada."

*(marcar a caixa e clicar em Corrigir)*

> "A correção é campo a campo, escolhida por quem opera, e fica gravada com usuário, data e
> hora.
>
> E repare numa coisa: **lembra da paciente que a gente costurou agora há pouco? Os alertas
> de duplicidade dela sumiram desta tela sozinhos.** Ninguém tocou nessas guias. Resolver
> identidade não só limpa alerta — revela divergências que estavam escondidas atrás de
> 'paciente não identificado'."

> 💡 **Esse é o ponto alto do vídeo.** É onde as duas telas se conectam e o produto deixa de
> ser duas funcionalidades e vira uma tese. Se precisar cortar tempo, corte de outro lugar.

---

## 4 · Mercado — 30s

> "O Brasil tem cerca de seis mil e quinhentos hospitais, segundo o CNES. Aproximadamente
> 60% são privados. Destes, cerca de um terço tem mais de cinquenta leitos — que é o nosso
> perfil. Dá algo entre mil e dois mil clientes possíveis, mais as clínicas especializadas
> com mais de um sistema.
>
> E a gente faz questão de dizer o dado que contraria o próprio pitch: 68% dos hospitais
> brasileiros têm até cinquenta leitos. Nós miramos a minoria do parque, não a maioria. É um
> mercado estreito e de ticket alto — venda consultiva, não autoatendimento."

**Tela:** o funil, de `docs/documento-consolidado.md`.

---

## 5 · Modelo de negócio — 35s

> "Assinatura mensal de plataforma por unidade de saúde, mais um valor por sistema
> integrado. O cliente entende a conta: paga pelo que conecta, e ela cresce junto com o uso.
>
> E a conta fecha. Para um hospital de cem leitos, com as nossas premissas, a parcela de
> glosa que vem de erro de cadastro gira em torno de cento e quarenta mil reais por mês. A
> Sutura custaria cerca de seis mil. Uma relação de um para vinte e quatro.
>
> Descartamos cobrar percentual sobre a glosa evitada, que parece atraente e é péssimo:
> exige auditoria conjunta todo mês e trava a venda no jurídico."

**Tela:** a tabela de premissas e a conta do cliente.

---

## 6 · Tecnologia utilizada — 35s

> "A Sutura roda sobre Oracle Autonomous Database 26ai, e a Oracle não é só onde o dado
> mora: **o motor do produto roda dentro do banco.**
>
> A comparação de nomes usa `UTL_MATCH`, com Jaro-Winkler, função nativa do Oracle. O
> agrupamento usa SOUNDEX. São views SQL, não código Java — porque trazer centenas de
> milhares de registros para a aplicação só para comparar texto desperdiçaria exatamente o
> que o banco faz bem.
>
> Por cima disso, Java 21 com Spring Boot e Angular 21. Onze migrations versionadas: subir
> uma instância nova para um cliente é rodar um comando contra um banco vazio."

**Tela:** o diagrama de arquitetura com as duas views em destaque.

---

## 7 · Diferencial competitivo — 35s

> "Três coisas nos separam.
>
> **De onde viemos.** Nosso time trabalha com ERP de saúde. O caso do homônimo e o caso do
> nome sem acento não foram inventados numa reunião — saíram de saber como sistema
> brasileiro grava dado.
>
> **A gente não finge.** O sistema recomenda, a pessoa decide, e quando falta evidência ele
> manda para revisão humana em vez de chutar. Vender cautela é mais difícil que vender
> automação, e é o que ganha a confiança de quem responde por risco clínico.
>
> **A gente mediu.** Vinte mil registros, seiscentos e oito mil pares comparados, vinte e um
> segundos, e zero falsos positivos. E medindo, achamos um gargalo e corrigimos: o mesmo
> resultado passou a sair cento e noventa e sete vezes mais rápido."

---

## 8 · Estratégia de crescimento — 25s

> "Começamos por clínicas especializadas, que decidem mais rápido, e usamos o primeiro caso
> para chegar ao hospital de médio porte.
>
> O crescimento não depende do mercado crescer. Cada cliente cresce sozinho somando
> conectores. A interoperabilidade virou obrigação regulatória, o que cria demanda sem
> precisar convencer ninguém. E o dado limpo vira insumo para automação de back-office
> dentro de quem já paga."

---

## 9 · Projeções — 25s

> "Com as premissas abertas no documento: três clientes no primeiro ano, doze no segundo,
> trinta no terceiro. A receita recorrente anual sai de cento e oitenta e quatro mil reais
> para dois milhões e quatrocentos mil.
>
> A margem bruta por cliente é de oitenta por cento, e **o ponto de equilíbrio é de nove
> clientes.** De mil a dois mil clientes possíveis no mercado, a gente precisa de nove para
> não dar prejuízo.
>
> O primeiro ano é deficitário de propósito: são três pilotos que existem para medir o que
> a gente não sabe — custo de aquisição e ciclo de venda. Nenhum dos dois está na projeção,
> e essa é a maior fragilidade dela."

**Tela:** a tabela de projeção com custo e resultado.

> 💡 **"Nove clientes" é o número mais forte deste bloco.** É concreto, pequeno e
> verificável — o oposto de "vamos pegar 1% de um mercado de bilhões", que quem ouve
> desconta na hora.

---

## 10 · Time e fechamento — 25s

> "Somos cinco alunos de Engenharia de Software, e um de nós trabalha com ERP de saúde — é
> de onde vem o conhecimento que está no produto.
>
> A interoperabilidade deixou de ser diferencial e virou obrigação. Quem resolver identidade
> de paciente com rastreabilidade vai ser infraestrutura desse setor. A gente já tem o motor
> funcionando, medido, sobre a nuvem da Oracle.
>
> A Sutura não é mais um sistema. É a costura entre eles."

**Tela:** o grupo em câmera, ou a marca com o nome dos cinco.

---

## O que cortar, se estourar

Nesta ordem, do menos custoso ao mais:

1. **A frase sobre Master Patient Index**, no bloco 2 — ganha 8s.
2. **O parágrafo dos modelos descartados**, no bloco 5 — ganha 10s.
3. **O bloco 3a**, a ingestão — ganha 20s. Só em último caso: é a prova de que o sistema
   roda de verdade.

**Nunca corte:** o bloco 3d, que é a única parte em que o espectador *vê* o dinheiro
aparecendo; o "zero falsos positivos"; e o dado dos 68%, que é o que mostra que olhamos o
mercado de verdade.

---

## Antes de enviar

- [ ] Duração abaixo de 7:00, conferida no arquivo final
- [ ] A demonstração mostra o sistema **rodando**, não slides sobre o sistema
- [ ] Os dez itens do enunciado aparecem, na ordem
- [ ] "Glosa inicial", nunca "perda", no bloco 1
- [ ] Áudio audível sem fone
- [ ] Texto na tela legível em celular
