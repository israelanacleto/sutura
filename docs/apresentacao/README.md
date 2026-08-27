# Apresentação

Material da demonstração da Sutura, para a versão **ligada ao Oracle Autonomous Database**.

## Conteúdo

| Arquivo | O que mostra |
|---|---|
| [roteiro-demo.md](roteiro-demo.md) | Roteiro cronometrado de 5 minutos: preparação, falas, cliques na ordem, plano B e as perguntas prováveis com resposta |
| [glossario.md](glossario.md) | Os termos técnicos do roteiro explicados, cada um com a frase pronta para responder na hora |
| [perguntas-mentoria.md](perguntas-mentoria.md) | O que perguntar aos mentores — e o que não perguntar |
| `01-conexoes.png` | Hub de conexões — MV, Tasy, laboratório e a planilha legada, com os volumes reais do banco |
| `02-fila-identificacao.png` | A fila no estado inicial, com os oito pares e as três recomendações diferentes |
| `03-caso-acentuacao.png` | **A imagem mais forte.** O par do Sebastião aberto: `CONCEIÇÃO ROCHA` e `CONCEICAO ROCHA` reconhecidos como o mesmo valor, com os dois originais à vista |
| `04-historico-unificado.png` | A linha do tempo costurada, com a origem de cada evento e o padrão das infusões |
| `05-antes-da-sutura.png` | A mesma paciente antes da costura: três cadastros, três grafias, cada sistema vendo um pedaço |

Capturas em 2880 px de largura (2×), tiradas da aplicação rodando contra o banco.

## Antes de apresentar

Restaure o estado da demonstração, senão o botão "Sincronizar" não traz nada novo e a
linha do tempo já nasce completa:

```
api/scripts/reset-demo.sql
```

O script imprime a conferência ao final — o esperado é **15 registros, 1 vínculo,
0 decisões, 12 eventos**. Os passos e a preparação completa estão no
[roteiro](roteiro-demo.md).

## Como regerar as capturas

Com backend e front no ar, e o banco no estado inicial:

```bash
chrome --headless=new --hide-scrollbars --force-device-scale-factor=2 --window-size=1440,900 --screenshot=01-conexoes.png http://localhost:4200/conexoes
```

Duas telas aceitam parâmetro na URL, o que permite capturar estados que exigiriam clique:

- `/identificacao?abrir=<id do par>` abre um candidato específico já expandido
- `/paciente?modo=antes` abre direto na visão fragmentada

As capturas 03, 04 e 05 exigem que a ingestão e a costura da Maria já tenham sido feitas —
ou seja, são tiradas **durante** a demonstração, não no estado inicial. Depois de gerá-las,
rode o reset de novo.
