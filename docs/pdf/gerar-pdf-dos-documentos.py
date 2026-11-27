"""Converte um .md em HTML pronto para o Chrome imprimir, com Mermaid renderizado."""
import pathlib, sys

origem = pathlib.Path(sys.argv[1])
destino = pathlib.Path(sys.argv[2])
titulo = sys.argv[3] if len(sys.argv) > 3 else origem.stem

md = origem.read_text(encoding="utf-8")
if "</script" in md:
    raise SystemExit("O markdown contem </script e quebraria o embute.")

CSS = """
@page { size: A4 portrait; margin: 18mm 16mm; }
* { box-sizing:border-box; }
body { font-family:"Segoe UI",system-ui,sans-serif; color:#1d2b29; line-height:1.6;
       font-size:10.5pt; -webkit-print-color-adjust:exact; print-color-adjust:exact; margin:0; }
h1 { font-size:23pt; font-weight:650; letter-spacing:-.01em; margin:0 0 14px; color:#0d3b36; }
h2 { font-size:15pt; font-weight:650; margin:26px 0 10px; color:#0f766e;
     border-bottom:2px solid #dfeae8; padding-bottom:6px; break-after:avoid; }
h3 { font-size:12pt; font-weight:640; margin:18px 0 7px; color:#15302c; break-after:avoid; }
h4 { font-size:10.5pt; font-weight:700; margin:14px 0 6px; color:#3c524e; }
p { margin:0 0 10px; }
ul,ol { margin:0 0 12px; padding-left:20px; }
li { margin-bottom:5px; }
strong { color:#0d2b28; font-weight:640; }
code { font-family:"Cascadia Mono",Consolas,monospace; font-size:9pt;
       background:#f1f6f5; padding:1.5px 5px; border-radius:4px; color:#0b5a54; }
pre { background:#f7faf9; border:1px solid #e2eceb; border-radius:7px; padding:12px 14px;
      overflow-x:auto; margin:0 0 13px; break-inside:avoid; }
pre code { background:none; padding:0; font-size:8.8pt; line-height:1.45; color:#2c3f3c; }
table { width:100%; border-collapse:collapse; margin:0 0 14px; font-size:9.5pt; break-inside:avoid; }
th { text-align:left; background:#f1f6f5; color:#0d3b36; font-weight:700; font-size:8.5pt;
     letter-spacing:.04em; text-transform:uppercase; padding:7px 9px; border-bottom:2px solid #d4e3e1; }
td { padding:7px 9px; border-bottom:1px solid #eaf1f0; vertical-align:top; }
blockquote { border-left:3px solid #0f766e; background:#f5faf9; margin:0 0 14px;
             padding:11px 16px; border-radius:0 6px 6px 0; break-inside:avoid; }
blockquote p:last-child { margin-bottom:0; }
hr { border:none; border-top:1px solid #e2eceb; margin:22px 0; }
a { color:#0f766e; text-decoration:none; }
.mermaid { text-align:center; margin:16px 0; break-inside:avoid; }
.mermaid svg { max-width:100%; height:auto; }
.capa { border-bottom:3px solid #0f766e; padding-bottom:12px; margin-bottom:22px; }
.capa .sel { font-size:8.5pt; font-weight:700; letter-spacing:.14em; text-transform:uppercase;
             color:#0f766e; }
"""

HTML = f"""<!doctype html><html lang="pt-BR"><head><meta charset="utf-8">
<title>{titulo}</title><style>{CSS}</style>
<script src="https://cdn.jsdelivr.net/npm/marked@12.0.2/marked.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/mermaid@11.4.1/dist/mermaid.min.js"></script>
</head><body>
<div class="capa"><div class="sel">Sutura &middot; FIAP &times; Oracle Academy &middot; 2026</div></div>
<div id="out"></div>
<script id="src" type="text/plain">{md}</script>
<script>
(async function () {{
  var fonte = document.getElementById('src').textContent;
  document.getElementById('out').innerHTML = marked.parse(fonte, {{ gfm: true, breaks: false }});
  document.querySelectorAll('pre code.language-mermaid').forEach(function (el) {{
    var d = document.createElement('div');
    d.className = 'mermaid';
    d.textContent = el.textContent;
    el.closest('pre').replaceWith(d);
  }});
  mermaid.initialize({{ startOnLoad: false, theme: 'neutral', securityLevel: 'loose' }});
  try {{ await mermaid.run(); }} catch (e) {{ console.log('mermaid: ' + e.message); }}
  document.title = {titulo!r};
}})();
</script></body></html>"""

destino.write_text(HTML, encoding="utf-8")
print(f"{origem.name} -> {destino.name}")
