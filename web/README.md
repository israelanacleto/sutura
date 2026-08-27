# Sutura — protótipo navegável

Protótipo de front-end da Sutura, a camada de integração que se conecta por cima dos ERPs
de saúde existentes. Feito para a mentoria 2 do challenge (20/08/2026).

## Como rodar

```
npm start
```

Abre em `http://localhost:4200`.

## O que tem aqui

| Rota | Tela | O que demonstra |
|---|---|---|
| `/conexoes` | Hub de conexões | MV, Tasy, laboratório e uma planilha legada como sistemas de origem. Sincronização traz registros novos e alimenta a fila de identificação. |
| `/identificacao` | Fila de identificação | Pares de registros candidatos a serem a mesma pessoa, com score, comparação campo a campo e recomendação. Inclui um caso de homônimo em que o sistema recomenda **não** costurar. |
| `/paciente` | Histórico unificado | Linha do tempo única com a origem de cada evento, destaque do tratamento contínuo (infusões a cada 28 dias) e alternador **Antes da Sutura / Com a Sutura**. |

O roteiro da apresentação está em [`../docs/apresentacao/roteiro-demo.md`](../docs/apresentacao/roteiro-demo.md).

## Estado atual e limites

- **Sem backend.** Todo o estado vive em memória, em `src/app/core/sutura-store.ts`.
  Na próxima fase esse serviço é substituído por chamadas ao backend em Java + Spring Boot
  sobre Oracle Autonomous Database.
- **Dados fictícios**, com formato realista (CNS, prontuário, CPF, protocolos HL7/FHIR).
  Nenhum dado de pessoa real.
- **Sem autenticação, sem persistência.** Recarregar a página zera o estado — o botão
  *Reiniciar demonstração* na barra lateral faz o mesmo sem recarregar.
- O motor de identificação **não** calcula score: os valores são fixos, escolhidos para
  representar casos reais de record linkage (nome abreviado, CPF ausente, homônimo).

## Estrutura

```
src/app/
  core/
    models.ts        tipos do domínio
    mock-data.ts     conexões, candidatos e paciente de exemplo
    sutura-store.ts  estado da aplicação (signals) — ponto de troca pelo backend
  pages/
    conexoes/
    identificacao/
    paciente/
```

Angular 21, standalone components, zoneless, signals. Sem bibliotecas de UI — todo o CSS é
próprio, com tokens em `src/styles.css`.
