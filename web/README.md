# Sutura — front-end

Angular 21, standalone components, zoneless, signals. Sem bibliotecas de UI: todo o CSS é
próprio, com tokens em `src/styles.css`.

## Como rodar

```bash
npm install
npm start
```

Abre em `http://localhost:4200`.

**Depende do backend.** As telas leem de `http://localhost:8080`; sem ele, aparece o aviso
de falha de conexão em vez dos dados. A preparação do banco e das credenciais está no
[README da raiz](../README.md).

## As telas

| Rota | Tela | O que demonstra |
|---|---|---|
| `/conexoes` | Hub de conexões | Os sistemas de origem com volume real de registros. O botão *Sincronizar agora* do Tasy dispara uma ingestão FHIR de verdade |
| `/identificacao` | Fila de identificação | Pares candidatos a serem a mesma pessoa, com score vindo do banco, comparação campo a campo e recomendação. Inclui um homônimo que o sistema recomenda **não** costurar |
| `/paciente` | Histórico unificado | Linha do tempo com a origem de cada evento e alternador **Antes da Sutura / Com a Sutura** |

Duas rotas aceitam parâmetro, o que ajuda em capturas e na apresentação:

- `/identificacao?abrir=<par>` abre um candidato já expandido
- `/paciente?modo=antes` abre direto na visão fragmentada

## Como os dados chegam

Tudo passa por `core/sutura-store.ts`, que é o único ponto do front que conhece a origem
dos dados. Ele usa `httpResource` (experimental desde a 19.2): cada recurso expõe valor,
carregamento e erro como signals, sem subscribe manual. Um signal de versão invalida as
consultas depois de cada mutação, então a fila e o histórico se refazem sozinhos após uma
decisão.

Os componentes não conhecem HTTP. Quando o backend entrou, no lugar dos dados fixos,
nenhuma tela precisou mudar de estrutura.

`core/sistemas.ts` guarda sigla e cor de cada sistema de origem — decisão de apresentação,
que não tem por que trafegar pela API a cada requisição.

## Limites conhecidos

- **Sem autenticação.** O usuário gravado na auditoria das decisões é fixo.
- **Sem desfazer.** Uma decisão registrada não se apaga pela interface: a trilha é
  auditável de propósito. Para recomeçar uma demonstração, use `api/scripts/reset-demo.sql`.
- **Dados fictícios**, com formato realista — CNS, prontuário, CPF, protocolos HL7/FHIR.
  Nenhum dado de pessoa real.

## Estrutura

```
src/app/
  core/
    models.ts        tipos do domínio
    sistemas.ts      sigla e cor dos sistemas de origem
    sutura-store.ts  acesso ao backend, em signals
  pages/
    conexoes/
    identificacao/
    paciente/
```
