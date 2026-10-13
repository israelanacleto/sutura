package br.com.sutura.service;

import br.com.sutura.domain.CorrecaoGuia;
import br.com.sutura.domain.GuiaFaturamento;
import br.com.sutura.domain.PacienteMestre;
import br.com.sutura.domain.VinculoRegistro;
import br.com.sutura.repository.AlertaGlosaBruto;
import br.com.sutura.repository.CorrecaoGuiaRepository;
import br.com.sutura.repository.GuiaFaturamentoRepository;
import br.com.sutura.repository.RiscoGlosaRepository;
import br.com.sutura.repository.VinculoRegistroRepository;
import br.com.sutura.web.Dtos.AlertaDto;
import br.com.sutura.web.Dtos.ComparacaoCampoDto;
import br.com.sutura.web.Dtos.CorrecaoRequest;
import br.com.sutura.web.Dtos.GuiaDetalheDto;
import br.com.sutura.web.Dtos.GuiaResumoDto;
import br.com.sutura.web.Dtos.PainelGuiasDto;
import java.math.BigDecimal;
import java.text.Normalizer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Prevenção de glosa de cadastro.
 *
 * As regras vivem em SQL, na V11. O que este serviço faz é o mesmo que o
 * IdentificacaoService faz do outro lado: transformar um veredito em algo que uma pessoa
 * possa conferir antes de agir — que campo está errado, o que está na guia, o que está no
 * cadastro — e aplicar a correção só no que ela escolher.
 *
 * O paralelo não é estético. É a mesma decisão de produto: em dado de saúde o custo do
 * erro é assimétrico, então o sistema recomenda e a pessoa decide.
 */
@Service
public class GlosaService {

    private static final String STATUS_ENVIADA = "ENVIADA";

    private static final String IGUAL = "igual";
    private static final String DIVERGENTE = "divergente";
    private static final String AUSENTE = "ausente";

    private final RiscoGlosaRepository riscoRepository;
    private final GuiaFaturamentoRepository guiaRepository;
    private final CorrecaoGuiaRepository correcaoRepository;
    private final VinculoRegistroRepository vinculoRepository;

    public GlosaService(RiscoGlosaRepository riscoRepository,
                        GuiaFaturamentoRepository guiaRepository,
                        CorrecaoGuiaRepository correcaoRepository,
                        VinculoRegistroRepository vinculoRepository) {
        this.riscoRepository = riscoRepository;
        this.guiaRepository = guiaRepository;
        this.correcaoRepository = correcaoRepository;
        this.vinculoRepository = vinculoRepository;
    }

    // ------------------------------------------------------------------ listagem

    @Transactional(readOnly = true)
    public PainelGuiasDto listar() {
        List<AlertaGlosaBruto> alertas = riscoRepository.listar();

        // A view já vem ordenada por gravidade e depois por valor. O LinkedHashMap preserva
        // essa ordem: cada guia entra na posição do seu alerta mais grave.
        Map<Long, List<AlertaGlosaBruto>> porGuia = new LinkedHashMap<>();
        for (AlertaGlosaBruto alerta : alertas) {
            porGuia.computeIfAbsent(alerta.guiaId(), id -> new ArrayList<>()).add(alerta);
        }

        List<GuiaResumoDto> guias = porGuia.values().stream().map(this::resumir).toList();

        BigDecimal emRisco = porGuia.values().stream()
                .map(lista -> lista.get(0).valor())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new PainelGuiasDto(
                Formatador.moeda(emRisco),
                guias.size(),
                guiaRepository.countByStatusNot(STATUS_ENVIADA),
                guias);
    }

    private GuiaResumoDto resumir(List<AlertaGlosaBruto> alertas) {
        AlertaGlosaBruto primeiro = alertas.get(0);
        return new GuiaResumoDto(
                String.valueOf(primeiro.guiaId()),
                primeiro.numeroGuia(),
                primeiro.nomePaciente(),
                Formatador.texto(primeiro.convenio()),
                primeiro.procedimento(),
                Formatador.moeda(primeiro.valor()),
                Formatador.data(primeiro.dataEmissao()),
                primeiro.severidade(),
                alertas.stream().map(AlertaGlosaBruto::codigoRegra).toList(),
                alertas.stream().anyMatch(AlertaGlosaBruto::bloqueiaPorIdentificacao));
    }

    // ------------------------------------------------------------------ detalhe

    @Transactional(readOnly = true)
    public GuiaDetalheDto detalhar(Long guiaId) {
        GuiaFaturamento guia = buscar(guiaId);
        List<AlertaGlosaBruto> alertas = riscoRepository.porGuia(guiaId);
        Optional<PacienteMestre> cadastro = cadastroDe(guia);

        // O link "resolver na fila de identificação" só faz sentido se houver de fato um
        // par pendente. Consultar isso é reexecutar o motor de identificação, então só
        // acontece quando algum alerta aponta para lá.
        String parPendente = null;
        if (alertas.stream().anyMatch(AlertaGlosaBruto::bloqueiaPorIdentificacao)) {
            parPendente = riscoRepository.parPendenteDe(guia.getRegistroOrigem().getId())
                    .map(par -> par[0] + "-" + par[1])
                    .orElse(null);
        }

        return new GuiaDetalheDto(
                String.valueOf(guia.getId()),
                guia.getNumeroGuia(),
                guia.getNomePaciente(),
                Formatador.texto(guia.getConvenio()),
                guia.getProcedimento(),
                Formatador.moeda(guia.getValor()),
                Formatador.data(guia.getDataEmissao()),
                guia.getStatus(),
                guia.getRegistroOrigem().getSistema().getCodigo().toLowerCase(),
                guia.getRegistroOrigem().getSistema().getUnidade(),
                parPendente,
                alertas.stream().map(this::traduzir).toList(),
                cadastro.map(mestre -> compararCampos(guia, mestre)).orElseGet(List::of));
    }

    private AlertaDto traduzir(AlertaGlosaBruto bruto) {
        return new AlertaDto(
                bruto.codigoRegra(),
                bruto.descricao(),
                bruto.orientacao(),
                bruto.severidade(),
                bruto.campo(),
                bruto.valorNaGuia(),
                bruto.valorNoCadastro(),
                bruto.similaridadeNome(),
                // Só é corrigível o que tem campo E tem de onde copiar. O Roberto é o
                // exemplo do segundo caso: a guia está sem CNS e o cadastro também.
                bruto.campo() != null && bruto.valorNoCadastro() != null);
    }

    /**
     * A comparação campo a campo, na mesma linguagem visual da tela de identificação:
     * valor da guia à esquerda, valor do cadastro à direita, situação no meio.
     */
    private List<ComparacaoCampoDto> compararCampos(GuiaFaturamento guia, PacienteMestre cadastro) {
        List<ComparacaoCampoDto> campos = new ArrayList<>();
        campos.add(comparar("Nome", guia.getNomePaciente(), cadastro.getNomeCanonico()));
        campos.add(comparar("CNS",
                Formatador.cns(guia.getCnsPaciente()), Formatador.cns(cadastro.getCns()),
                guia.getCnsPaciente(), cadastro.getCns()));
        campos.add(comparar("CPF",
                Formatador.cpf(guia.getCpfPaciente()), Formatador.cpf(cadastro.getCpf()),
                guia.getCpfPaciente(), cadastro.getCpf()));
        campos.add(comparar("Data de nascimento",
                Formatador.data(guia.getNascimentoPaciente()), Formatador.data(cadastro.getDataNascimento()),
                guia.getNascimentoPaciente(), cadastro.getDataNascimento()));
        campos.add(comparar("Carteirinha", guia.getCarteirinha(), cadastro.getCarteirinha()));
        return campos;
    }

    private ComparacaoCampoDto comparar(String rotulo, String naGuia, String noCadastro) {
        return comparar(rotulo, Formatador.texto(naGuia), Formatador.texto(noCadastro), naGuia, noCadastro);
    }

    private ComparacaoCampoDto comparar(String rotulo, String exibeGuia, String exibeCadastro,
                                        Object naGuia, Object noCadastro) {
        String situacao;
        if (vazio(naGuia) || vazio(noCadastro)) {
            situacao = AUSENTE;
        } else if (equivalentes(naGuia, noCadastro)) {
            situacao = IGUAL;
        } else {
            situacao = DIVERGENTE;
        }
        return new ComparacaoCampoDto(rotulo, exibeGuia, exibeCadastro, situacao);
    }

    // ------------------------------------------------------------------ correção

    /**
     * Aplica na guia os valores do cadastro unificado, campo a campo, só no que foi pedido.
     *
     * Deliberadamente NÃO existe um "aceitar tudo". O operador escolhe cada campo, pelo
     * mesmo motivo que a costura exige decisão humana: CNS divergente pode significar guia
     * emitida para outra pessoa, e aplicar o cadastro por cima esconderia o problema em vez
     * de resolvê-lo.
     */
    @Transactional
    public void corrigir(Long guiaId, CorrecaoRequest requisicao) {
        if (requisicao == null || requisicao.campos() == null || requisicao.campos().isEmpty()) {
            throw new IllegalArgumentException("Informe ao menos um campo para corrigir.");
        }
        if (requisicao.usuario() == null || requisicao.usuario().isBlank()) {
            throw new IllegalArgumentException("A correção precisa identificar quem a fez.");
        }

        GuiaFaturamento guia = buscar(guiaId);
        PacienteMestre cadastro = cadastroDe(guia).orElseThrow(() -> new CorrecaoIndisponivelException(
                "A guia %s não tem cadastro unificado para copiar. Resolva a identificação primeiro."
                        .formatted(guia.getNumeroGuia())));

        List<CorrecaoGuia> auditoria = new ArrayList<>();
        String usuario = requisicao.usuario().trim();

        for (String pedido : requisicao.campos()) {
            String campo = pedido == null ? "" : pedido.trim().toLowerCase();
            switch (campo) {
                case "nome" -> aplicarTexto(auditoria, guia, usuario, "nome",
                        guia.getNomePaciente(), cadastro.getNomeCanonico(), guia::setNomePaciente);
                case "cns" -> aplicarTexto(auditoria, guia, usuario, "cns",
                        guia.getCnsPaciente(), cadastro.getCns(), guia::setCnsPaciente);
                case "cpf" -> aplicarTexto(auditoria, guia, usuario, "cpf",
                        guia.getCpfPaciente(), cadastro.getCpf(), guia::setCpfPaciente);
                case "carteirinha" -> aplicarTexto(auditoria, guia, usuario, "carteirinha",
                        guia.getCarteirinha(), cadastro.getCarteirinha(), guia::setCarteirinha);
                case "nascimento" -> aplicarData(auditoria, guia, usuario,
                        guia.getNascimentoPaciente(), cadastro.getDataNascimento(), guia::setNascimentoPaciente);
                default -> throw new IllegalArgumentException(
                        "Campo desconhecido: %s. Corrigíveis: nome, cns, cpf, carteirinha, nascimento."
                                .formatted(pedido));
            }
        }

        if (auditoria.isEmpty()) {
            throw new CorrecaoIndisponivelException(
                    "Nada mudou: os campos pedidos já estão iguais ao cadastro unificado.");
        }

        guiaRepository.save(guia);
        correcaoRepository.saveAll(auditoria);
    }

    private void aplicarTexto(List<CorrecaoGuia> auditoria, GuiaFaturamento guia, String usuario,
                              String campo, String naGuia, String noCadastro, Consumer<String> gravar) {
        if (noCadastro == null || noCadastro.isBlank()) {
            throw new IllegalArgumentException(
                    "O cadastro unificado também não tem %s — não há de onde copiar.".formatted(campo));
        }
        if (Objects.equals(naGuia, noCadastro)) {
            return;
        }
        gravar.accept(noCadastro);
        auditoria.add(montarAuditoria(guia, campo, naGuia, noCadastro, usuario));
    }

    private void aplicarData(List<CorrecaoGuia> auditoria, GuiaFaturamento guia, String usuario,
                             LocalDate naGuia, LocalDate noCadastro, Consumer<LocalDate> gravar) {
        if (noCadastro == null) {
            throw new IllegalArgumentException(
                    "O cadastro unificado também não tem data de nascimento — não há de onde copiar.");
        }
        if (Objects.equals(naGuia, noCadastro)) {
            return;
        }
        gravar.accept(noCadastro);
        auditoria.add(montarAuditoria(guia, "nascimento",
                naGuia == null ? null : Formatador.data(naGuia), Formatador.data(noCadastro), usuario));
    }

    private CorrecaoGuia montarAuditoria(GuiaFaturamento guia, String campo,
                                         String anterior, String novo, String usuario) {
        CorrecaoGuia correcao = new CorrecaoGuia();
        correcao.setGuia(guia);
        correcao.setCampo(campo);
        correcao.setValorAnterior(anterior);
        correcao.setValorNovo(novo);
        correcao.setUsuario(usuario);
        return correcao;
    }

    // ------------------------------------------------------------------ apoio

    private GuiaFaturamento buscar(Long guiaId) {
        return guiaRepository.findById(guiaId)
                .orElseThrow(() -> new NoSuchElementException("Guia %d não encontrada.".formatted(guiaId)));
    }

    private Optional<PacienteMestre> cadastroDe(GuiaFaturamento guia) {
        return vinculoRepository.findByRegistroOrigemId(guia.getRegistroOrigem().getId())
                .map(VinculoRegistro::getPacienteMestre);
    }

    private boolean vazio(Object valor) {
        return valor == null || (valor instanceof String texto && texto.isBlank());
    }

    /**
     * Mesma tolerância que a V11 usa no SQL: acento e caixa não fazem dois valores
     * diferentes. Se as duas comparações divergissem, a tela mostraria "igual" num campo
     * que a view considera divergente — e o operador não teria como entender o alerta.
     */
    private boolean equivalentes(Object a, Object b) {
        if (a instanceof String textoA && b instanceof String textoB) {
            return normalizar(textoA).equals(normalizar(textoB));
        }
        return Objects.equals(a, b);
    }

    private String normalizar(String valor) {
        String semAcento = Normalizer.normalize(valor, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return semAcento.toUpperCase().trim().replaceAll(" {2,}", " ");
    }

    public static class CorrecaoIndisponivelException extends RuntimeException {
        public CorrecaoIndisponivelException(String mensagem) {
            super(mensagem);
        }
    }
}
