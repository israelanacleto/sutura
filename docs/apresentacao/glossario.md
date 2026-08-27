# Glossário do roteiro

Os termos que aparecem na demonstração, com uma explicação de bolso e a **frase pronta**
para dizer se alguém perguntar no meio da apresentação.

Ordenado pela ordem em que os termos aparecem no [roteiro](roteiro-demo.md).

---

## Do domínio da saúde

### ERP de saúde
Sistema que roda a operação de um hospital ou clínica: agenda, prontuário, faturamento,
estoque, internação. **MV** e **Tasy** (da Philips) são os dois maiores do Brasil.

> "É o sistema em que o hospital vive o dia inteiro. A Sutura não substitui nenhum deles."

### Prontuário
O registro do paciente dentro de um sistema. O número de prontuário identifica a pessoa
**naquele** sistema — e só nele. É por isso que a mesma pessoa tem números diferentes em
cada lugar, e é exatamente o problema que a Sutura resolve.

### CNS — Cartão Nacional de Saúde
Número de 15 dígitos que identifica o cidadão no SUS. **É o identificador mais confiável
para dizer que dois registros são a mesma pessoa**, e por isso tem o maior peso no motor:
45 dos 100 pontos.

> "É o RG do paciente dentro do sistema de saúde brasileiro."

### Glosa
Quando a operadora **recusa pagar** um procedimento já realizado. Boa parte das glosas não
vem de erro clínico, mas de cadastro: dado divergente, código errado, guia mal preenchida.
O hospital fez o atendimento e não recebe.

> "É o hospital trabalhar e não receber, por erro de papel."

### LIS — Laboratory Information System
O sistema do laboratório. No projeto é o "Lab Alpha", a terceira fonte de dados da Maria.

### ANS / TISS
A **ANS** é a agência que regula os planos de saúde. O **TISS** é o padrão obrigatório que
ela define para a troca de informações entre prestador e operadora — guias, cobranças,
faturamento.

### LGPD
Lei Geral de Proteção de Dados. Dado de saúde é classificado como **dado sensível**
(art. 11), o que exige cuidado extra: registrar quem acessou, quem decidiu o quê e quando.
É a razão de existir a trilha de auditoria nas decisões de costura.

---

## Interoperabilidade — como o dado chega

### FHIR
*Fast Healthcare Interoperability Resources.* O padrão moderno de troca de dados de saúde,
mantido pela HL7. Define **recursos** com formato fixo — `Patient`, `Procedure`,
`Observation`, `Encounter` — trafegados em JSON sobre HTTP, como qualquer API REST atual.

> "É o formato padrão em que sistemas de saúde falam entre si hoje."

### R4
A **versão** do FHIR. *Release 4*, publicada em 2019, é a versão estável e a mais adotada
no mundo — inclusive no Brasil, que baseia nela o padrão nacional. Existe R5, com adoção
ainda pequena.

> "É a versão do padrão. R4 é a que o mercado usa de verdade."

### Bundle
Um recurso do FHIR que funciona como **envelope**: em vez de mandar um paciente por
requisição, você manda um pacote com vários recursos juntos — os pacientes, os
procedimentos, os exames, tudo num documento só.

**Juntando tudo: um "Bundle FHIR R4" é um pacote de registros de saúde no formato padrão
da indústria, versão 4.** É o que o botão *Sincronizar agora* envia para a API.

> "É o lote de dados que o sistema de origem manda pra gente, no formato padrão."

### HL7 v2
O padrão **anterior** ao FHIR, dos anos 90, ainda dominante em hospital brasileiro. Não é
JSON: são mensagens de texto com campos separados por barra vertical (`|`). Feio, mas vivo
— por isso a tela de Conexões mostra o laboratório entrando por HL7 v2 e a clínica por
FHIR. Sistema de saúde real é essa mistura.

### Ingestão
O ato de **receber e gravar** o dado que veio de fora, sem interpretar nem decidir nada
ainda. Na Sutura, ingerir grava o registro e guarda o documento original junto.

### Registro × evento
Duas coisas diferentes, e a mensagem da sincronização conta as duas separadamente
— *"2 registros novos e 3 eventos ingeridos"*.

- **Registro** é a **pessoa** dentro de um sistema de origem: nome, CNS, CPF, data de
  nascimento, número de prontuário. A mesma pessoa tem um registro em cada sistema onde foi
  atendida — é justamente por isso que existe a fila de identificação.
- **Evento** é o que **aconteceu** com ela: uma infusão, uma consulta, um exame, uma
  cirurgia. Tem data, título e categoria.

No lote da demonstração:

| No Bundle FHIR | Vira |
|---|---|
| `Patient` — Sebastião | 1 registro |
| `Patient` — Lúcia | 1 registro |
| `Procedure` — infusão de Zoledronato | 1 evento, do Sebastião |
| `Observation` — cálcio sérico | 1 evento, do Sebastião |
| `Procedure` — consulta de oncologia | 1 evento, da Lúcia |

**O evento pertence ao registro de origem, nunca diretamente à pessoa.** É essa decisão de
modelagem que faz a linha do tempo da Maria pular de 4 para 12 eventos na hora da costura:
nenhum evento se move nem é copiado — o que muda é quantos registros passam a apontar para
ela.

> "Registro é a pessoa naquele sistema; evento é o que aconteceu com ela. Ingerimos dois
> pacientes e três acontecimentos clínicos."

### Payload bruto
O documento original, exatamente como chegou, guardado no banco ao lado do dado já
interpretado. Serve para duas coisas: **auditoria** — provar o que o sistema de origem
mandou — e **reprocessamento**, se depois a gente descobrir que interpretou errado.

> "Guardamos o documento original. Se a nossa leitura estiver errada, dá pra refazer sem
> pedir o dado de novo."

---

## O motor de identificação

### Record linkage
O nome técnico do problema: **decidir se dois registros, vindos de fontes diferentes,
falam da mesma pessoa**. É um campo de estudo com décadas de literatura, não uma
invenção do projeto. Também aparece como *entity resolution* ou *patient matching*.

> "É o problema clássico de descobrir se dois cadastros são a mesma pessoa."

### Jaro-Winkler
Um algoritmo que compara **quão parecidas duas palavras são**, devolvendo de 0 a 100. Não
é "igual ou diferente": "MARIA APARECIDA SOUZA" e "M. A. SOUZA" dão 49; "SEBASTIÃO ROCHA
MARTINS" e "SEBASTIAO MARTINS" dão mais. Ele dá **peso extra ao começo da palavra**, o que
funciona bem para nomes, onde o começo costuma ser preservado e o fim é que varia.

> "É a régua que diz o quanto dois nomes se parecem, em vez de só dizer se são idênticos."

### UTL_MATCH
O **pacote nativo do Oracle** que implementa Jaro-Winkler e outros algoritmos de
similaridade. É o detalhe que sustenta a frase "usamos o Oracle como banco de verdade": a
comparação roda dentro do banco, onde o dado está, em vez de trazer tudo para a aplicação.

> "É função nativa do Oracle. A gente não trouxe 100 mil registros pra memória do Java pra
> comparar texto."

### Score
A nota de 0 a 100 que o motor dá a um par. **Não é uma média** — é a razão entre o peso
que os campos conferiram e o peso dos campos que dava para comparar:

```
CNS igual              45 pontos
CPF igual              20
Data de nascimento     15
Nome da mãe            10
Similaridade do nome   até 10
```

Um campo só entra na conta se **os dois lados o possuem**. CPF em branco não conta contra
o par — é ausência de evidência, não evidência contrária.

### Piso de evidência
A regra que impede confiança falsa. Se o peso **comparável** ficar abaixo de 60, nenhuma
decisão é automática, por mais alto que o score seja.

O caso do Roberto Nascimento existe para mostrar isso: dois registros com o mesmo nome e a
mesma data de nascimento, **sem CNS e sem CPF dos dois lados**. Tudo o que dava para
comparar bateu, então o score é 100 — mas só havia 25 dos 100 pontos possíveis de
evidência. Vai para revisão humana.

> "Score alto com pouca evidência não é confiança, é ilusão. O sistema sabe a diferença."

### Blocking
Otimização clássica de record linkage: **não comparar todo mundo com todo mundo**. Com 15
registros dá 105 pares; com 100 mil daria 5 bilhões. O blocking só compara pares que já
têm alguma âncora em comum — mesmo CNS, mesmo CPF, mesma data de nascimento ou nome
foneticamente parecido.

> "Sem isso a comparação viraria produto cartesiano e não escalaria."

### SOUNDEX
Algoritmo que reduz uma palavra ao seu **som aproximado**, para agrupar grafias diferentes
do mesmo nome. Também é função nativa do Oracle, e é uma das âncoras do blocking.

### Paciente mestre
A **identidade unificada**, criada pela Sutura ao costurar registros. Ela não substitui os
cadastros de origem: aponta para eles. Desfazer uma costura apaga o vínculo, nunca o dado.

### Vínculo
A ligação entre um registro de origem e um paciente mestre. **É a costura em si.** Uma
regra do banco garante que cada registro pertença a no máximo um paciente mestre — é o
banco recusando o prontuário falso, sem depender de o programa lembrar de checar.

### Auditoria
O registro de **quem decidiu, quando, com que score e contrariando ou não a recomendação**.
Guarda inclusive as decisões de *separar*. Sem isso não há como responder, meses depois,
por que dois históricos foram unidos.

---

## Infraestrutura

### Oracle Autonomous Database
Banco de dados gerenciado da Oracle: sem instalar, sem administrar, com backup e
atualização automáticos. A versão **Always Free** não expira e não cobra — é a que o
projeto usa, na região de São Paulo.

### Wallet / mTLS
**mTLS** é TLS nos dois sentidos: além de o cliente verificar o servidor, o servidor
verifica o cliente. A **wallet** é o pacote de certificados que prova quem é o cliente.
Na prática: uma pasta de arquivos que a aplicação precisa ter para conseguir conectar —
por isso ela nunca vai para o repositório.

### Flyway / migration
**Migration** é um arquivo SQL versionado que descreve uma mudança no banco. O **Flyway**
aplica os arquivos em ordem e anota quais já rodaram, então o schema pode ser recriado do
zero em qualquer máquina, na mesma sequência.

> "O banco é código versionado, não algo que alguém montou na mão e ninguém sabe reproduzir."

### Spring Boot
O framework Java em que o backend é escrito. Roda a API, conversa com o banco e aplica as
migrations na subida.

### Angular
O framework do front-end, em TypeScript. As três telas da demonstração.

### API / endpoint
**API** é a porta pela qual dois sistemas conversam. **Endpoint** é cada porta específica —
`GET /v1/candidatos` devolve a fila, `POST /v1/ingest/exemplo` recebe o lote FHIR.

---

## Se travar numa pergunta

Três frases que servem para quase tudo, sem inventar:

- *"Essa parte a gente não implementou nesta fase — está declarada como fora de escopo no PRD."*
- *"Os dados são fictícios, mas o caminho que eles percorrem é o real."*
- *"Não sei responder de cabeça, mas está documentado no repositório e eu te mando."*

Dizer "não sei" com precisão vale mais que inventar uma resposta que não se sustenta na
pergunta seguinte.
