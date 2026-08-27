# Apresentação — mentoria 2

Material da apresentação do protótipo da Sutura. Mentoria 2 do challenge, 27/08/2026.

## Conteúdo

| Arquivo | O que é |
|---|---|
| [roteiro-demo.md](roteiro-demo.md) | Roteiro cronometrado de 5 minutos: falas, cliques exatos, plano B e as perguntas prováveis da banca com resposta |
| `01-conexoes.png` | Hub de conexões — MV, Tasy, laboratório e a planilha legada |
| `02-identificacao.png` | Fila de identificação — comparação campo a campo, incluindo o homônimo que o sistema recomenda **não** costurar |
| `03-historico-unificado.png` | Histórico unificado — linha do tempo com a origem de cada evento e o padrão das infusões a cada 28 dias |
| `04-antes-da-sutura.png` | A mesma paciente **antes** da costura: 3 cadastros, 3 grafias do nome, cada sistema vendo um pedaço |

As capturas saíram da aplicação rodando, em 2880 px de largura (2×) — dá pra colar no
PowerPoint sem serrilhar.

## Como regerar as capturas

Com a aplicação no ar (`npm start` dentro de `web/`):

```bash
chrome --headless=new --hide-scrollbars --force-device-scale-factor=2 --window-size=1440,1010 --screenshot=01-conexoes.png http://localhost:4200/conexoes
```

A tela de histórico aceita `?modo=antes` na URL para abrir direto na visão fragmentada —
serve tanto para a captura quanto para a demonstração ao vivo, se você quiser começar por
ela sem precisar clicar no alternador.

## Ordem da apresentação

1. Conexões → clicar em **Sincronizar agora** no MV
2. Identificação → costurar os dois pares da Maria, depois abrir o caso do homônimo
3. Histórico unificado → apontar o padrão das infusões
4. Alternar para **Antes da Sutura** — é o ponto mais forte da demonstração

O detalhamento de cada passo, com as falas, está no [roteiro](roteiro-demo.md).
