# Roteiro da demonstração — 5 minutos

Versão de 27/08/2026, para a aplicação **ligada ao Oracle Autonomous Database**.

> A versão anterior deste roteiro descrevia o protótipo com dados fixos. Ela não vale mais:
> botões mudaram, scores mudaram e o "Sincronizar" deixou de ser simulação.

Os termos técnicos que aparecem aqui — Bundle FHIR R4, Jaro-Winkler, piso de evidência,
registro × evento — estão explicados no [glossário](glossario.md), com a frase pronta para
responder cada um.

---

## Antes de começar

**1. Restaurar o estado da demonstração** (obrigatório se alguém já mexeu na aplicação):

```
api/scripts/reset-demo.sql
```

Rode pelo SQL Worksheet do console OCI (*Database actions → SQL*) ou por linha de comando.
Ao final ele imprime a conferência — o esperado é **15 registros, 1 vínculo, 1 paciente
mestre, 0 decisões, 12 eventos**. Sem isso, o botão "Sincronizar" não traz nada novo e a
linha do tempo já nasce completa, matando os dois melhores momentos da apresentação.

Confira o número de pacientes mestres: se vier maior que 1, sobraram registros de uma
demonstração anterior.

**2. Subir as duas pontas:**

```bash
cd F:\PJ\FIAP\Sutura\api && mvn spring-boot:run -Dspring-boot.run.profiles=local
```

```bash
cd F:\PJ\FIAP\Sutura\web && npm start
```

**3. Deixar o navegador aberto em `http://localhost:4200/conexoes`** antes de apresentar.

---


## Compartilhamento de tela — quem pilota é você

Cinco minutos de preparo que evitam o constrangimento clássico:

- **Compartilhe a janela do navegador, não a tela inteira.** Notificação do WhatsApp
  aparecendo no meio da demonstração de um produto de saúde é péssimo.
- **Silencie as notificações** antes de começar (Windows: `Win + N` → Assistente de foco).
- **Feche as outras abas** e esconda a barra de favoritos (`Ctrl + Shift + B`).
- **Aumente o zoom para 110% ou 125%** (`Ctrl + +`). Compressão de vídeo em chamada come
  detalhe, e a tabela de comparação campo a campo é o momento em que eles precisam
  conseguir ler.
- **Deixe a aplicação já aberta em `localhost:4200/conexoes`** antes de compartilhar.
- **Rode o `reset-demo.sql` antes de tudo isso**, não depois.

Se outra pessoa estiver narrando enquanto você clica, combinem que **quem fala olha para a
banca e quem clica olha para a tela**. O erro comum é o piloto narrar sozinho, de cabeça
baixa, para o próprio monitor.

---

## 0:00 – 0:40 · O problema

> "Um paciente não tem um histórico. Ele tem pedaços de histórico espalhados por sistemas
> que não se falam. O hospital usa MV, a clínica de oncologia usa Tasy, o laboratório tem o
> próprio sistema. Cada um enxerga um pedaço, ninguém enxerga a pessoa. O resultado é exame
> repetido, história recontada, e uma equipe administrativa gastando mais de 50 horas por
> mês costurando isso na mão."

## 0:40 – 1:20 · Conexões — e uma ingestão de verdade

**Tela:** já aberta em Conexões.

> "A Sutura não substitui nenhum desses sistemas, se conecta por cima. MV, Tasy,
> laboratório — e um quarto caso que quem trabalha com saúde reconhece na hora: a planilha
> de faturamento importada na mão, com linhas rejeitadas por layout fora do padrão."

**Onde clicar:** a tela tem três cartões de indicador no topo e, abaixo, **quatro cartões de
sistema em duas colunas** — MV em cima à esquerda, **Philips Tasy em cima à direita**, Lab
Alpha embaixo à esquerda, SGH Legado embaixo à direita.

O botão **Sincronizar agora** fica no **rodapé do cartão do Tasy**, verde, no canto inferior
esquerdo daquele cartão. **É o único cartão que tem botão** — os outros três mostram
"Sincronização automática" no lugar. Se você estiver vendo texto em vez de botão, está no
cartão errado.

> "E isso aqui não é um botão de mentira. Ele manda um Bundle FHIR R4 para a API, que
> parseia o documento, grava cada registro **preservando o documento original** para
> auditoria, e manda o banco reavaliar quem pode ser a mesma pessoa."

*(o botão vira "Ingerindo…" com um spinner por cerca de um segundo)*

**O que aparece:** uma **faixa amarela** entre os indicadores do topo e a grade de cartões,
dizendo *"2 registros novos e 3 eventos ingeridos"*. O contador **Pendentes de
identificação** salta de 8 para 10.

**Onde clicar:** o link **Ver fila de identificação →** fica na **ponta direita dessa faixa
amarela**.

## 1:20 – 3:00 · Identificação — o coração

> "Aqui está o motor. E ele não roda em Java: o score é calculado **dentro do Oracle**, com
> `UTL_MATCH.JARO_WINKLER_SIMILARITY`, que é uma função nativa do banco. A gente usa o
> Oracle como banco de verdade, não como um lugar onde guardar JSON."

### Beat 1 — o caso que veio da ingestão (~40s)

**Onde clicar:** a lista vem ordenada por score, do maior para o menor. O card do
**SEBASTIÃO ROCHA MARTINS × SEBASTIAO MARTINS** é o **quarto**, logo abaixo do Carlos
Eduardo Prado — mas procure pelo nome, não pela posição, porque a ordem muda conforme você
decide os casos.

Clique **em qualquer lugar da linha do cabeçalho do card** — a faixa com o score à esquerda
e os dois nomes. O card abre para baixo e a setinha da direita gira.

> "Este par acabou de chegar naquela sincronização. CNS confere, CPF confere, data de
> nascimento confere. E olhem o nome da mãe: um sistema gravou CONCEIÇÃO com cedilha e til,
> o outro gravou CONCEICAO sem acento nenhum."

**Apontar a linha "Nome da mãe", marcada como igual.**

> "Sistema de saúde brasileiro gravando sem acento é regra, não exceção — legado em ASCII,
> integração que perde diacrítico, importação de planilha. Descobrimos isso testando com
> dado realista: antes da correção, esse par caía de 99 para 89 e ia parar na fila de
> revisão humana por causa de uma cedilha."

**Onde clicar:** **Costurar registros**, o botão verde no **canto inferior esquerdo da área
que abriu**, logo abaixo da tabela de comparação.

*(o card some da lista e aparece uma faixa "Costurado" no topo; o contador da fila cai de
10 para 9)*

### Beat 2 — o homônimo (~45s)

**Onde clicar:** o card do **JOÃO CARLOS FERREIRA**, que é o **último da lista** — score 10,
o único com a etiqueta vermelha *"Recomendado manter separado"*. Mesma coisa: clique na
linha do cabeçalho.

> "Agora o oposto. Nome escrito **exatamente igual** nos dois sistemas. Um de-para ingênuo
> juntaria os dois na hora — e criaria um prontuário falso, misturando o histórico de duas
> pessoas. Isso não é erro de cadastro, é risco clínico. Alguém pode receber medicação com
> base no histórico de outro."

> "A Sutura olha CNS, CPF, data de nascimento e nome da mãe, vê que **tudo** diverge, e
> devolve score 10 com recomendação de manter separado. E não tem nenhum `if` no código
> tratando esse caso: é a fórmula que chega nessa conclusão sozinha."

**Onde clicar:** **São pessoas diferentes**.

Atenção: **neste card os botões estão invertidos.** Como a recomendação é separar, "São
pessoas diferentes" é o botão **verde, à esquerda**, e "Costurar mesmo assim" é o branco ao
lado. Nos outros cards é o contrário. A tela sempre coloca em verde o que o motor recomenda
— e é justamente isso que você quer mostrar.

> "E a decisão fica gravada com usuário, data e hora — inclusive a de separar. Auditoria
> clínica e LGPD exigem poder responder quem decidiu o quê, e quando."

### Beat 3 — costurar a Maria (~25s)

A Maria Aparecida aparece em **três** pares: MV × Tasy, MV × Lab e Tasy × Lab.

**Onde clicar:** procure os cards em que o **lado esquerdo é MARIA APARECIDA SOUZA (MV)** —
são dois. Um tem 100 de score e é o **primeiro da lista**; o outro tem 94 e está mais abaixo.

Para cada um: clique no cabeçalho para abrir, depois em **Costurar registros**.

O terceiro par da Maria — **M. A. SOUZA × MARIA APARECIDA DE SOUZA**, sem o MV do lado
esquerdo — é o que vai sumir sozinho. Não clique nele.

Ao costurar o segundo, o terceiro par **desaparece sozinho da fila**. Vale apontar:

> "Repare que sobrou um par da Maria e ele sumiu sem eu tocar. Depois que os três
> registros passam a apontar para a mesma pessoa, não há mais o que decidir — o banco
> tira o par da fila sozinho. É a diferença entre uma lista de sugestões e um sistema
> que sabe o que já resolveu."

**Onde clicar:** o link **Ver histórico unificado →** aparece na **faixa "Costurado" do topo
da lista**, à direita. Se houver mais de uma faixa, use a de cima.

## 3:00 – 4:20 · Histórico unificado

> "Maria Aparecida, 62 anos, em tratamento de câncer de mama. Isso aqui não existia trinta
> segundos atrás — nasceu das costuras que a gente acabou de fazer."

**Apontar a faixa verde.**

> "Seis infusões com intervalo de 28 dias. Não são seis atendimentos avulsos: é um
> tratamento contínuo, e o sistema projeta a próxima para 11 de setembro. Essa conta sai
> dos dados, não é texto escrito na tela."

> "Repare na origem de cada evento: as infusões vêm do Tasy, a cirurgia e as consultas vêm
> do MV, a biópsia e o hemograma vêm do laboratório. Um histórico só, com a procedência de
> cada linha rastreada até o sistema de origem."

**Onde clicar:** **Antes da Sutura**, o botão da **esquerda** no par de botões do **canto
superior direito** da tela, na mesma altura do título. *(o momento da virada)*

> "E é assim que essa paciente existe hoje. Três cadastros, três grafias diferentes do
> nome, e cada sistema enxergando quatro, seis, dois eventos de um histórico de doze. O
> oncologista não vê o ecocardiograma que o hospital pediu. O hospital não sabe em que
> ciclo ela está. E ninguém está errado — cada um está fazendo o certo com o pedaço que tem."

**Onde clicar:** **Com a Sutura**, o botão da **direita** no mesmo par.

## 4:20 – 5:00 · Fechamento

> "Tudo o que vocês viram rodou contra um Oracle Autonomous Database em São Paulo, com
> Spring Boot e Angular. Os dados são fictícios; o caminho é real — ingestão FHIR, motor de
> identificação no banco, trilha de auditoria."

> "O próximo passo é a camada que fica em cima disso: automação de back-office e prevenção
> de glosa. E, com dado limpo e unificado, alimentar ferramentas clínicas de IA como a
> Sofya, que hoje recebem dado sujo e fragmentado."

**Para esta banca, acrescente** — é o trecho que mostra que vocês sabem onde cada serviço
da Oracle entra, em vez de tê-los listado no slide por listar:

> "E dentro do ecossistema de vocês, a gente sabe onde quer chegar. **Data Safe** para
> mascarar dado sensível fora de produção — que para dado de saúde é obrigação de LGPD, não
> enfeite. **Document Understanding** para ler guia de faturamento, que hoje é digitação
> manual e é de onde vem boa parte da glosa. E **AI Vector Search** na similaridade de
> nomes, se ele se provar melhor que o Jaro-Winkler que estamos usando. A gente preferiu
> fazer um serviço direito antes de encostar em doze."

Três serviços, cada um amarrado a um problema que vocês acabaram de mostrar na tela — não
uma lista. E a última frase converte o que pareceria dívida em critério de engenharia.

> ⚠️ **Só diga isso se for verdade.** Prometer roadmap para o fabricante do produto, sem
> intenção real de seguir, é pior que ficar calado — eles vão perguntar na próxima mentoria.

> "A gente não é mais um sistema. A gente é a costura entre eles."

---

## Mapa de cliques — para bater o olho durante a demonstração

| # | Tela | Onde | O que acontece |
|---|---|---|---|
| 1 | Conexões | **Sincronizar agora** — rodapé do cartão do **Tasy**, coluna direita, primeira linha. Único cartão com botão | Faixa amarela: "2 registros novos e 3 eventos"; pendentes 8 → 10 |
| 2 | Conexões | **Ver fila de identificação →** — ponta direita da faixa amarela | Vai para a fila |
| 3 | Identificação | Cabeçalho do card **SEBASTIÃO** (4º, score 99) | Abre a comparação campo a campo |
| 4 | Identificação | **Costurar registros** — verde, canto inferior esquerdo da área aberta | Card sai; fila 10 → 9 |
| 5 | Identificação | Cabeçalho do card **JOÃO CARLOS FERREIRA** (último, score 10) | Abre a comparação |
| 6 | Identificação | **São pessoas diferentes** — aqui é o botão **verde, à esquerda** (invertido) | Card sai; fila 9 → 8 |
| 7 | Identificação | Cabeçalho do card **MARIA (MV)** de score 100, o primeiro da lista | Abre |
| 8 | Identificação | **Costurar registros** | Card sai |
| 9 | Identificação | Cabeçalho do outro card **MARIA (MV)**, score 94 | Abre |
| 10 | Identificação | **Costurar registros** | Saem **dois** cards: esse e o par Tasy × Lab |
| 11 | Identificação | **Ver histórico unificado →** — na faixa "Costurado" do topo | Vai para o histórico |
| 12 | Paciente | **Antes da Sutura** — botão da esquerda, canto superior direito | Três colunas fragmentadas |
| 13 | Paciente | **Com a Sutura** — botão da direita | Volta para a linha do tempo |

Treze cliques. Se perder o fio, a regra é: **procure pelo nome, não pela posição** — a ordem
da lista muda conforme você decide.

## Se algo der errado

- **A tela mostra "Não foi possível falar com a API":** o backend caiu ou o banco parou.
  Autonomous Free para sozinho após 7 dias sem uso — nesse caso, religar pelo console leva
  ~1 minuto e o dado é preservado.
- **"Sincronizar" responde "Nada novo":** o lote já foi ingerido. Rode o `reset-demo.sql`.
- **A fila está vazia:** mesma coisa, rode o reset.
- **Nada sobe a tempo:** as capturas em `docs/apresentacao` são o plano B.
- **Você errou o clique e costurou o par errado:** rode o `reset-demo.sql` e recomece. Não
  existe desfazer pela interface — a decisão é auditada, e apagar auditoria por um botão
  seria pior que refazer a demonstração.

## Atalhos úteis

Duas telas abrem em estados específicos por URL, o que ajuda se você precisar retomar um
ponto do roteiro sem refazer os cliques:

- `localhost:4200/identificacao?abrir=<par>` — abre um candidato já expandido. O
  identificador é o par de ids dos dois registros, separado por hífen, e sai da API:
  `curl -s localhost:8080/v1/candidatos` devolve o campo `id` de cada par. Foi feito para
  as capturas; em apresentação serve para retomar um caso específico.
- `localhost:4200/paciente?modo=antes` — abre direto na visão fragmentada, se você quiser
  começar pelo "antes" e depois virar para o "depois".

## Perguntas prováveis

**"Só quinze registros?"**
São quinze no estado inicial e dezessete depois da ingestão — é o lote de demonstração,
não um teste de carga. O motor é o mesmo para dezessete ou para cento e vinte mil — a comparação roda no banco, com blocking por CNS, CPF, data de
nascimento e SOUNDEX do nome para não virar produto cartesiano.

**"E quando não há CNS nem CPF?"**
Tem um caso desses na fila: dois "Roberto Nascimento" com a mesma data de nascimento e
nenhum documento dos dois lados. O score dá 100 — tudo que dava para comparar bateu — mas o
sistema manda para **revisão humana** mesmo assim, porque só havia 25 dos 100 pontos de
evidência possíveis. Score alto com pouca evidência não é confiança, é ilusão.

**"Os ERPs vão deixar vocês integrarem?"**
É o principal risco do negócio e está mapeado no stakeholder *TI/fornecedor do ERP*. MV e
Tasy têm API REST e suporte a FHIR/HL7. O caminho é integração autorizada pelo cliente, que
é o dono do dado — não raspagem.

**"E a LGPD?"**
Dado de saúde é dado sensível. Toda decisão de identidade é gravada com autor e momento, e
o documento original de cada registro fica preservado para auditoria. No desenho, OCI Data
Safe entra para mascaramento em ambiente não-produtivo.

**"Isso não é o que um barramento de interoperabilidade já faz?"**
Barramento transporta mensagem. Ele não resolve identidade de paciente nem back-office. A
Sutura resolve o "quem é essa pessoa nos meus cinco sistemas" — e o que vem depois disso.
