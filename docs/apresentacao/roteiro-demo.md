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

**Ação:** clicar em **Sincronizar agora** no cartão do **Philips Tasy**.

> "E isso aqui não é um botão de mentira. Ele manda um Bundle FHIR R4 para a API, que
> parseia o documento, grava cada registro **preservando o documento original** para
> auditoria, e manda o banco reavaliar quem pode ser a mesma pessoa."

*(aparece: "2 registros novos e 3 eventos ingeridos")*

**Ação:** clicar em **Ver fila de identificação →**.

## 1:20 – 3:00 · Identificação — o coração

> "Aqui está o motor. E ele não roda em Java: o score é calculado **dentro do Oracle**, com
> `UTL_MATCH.JARO_WINKLER_SIMILARITY`, que é uma função nativa do banco. A gente usa o
> Oracle como banco de verdade, não como um lugar onde guardar JSON."

### Beat 1 — o caso que veio da ingestão (~40s)

**Ação:** abrir **SEBASTIÃO ROCHA MARTINS × SEBASTIAO MARTINS**, score 99.

> "Este par acabou de chegar naquela sincronização. CNS confere, CPF confere, data de
> nascimento confere. E olhem o nome da mãe: um sistema gravou CONCEIÇÃO com cedilha e til,
> o outro gravou CONCEICAO sem acento nenhum."

**Apontar a linha "Nome da mãe", marcada como igual.**

> "Sistema de saúde brasileiro gravando sem acento é regra, não exceção — legado em ASCII,
> integração que perde diacrítico, importação de planilha. Descobrimos isso testando com
> dado realista: antes da correção, esse par caía de 99 para 89 e ia parar na fila de
> revisão humana por causa de uma cedilha."

**Ação:** **Costurar registros**.

### Beat 2 — o homônimo (~45s)

**Ação:** abrir **JOÃO CARLOS FERREIRA × JOÃO CARLOS FERREIRA**, score 10.

> "Agora o oposto. Nome escrito **exatamente igual** nos dois sistemas. Um de-para ingênuo
> juntaria os dois na hora — e criaria um prontuário falso, misturando o histórico de duas
> pessoas. Isso não é erro de cadastro, é risco clínico. Alguém pode receber medicação com
> base no histórico de outro."

> "A Sutura olha CNS, CPF, data de nascimento e nome da mãe, vê que **tudo** diverge, e
> devolve score 10 com recomendação de manter separado. E não tem nenhum `if` no código
> tratando esse caso: é a fórmula que chega nessa conclusão sozinha."

**Ação:** **São pessoas diferentes**.

> "E a decisão fica gravada com usuário, data e hora — inclusive a de separar. Auditoria
> clínica e LGPD exigem poder responder quem decidiu o quê, e quando."

### Beat 3 — costurar a Maria (~25s)

A Maria Aparecida aparece em **três** pares: MV × Tasy, MV × Lab e Tasy × Lab.

**Ação:** costurar os dois primeiros — **MV × Tasy** e **MV × Lab**.

Ao costurar o segundo, o terceiro par **desaparece sozinho da fila**. Vale apontar:

> "Repare que sobrou um par da Maria e ele sumiu sem eu tocar. Depois que os três
> registros passam a apontar para a mesma pessoa, não há mais o que decidir — o banco
> tira o par da fila sozinho. É a diferença entre uma lista de sugestões e um sistema
> que sabe o que já resolveu."

**Ação:** clicar em **Ver histórico unificado →**.

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

**Ação:** clicar em **Antes da Sutura**. *(o momento da virada)*

> "E é assim que essa paciente existe hoje. Três cadastros, três grafias diferentes do
> nome, e cada sistema enxergando quatro, seis, dois eventos de um histórico de doze. O
> oncologista não vê o ecocardiograma que o hospital pediu. O hospital não sabe em que
> ciclo ela está. E ninguém está errado — cada um está fazendo o certo com o pedaço que tem."

**Ação:** voltar para **Com a Sutura**.

## 4:20 – 5:00 · Fechamento

> "Tudo o que vocês viram rodou contra um Oracle Autonomous Database em São Paulo, com
> Spring Boot e Angular. Os dados são fictícios; o caminho é real — ingestão FHIR, motor de
> identificação no banco, trilha de auditoria."

> "O próximo passo é a camada que fica em cima disso: automação de back-office e prevenção
> de glosa. E, com dado limpo e unificado, alimentar ferramentas clínicas de IA como a
> Sofya, que hoje recebem dado sujo e fragmentado."

> "A gente não é mais um sistema. A gente é a costura entre eles."

---

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
