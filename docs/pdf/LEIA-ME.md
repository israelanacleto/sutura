# PDFs dos documentos

Os documentos em `docs/` usam diagramas **Mermaid**, que o GitHub renderiza mas um leitor de
Markdown local normalmente não. Como o enunciado exige que os arquivos "funcionem
corretamente ao serem abertos localmente", esta pasta traz os mesmos documentos em PDF, com
os diagramas já desenhados.

| PDF | Equivale a |
|---|---|
| `documento-consolidado.pdf` | `docs/documento-consolidado.md` |
| `arquitetura.pdf` | `docs/arquitetura.md` |
| `validacao-tecnica.pdf` | `docs/validacao-tecnica.md` |
| `roadmap.pdf` | `docs/roadmap.md` |

**Os `.md` continuam sendo a fonte.** Os PDFs são gerados a partir deles — se um documento
mudar, regere com `gerar-pdf-dos-documentos.py` em vez de editar o PDF.
