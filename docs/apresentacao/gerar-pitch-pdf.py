"""Monta o deck do pitch da Sutura em HTML e deixa pronto para o Chrome imprimir em PDF."""
import base64, pathlib, sys

raiz = pathlib.Path(sys.argv[1])
saida = pathlib.Path(sys.argv[2])


def img(nome):
    dados = (raiz / "docs" / "apresentacao" / nome).read_bytes()
    return "data:image/jpeg;base64," + base64.b64encode(dados).decode()


LISTA = img("06-faturamento-valor-em-risco.jpg")
GUIA = img("07-guia-evidencia-e-correcao.jpg")
HISTORICO = img("04-historico-unificado.png").replace("image/jpeg", "image/png")
FILA = img("02-fila-identificacao.png").replace("image/jpeg", "image/png")

CSS = """
@page { size: 1280px 720px; margin: 0; }
* { margin:0; padding:0; box-sizing:border-box; }
body { font-family: "Segoe UI", system-ui, sans-serif; color:#15302c; -webkit-print-color-adjust:exact; print-color-adjust:exact; }
.s { width:1280px; height:720px; page-break-after:always; padding:64px 72px; position:relative; background:#fff; display:flex; flex-direction:column; }
.s:last-child { page-break-after:auto; }
.eyebrow { font-size:15px; font-weight:700; letter-spacing:.14em; text-transform:uppercase; color:#0f766e; margin-bottom:14px; }
h1 { font-size:52px; font-weight:650; line-height:1.1; letter-spacing:-.02em; }
h2 { font-size:40px; font-weight:650; line-height:1.15; letter-spacing:-.015em; margin-bottom:26px; }
p { font-size:21px; line-height:1.55; color:#3c524e; }
.big { font-size:92px; font-weight:680; letter-spacing:-.03em; color:#0f766e; line-height:1; }
.big.alerta { color:#b4352c; }
ul { list-style:none; display:flex; flex-direction:column; gap:15px; margin-top:8px; }
li { font-size:20px; line-height:1.45; color:#3c524e; padding-left:22px; position:relative; }
li::before { content:""; position:absolute; left:0; top:11px; width:8px; height:8px; border-radius:50%; background:#0f766e; }
li b { color:#15302c; font-weight:640; }
.cols { display:flex; gap:40px; flex:1; align-items:center; }
.col { flex:1; min-width:0; }
.rodape { position:absolute; left:72px; bottom:34px; font-size:13px; color:#8a9a97; }
.pag { position:absolute; right:72px; bottom:34px; font-size:13px; color:#8a9a97; font-variant-numeric:tabular-nums; }
.fonte { font-size:13.5px; color:#8a9a97; margin-top:16px; font-style:italic; }
img.shot { width:100%; border-radius:10px; border:1px solid #d9e2e0; box-shadow:0 8px 28px rgba(0,0,0,.10); }
table { width:100%; border-collapse:collapse; margin-top:10px; }
th { text-align:left; font-size:13px; font-weight:700; letter-spacing:.07em; text-transform:uppercase; color:#8a9a97; padding:10px 14px; border-bottom:2px solid #e4ebea; }
td { font-size:19px; padding:12px 14px; border-bottom:1px solid #eef3f2; color:#3c524e; }
td b, th.destaque { color:#0f766e; }
td.num { font-variant-numeric:tabular-nums; font-weight:640; color:#15302c; }
.capa { background:linear-gradient(135deg,#0d2b28 0%,#124f49 100%); color:#fff; justify-content:center; }
.capa h1 { font-size:80px; color:#fff; }
.capa p { color:#9fc5c0; font-size:25px; margin-top:16px; }
.capa .time { margin-top:auto; font-size:16px; color:#7fada8; line-height:1.8; }
.marca { display:flex; align-items:center; gap:16px; margin-bottom:28px; }
.marca-c { width:56px; height:56px; border-radius:50%; background:#0f766e; display:flex; align-items:center; justify-content:center; color:#fff; font-size:30px; font-weight:650; }
.caixa { background:#f3f8f7; border-left:4px solid #0f766e; border-radius:0 10px 10px 0; padding:22px 26px; }
.caixa p { font-size:20px; color:#15302c; }
.alertabox { background:#fdf4f2; border-left-color:#b4352c; }
.fluxo { display:flex; gap:12px; align-items:stretch; margin-top:14px; }
.passo { flex:1; background:#f3f8f7; border-radius:10px; padding:18px 16px; font-size:16px; line-height:1.4; color:#3c524e; }
.passo b { display:block; color:#0f766e; font-size:13px; letter-spacing:.07em; text-transform:uppercase; margin-bottom:7px; }
"""


def slide(conteudo, n, classe=""):
    return (f'<section class="s {classe}">{conteudo}'
            f'<div class="rodape">Sutura · FIAP × Oracle Academy · 2026</div>'
            f'<div class="pag">{n}</div></section>')


S = []

S.append(slide("""
<div class="marca"><div class="marca-c">S</div></div>
<h1>Sutura</h1>
<p>A costura entre os sistemas de saúde que não se falam</p>
<div class="time">
  <b>Grupo 60 · Engenharia de Software · 4ESOA</b><br>
  Israel Anacleto Batista · Ryan Aquino dos Santos · Leonardo Bernardes de Sá<br>
  Lucas Maciel Silvestrini · Matheus Medeiros de Castro Gouvêa da Cruz
</div>""", 1, "capa"))

S.append(slide("""
<div class="eyebrow">1 · O problema</div>
<h2>Um paciente não tem um histórico.<br>Tem pedaços espalhados.</h2>
<div class="cols">
  <div class="col">
    <ul>
      <li>O hospital usa um ERP. A clínica usa outro. O laboratório tem o próprio sistema.</li>
      <li><b>Risco clínico:</b> exame repetido, decisão tomada com metade da informação.</li>
      <li><b>Perda financeira:</b> cadastro divergente vira guia glosada, que vira retrabalho.</li>
    </ul>
  </div>
  <div class="col">
    <div class="eyebrow">Glosa inicial · hospitais privados</div>
    <div style="display:flex;align-items:baseline;gap:18px">
      <span style="font-size:46px;color:#8a9a97;font-weight:620">11,89%</span>
      <span style="font-size:34px;color:#8a9a97">→</span>
      <span class="big alerta">15,89%</span>
    </div>
    <p style="margin-top:14px">Quase dobrou em dois anos. Independentemente do quanto volta
    após recurso, <b>o trabalho de contestar explodiu no mesmo ritmo</b>.</p>
    <div class="fonte">Observatório Anahp 2025 · série gerencial · 2023→2024</div>
  </div>
</div>""", 2))

S.append(slide("""
<div class="eyebrow">2 · A solução</div>
<h2>Uma camada por cima dos ERPs que o hospital já usa.<br>Não substitui nenhum.</h2>
<div class="fluxo">
  <div class="passo"><b>Recebe</b>Dados de cada sistema, no padrão FHIR, preservando o documento original</div>
  <div class="passo"><b>Identifica</b>Descobre quais registros são a mesma pessoa, com decisão humana</div>
  <div class="passo"><b>Unifica</b>Devolve um histórico único, com a origem de cada informação rastreada</div>
  <div class="passo"><b>Protege</b>Valida as guias contra o cadastro unificado antes do envio</div>
</div>
<div class="caixa" style="margin-top:34px">
  <p>Na indústria isso tem nome: <b>Master Patient Index</b>. A diferença é que os que existem
  são feitos para grandes redes americanas — e a Sutura é feita para o hospital brasileiro
  de médio porte.</p>
</div>""", 3))

S.append(slide(f"""
<div class="eyebrow">3 · Demonstração do MVP</div>
<h2>O motor recomenda. A pessoa decide.</h2>
<div class="cols">
  <div class="col"><img class="shot" src="{FILA}" alt="Fila de identificação"></div>
  <div class="col">
    <ul>
      <li>Dois registros, <b>o mesmo nome escrito igual</b> — e todo o resto divergindo.</li>
      <li>Um sistema ingênuo juntaria os dois e criaria um <b>prontuário falso</b>. Isso não é
      erro de cadastro, é risco clínico.</li>
      <li>A Sutura dá score <b>10</b> e recomenda manter separado. Sem exceção escrita para o caso.</li>
      <li>Quando falta evidência, vai para <b>revisão humana</b> em vez de chutar.</li>
    </ul>
  </div>
</div>""", 4))

S.append(slide(f"""
<div class="eyebrow">3 · Demonstração do MVP</div>
<h2>Resolver identidade faz o dinheiro aparecer.</h2>
<div class="cols">
  <div class="col"><img class="shot" src="{GUIA}" alt="Guia com evidência e correção"></div>
  <div class="col">
    <ul>
      <li>A carteirinha na guia termina em <b>50</b>. No cadastro unificado, em <b>05</b>.
      Essa guia seria glosada.</li>
      <li>O sistema mostra a evidência lado a lado — não diz só que está errado.</li>
      <li>A correção é <b>campo a campo</b>, escolhida por quem opera, e gravada com usuário,
      data e hora.</li>
    </ul>
    <div class="caixa" style="margin-top:22px">
      <p>Costurar a paciente na tela de identificação <b>apaga alertas aqui sozinho</b> — e
      revela divergências que estavam escondidas atrás de "paciente não identificado".</p>
    </div>
  </div>
</div>""", 5))

S.append(slide("""
<div class="eyebrow">4 · Mercado</div>
<h2>Contado de baixo para cima, com fonte pública.</h2>
<div class="cols">
  <div class="col">
    <table>
      <tr><td>Hospitais no Brasil</td><td class="num">6.500</td></tr>
      <tr><td>× natureza privada (~60%)</td><td class="num">3.900</td></tr>
      <tr><td>× acima de 50 leitos (~32%)</td><td class="num">1.250</td></tr>
      <tr><td><b>+ clínicas com mais de um sistema</b></td><td class="num">—</td></tr>
    </table>
    <p style="margin-top:20px">Mercado endereçável na ordem de <b>R$ 106 milhões/ano</b>.</p>
    <div class="fonte">CNES, fim de 2024 · cada fator verificável</div>
  </div>
  <div class="col">
    <div class="caixa alertabox">
      <p><b>O dado que contraria o nosso próprio pitch:</b> 68,1% dos hospitais brasileiros
      têm até 50 leitos. Nós miramos a <b>minoria</b> do parque, não a maioria.</p>
      <p style="margin-top:14px">É um mercado estreito e de ticket alto: venda consultiva,
      ciclo longo, poucos clientes valendo muito. Autoatendimento não funciona aqui.</p>
    </div>
  </div>
</div>""", 6))

S.append(slide("""
<div class="eyebrow">5 · Modelo de negócio</div>
<h2>Assinatura de plataforma + valor por conector ativo.</h2>
<div class="cols">
  <div class="col">
    <p>O comprador entende a conta: <b>paga pelo que conecta</b>. Cresce com a adoção sem
    punir o uso, e acompanha o custo real — que está no conector.</p>
    <table style="margin-top:22px">
      <tr><td>Faturamento mensal em guias</td><td class="num">R$ 3.600.000</td></tr>
      <tr><td>× glosa inicial (Anahp, 2024)</td><td class="num">15,89%</td></tr>
      <tr><td>× parcela de erro de cadastro<br><span style="font-size:14px;color:#8a9a97">premissa do grupo</span></td><td class="num">25%</td></tr>
      <tr><td><b>Valor endereçado por mês</b></td><td class="num" style="color:#0f766e">R$ 143.010</td></tr>
      <tr><td>Custo da Sutura</td><td class="num">R$ 5.900</td></tr>
    </table>
  </div>
  <div class="col">
    <div class="eyebrow">A relação que explica a venda</div>
    <div class="big">1 : 24</div>
    <p style="margin-top:16px">Para cada real pago, vinte e quatro endereçados. Sobra margem
    para o desconto que toda negociação pede.</p>
    <div class="caixa alertabox" style="margin-top:26px">
      <p style="font-size:18px">Descartamos cobrar <b>percentual da glosa evitada</b>: exige
      auditoria conjunta do contrafactual todo mês e trava a venda no jurídico.</p>
    </div>
  </div>
</div>""", 7))

S.append(slide("""
<div class="eyebrow">6 · Tecnologia utilizada</div>
<h2>A Oracle não é onde o dado mora.<br>É onde o motor roda.</h2>
<div class="cols">
  <div class="col">
    <ul>
      <li><b>UTL_MATCH.JARO_WINKLER_SIMILARITY</b> — similaridade de nome, função nativa do Oracle</li>
      <li><b>SOUNDEX</b> — agrupamento fonético que corta 99% das comparações</li>
      <li><b>Autonomous Database 26ai</b> — persistência e execução dos dois motores</li>
      <li><b>mTLS com wallet</b> — conexão segura</li>
    </ul>
    <p style="margin-top:20px">São <b>views SQL</b>, não código Java. Trazer centenas de
    milhares de registros para a aplicação só para comparar texto desperdiçaria exatamente
    o que o banco faz bem.</p>
  </div>
  <div class="col">
    <table>
      <tr><td>Registros avaliados</td><td class="num">19.999</td></tr>
      <tr><td>Pares comparados</td><td class="num">608.194</td></tr>
      <tr><td>Tempo, em 1 OCPU</td><td class="num">21,3 s</td></tr>
      <tr><td><b>Falsos positivos</b></td><td class="num" style="color:#0f766e">0</td></tr>
    </table>
    <div class="caixa" style="margin-top:26px">
      <p>Medindo, achamos um gargalo: trocar <b>OR por UNION</b> no blocking levou a consulta
      de 47.500 ms para <b>241 ms</b>. Mesmo resultado, <b>197× mais rápido</b>.</p>
    </div>
  </div>
</div>""", 8))

S.append(slide("""
<div class="eyebrow">7 · Diferencial competitivo</div>
<h2>Três coisas que não se copiam em três meses.</h2>
<div class="fluxo" style="margin-top:26px">
  <div class="passo" style="padding:26px 24px">
    <b>De onde viemos</b>
    <span style="font-size:18px">Nosso time trabalha com ERP de saúde. O caso do homônimo e o
    do nome sem acento não saíram de uma reunião — saíram de saber como sistema brasileiro
    grava dado.</span>
  </div>
  <div class="passo" style="padding:26px 24px">
    <b>A gente não finge</b>
    <span style="font-size:18px">O sistema recomenda, a pessoa decide, e quando falta
    evidência vai para revisão humana. Vender cautela é mais difícil que vender automação —
    e é o que ganha quem responde por risco clínico.</span>
  </div>
  <div class="passo" style="padding:26px 24px">
    <b>A gente mediu</b>
    <span style="font-size:18px">608 mil pares, zero falsos positivos, e um gargalo achado e
    corrigido com 197× de ganho. Número medido no lugar de adjetivo.</span>
  </div>
</div>
<div class="caixa alertabox" style="margin-top:34px">
  <p style="font-size:18px"><b>O que não é diferencial, e não apresentamos como se fosse:</b>
  "usamos Oracle Cloud" (qualquer um usa), "temos IA" (todo pitch de 2026 tem),
  "o time é dedicado" (todo time diz).</p>
</div>""", 9))

S.append(slide("""
<div class="eyebrow">8 · Estratégia de crescimento</div>
<h2>Não depende do mercado crescer.</h2>
<div class="cols">
  <div class="col">
    <ul>
      <li><b>Cada cliente cresce sozinho.</b> Começa com dois conectores e vai somando. A
      receita por cliente sobe sem venda nova.</li>
      <li><b>A interoperabilidade virou obrigação.</b> RNDS, TISS e a agenda regulatória
      criam demanda que independe de convencer alguém.</li>
      <li><b>O dado limpo vira insumo.</b> Sustenta back-office, prevenção de glosa e
      ferramentas clínicas — expansão dentro de quem já paga.</li>
    </ul>
  </div>
  <div class="col">
    <div class="eyebrow">Entrada no mercado</div>
    <table>
      <tr><td><b>1 · Piloto</b><br><span style="font-size:15px;color:#8a9a97">Clínicas especializadas: decidem rápido</span></td></tr>
      <tr><td><b>2 · Referência</b><br><span style="font-size:15px;color:#8a9a97">Hospital médio, pela indicação do piloto</span></td></tr>
      <tr><td><b>3 · Canal</b><br><span style="font-size:15px;color:#8a9a97">Integradores e fornecedores de ERP</span></td></tr>
    </table>
    <div class="caixa alertabox" style="margin-top:22px">
      <p style="font-size:17px"><b>O risco que levantamos nós mesmos:</b> se a RNDS resolver
      interoperabilidade nacional, parte do problema some. Mas ela transporta dado <i>entre</i>
      instituições — não resolve identidade <i>dentro</i> de uma que usa cinco sistemas.</p>
    </div>
  </div>
</div>""", 10))

S.append(slide("""
<div class="eyebrow">9 · Projeções</div>
<h2>Premissas abertas, não pesquisa de mercado.</h2>
<table style="margin-top:14px">
  <tr><th>&nbsp;</th><th>Ano 1</th><th>Ano 2</th><th class="destaque">Ano 3</th></tr>
  <tr><td>Clientes ativos</td><td class="num">3</td><td class="num">12</td><td class="num">30</td></tr>
  <tr><td>Conectores por cliente</td><td class="num">2</td><td class="num">3</td><td class="num">4</td></tr>
  <tr><td>Receita mensal por cliente</td><td class="num">R$ 5.100</td><td class="num">R$ 5.900</td><td class="num">R$ 6.700</td></tr>
  <tr><td><b>MRR no fim do ano</b></td><td class="num">R$ 15,3 mil</td><td class="num">R$ 70,8 mil</td><td class="num" style="color:#0f766e">R$ 201 mil</td></tr>
  <tr><td><b>ARR anualizado</b></td><td class="num">R$ 184 mil</td><td class="num">R$ 850 mil</td><td class="num" style="color:#0f766e">R$ 2,41 mi</td></tr>
</table>
<div class="caixa alertabox" style="margin-top:30px">
  <p><b>O que ainda não sabemos:</b> custo de aquisição e ciclo de venda. Ninguém do grupo
  vendeu para hospital. Os dois primeiros clientes existem para medir isso, não para dar lucro.</p>
</div>""", 11))

S.append(slide(f"""
<div class="eyebrow">10 · Time</div>
<h2>Cinco alunos de Engenharia de Software —<br>e um deles trabalha com ERP de saúde.</h2>
<div class="cols">
  <div class="col">
    <p>É de onde vem o conhecimento que está no produto. SINAN, internação, faturamento e
    glosa por dentro. Isso não se lê em relatório.</p>
    <div class="caixa" style="margin-top:28px">
      <p style="font-size:24px;line-height:1.4"><b>A interoperabilidade deixou de ser
      diferencial e virou obrigação.</b> Quem resolver identidade de paciente com
      rastreabilidade vai ser infraestrutura desse setor.</p>
    </div>
    <p style="margin-top:28px;font-size:27px;color:#0f766e;font-weight:640">A Sutura não é
    mais um sistema. É a costura entre eles.</p>
  </div>
  <div class="col"><img class="shot" src="{HISTORICO}" alt="Histórico unificado"></div>
</div>""", 12))

html = ("<!doctype html><html lang='pt-BR'><head><meta charset='utf-8'>"
        f"<title>Sutura — Pitch</title><style>{CSS}</style></head><body>"
        + "".join(S) + "</body></html>")

saida.write_text(html, encoding="utf-8")
print(f"{len(S)} slides -> {saida}")
