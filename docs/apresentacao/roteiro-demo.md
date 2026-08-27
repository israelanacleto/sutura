# Roteiro da demonstração — 5 minutos

Mentoria 2 · 20/08/2026 · 19:30

Protótipo em `web/`. Subir com `npm start` dentro de `web/` e abrir `http://localhost:4200`.
Deixar o navegador já aberto na tela **Conexões** antes de começar a apresentar.

> Antes de começar: clicar em **Reiniciar demonstração** (canto inferior esquerdo) para
> zerar o estado. Isso devolve a fila de identificação ao ponto inicial.

---

## 0:00 – 0:45 · O problema (sem tela, ou no slide do Rich Picture)

> "Hoje um paciente não tem um histórico. Ele tem pedaços de histórico espalhados por
> sistemas que não se falam. O hospital usa MV, a clínica de oncologia usa Tasy, o
> laboratório tem o próprio sistema. Cada um enxerga um pedaço, ninguém enxerga a pessoa.
> O resultado é exame repetido, história recontada, e uma equipe administrativa perdendo
> mais de 50 horas por mês costurando isso na mão, no Excel."

## 0:45 – 1:45 · Tela 1 — Conexões

**Ação:** já está aberta. Apontar os quatro cartões.

> "A Sutura não substitui nenhum desses sistemas. Ela se conecta por cima. Aqui estão o MV
> do Hospital Santa Clara, o Tasy da clínica de oncologia, o laboratório via HL7 — e um
> quarto caso que todo mundo que trabalha com saúde reconhece: a planilha de faturamento
> importada na mão, com linhas rejeitadas por layout fora do padrão."

**Ação:** clicar em **Sincronizar agora** no cartão do MV. Esperar o spinner.

> "A sincronização traz os registros novos — e olha o que aparece."

*(surge a faixa amarela: 2 novos pares suspeitos de serem o mesmo paciente)*

> "Dois novos pares de registros que provavelmente são a mesma pessoa em sistemas
> diferentes. É aí que a Sutura faz o trabalho que dá nome a ela."

**Ação:** clicar em **Ver fila de identificação →**.

## 1:45 – 3:15 · Tela 2 — Identificação (o coração)

**Ação:** o primeiro cartão (score 96%) já vem aberto.

> "O motor compara CNS, CPF, data de nascimento, nome da mãe e similaridade do nome. Aqui:
> o CNS é idêntico, a data de nascimento é idêntica, o nome da mãe confere. O que difere é
> a grafia do nome — num sistema está 'Maria Aparecida Souza', no outro 'M. A. Souza' — e o
> CPF que simplesmente não foi preenchido no Tasy. Para um sistema tradicional, são duas
> pessoas. Para a Sutura, é a mesma pessoa com 96% de confiança."

**Ação:** clicar em **Costurar registros**.

**Ação:** abrir o segundo cartão (91%, Maria Aparecida **de** Souza, laboratório) e costurar
também.

> "Mesma paciente, agora com a terceira fonte."

**Ação:** abrir o cartão do **João Carlos Ferreira** (score 58%, vermelho).

> "E esse é o ponto que separa a Sutura de um 'de-para' ingênuo. Dois registros com o nome
> exatamente igual. Um sistema burro juntaria os dois — e criaria um prontuário falso,
> misturando o histórico de duas pessoas. Isso é risco clínico, não é bug de cadastro. A
> Sutura olha CNS, CPF e data de nascimento, vê que tudo diverge, e **recomenda manter
> separado**. A decisão final é sempre humana, e fica registrada para auditoria e LGPD."

**Ação:** clicar em **São pessoas diferentes**.

**Ação:** no cartão costurado da Maria, clicar em **Ver histórico unificado →**.

## 3:15 – 4:30 · Tela 3 — Histórico unificado

> "Esse é o resultado da costura. Maria Aparecida, 62 anos, em tratamento de câncer de mama."

**Ação:** apontar a faixa verde "Padrão detectado".

> "Seis infusões com intervalo exato de 28 dias. Isso não são seis atendimentos avulsos — é
> um tratamento contínuo, ciclo 6 de 8, com a próxima infusão prevista para 11 de setembro.
> Repare na coluna da esquerda: as infusões vêm do Tasy, a cirurgia e as consultas vêm do
> MV, a biópsia e o hemograma vêm do laboratório. Um histórico só, com a origem de cada
> evento rastreada."

**Ação:** clicar em **Antes da Sutura** (canto superior direito). *(o momento da virada)*

> "E é assim que essa mesma paciente existe hoje. Três cadastros, com três grafias
> diferentes do nome, e cada sistema enxergando cinco, quatro, três eventos de um histórico
> de doze. O oncologista não vê o ecocardiograma que o hospital pediu. O hospital não sabe
> em que ciclo ela está. E ninguém está errado — cada um está fazendo o certo com o pedaço
> que tem."

**Ação:** voltar para **Com a Sutura**.

## 4:30 – 5:00 · Fechamento

> "Isso é um protótipo com dados fictícios — o backend em Java com Spring Boot e o Oracle
> Autonomous Database entram no lugar da camada de dados que hoje está em memória. Mas o
> fluxo é esse: conectar, identificar, costurar. E com o dado limpo e unificado, a próxima
> camada é a automação de back-office e a prevenção de glosa — e ainda alimentar
> ferramentas clínicas de IA, como a Sofya, que hoje recebem dado sujo e fragmentado.
> A gente não é mais um sistema. A gente é a costura entre eles."

---

## Se algo der errado

- **A aplicação não sobe:** usar as capturas de tela no deck (slides adicionados ao final).
- **O estado ficou bagunçado no meio da demo:** botão **Reiniciar demonstração** na barra
  lateral devolve tudo ao início sem recarregar a página.
- **Perguntarem se está funcionando de verdade:** responder direto — é um protótipo
  navegável com dados fictícios, o motor de identificação e a persistência são a próxima
  entrega (08/09). Não vender como pronto; a honestidade aqui conta ponto.

## Perguntas que provavelmente vão fazer

**"Os ERPs vão deixar vocês integrarem?"**
É o principal risco do negócio, e está mapeado no stakeholder *TI/fornecedor do ERP*. MV e
Tasy têm APIs REST e suporte a FHIR/HL7; o caminho é integração autorizada pelo cliente,
que é o dono do dado — não raspagem.

**"E a LGPD?"**
Dado de saúde é dado sensível. O desenho prevê OCI Data Safe, mascaramento em ambientes
não-produtivos, e trilha de auditoria em toda decisão de costura — que é exatamente o que
a tela de identificação registra.

**"Isso não é o que um barramento de interoperabilidade já faz?"**
Barramento transporta mensagem. Ele não resolve identidade do paciente nem back-office. A
Sutura resolve o "quem é essa pessoa nos meus cinco sistemas" e o que vem depois disso.
