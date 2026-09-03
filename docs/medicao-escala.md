# Medição de escala do motor de identificação

Medido em 03/09/2026, contra o Oracle Autonomous Database Always Free (1 OCPU,
região São Paulo), com carga sintética isolada em tabelas próprias.

Existe para responder com número, e não com opinião, à pergunta que qualquer banca
técnica faz: **isso escala?**

---

## Como a carga foi montada

`api/scripts/carga-sintetica.sql` gera pessoas fictícias e, para cada uma, de 1 a 3
registros em sistemas diferentes — com as deformações que aparecem em dado real:

- nome abreviado — `MARIA APARECIDA SOUZA` vira `M. A. SOUZA`
- sobrenomes invertidos, erro comum de digitação
- CPF ausente, típico de cadastro de laboratório
- CNS ausente, típico de sistema legado

Cada registro carrega um `pessoa_id` que **nunca entra na comparação**. É o gabarito:
serve só para conferir, depois, se o motor acertou.

**Isolamento:** nada disso toca as tabelas da aplicação. A carga vive em
`carga_registro_origem`, que replica a estrutura e os índices de `registro_origem`.

---

## Resultado com 20 mil registros

| | |
|---|---|
| Registros | 19.999 |
| Pares avaliados | 608.194 |
| Tempo total | **21,3 segundos** |
| Costuras corretas | 11.428 |
| **Falsos positivos** | **0** |
| **Precisão** | **100%** |

### O blocking corta 99%

| Bloco | Pares gerados |
|---|---|
| CNS igual | 11.428 |
| CPF igual | 5.000 |
| Data de nascimento igual | 13.332 |
| SOUNDEX do nome | 1.926.856 |
| **Teto somado** | **1.956.616** |
| Produto cartesiano, sem blocking | 199.970.001 |

**Redução de 99,0%.**

---

## O gargalo encontrado, e a correção

A primeira medição, com apenas 4 mil registros, levou **47 segundos**. Número alto
demais para o volume, o que motivou olhar o plano de execução em vez de aceitar.

O Oracle estava fazendo `MERGE JOIN` + `FILTER` — ou seja, **ignorando os índices**.

### A causa

O `OR` na condição de junção. Comprovado isolando cada caso:

| Condição do join | Plano | Tempo |
|---|---|---|
| `a.cns = b.cns` | **HASH JOIN** | 38 ms |
| `OR` de CNS, CPF e nascimento | MERGE JOIN + FILTER | 608 ms |
| `OR` das quatro, com SOUNDEX | MERGE JOIN + FILTER | ~47.000 ms |

Com uma igualdade só, o otimizador usa hash join. Com `OR` de colunas diferentes, ele
desiste, ordena os dois lados e aplica a condição como filtro — **o blocking vira
decoração, porque a varredura acontece de qualquer jeito.**

### A correção

Expansão do `OR`: quatro junções separadas, unidas por `UNION`, cada uma livre para
usar seu próprio caminho de acesso.

```
OR das quatro condições ..... 26.126 pares em 47.500 ms
UNION das quatro condições .. 26.126 pares em    241 ms
```

**Mesmo resultado, 197 vezes mais rápido.** Quatro `HASH JOIN` no plano, onde antes
havia zero.

---

## Ressalvas honestas

**O pool de nomes é pequeno demais.** São 230 nomes distintos em 20 mil registros, o
que torna os blocos de SOUNDEX artificialmente grandes — ele responde por 98% do custo
do blocking. Com nome real, mais diverso, esse custo cai bastante. **O número medido é
pessimista, não otimista.**

**Precisão de 100% não se traduz direto para dado real.** A carga sintética deforma os
nomes de maneiras conhecidas; dado de verdade traz erro de digitação, troca de letra e
nome social, que são mais difíceis. O que a medição prova é que a fórmula não gera
falso positivo *nas deformações que ela conhece* — e que o piso de evidência está
segurando o que deveria segurar.

**21 segundos é tempo de lote, não de tela.** O uso real seria uma reavaliação
periódica, não uma consulta interativa. Se precisasse ser interativo, o caminho é
processar incrementalmente apenas os registros novos.

---

## O que fazer com isto

1. **Aplicar a expansão do `OR` na view** `vw_candidato_identificacao`. É o ganho de
   197x, e vale uma migration.
2. **Reduzir o peso do SOUNDEX no blocking** — hoje ele gera 98% dos pares e é o único
   bloco que cresce quadraticamente com nomes repetidos. Um índice funcional em
   `SOUNDEX(nome)` ajudaria o acesso, mas o problema é a seletividade, não o acesso.
3. **Medir de novo depois das duas mudanças**, com o mesmo script, para ter o antes e
   o depois no mesmo eixo.

## Como reproduzir

```
api/scripts/carga-sintetica.sql          gera a carga (troque a quantidade de pessoas)
api/scripts/carga-sintetica-limpar.sql   remove tudo
```
