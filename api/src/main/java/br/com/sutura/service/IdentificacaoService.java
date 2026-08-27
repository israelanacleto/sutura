package br.com.sutura.service;

import br.com.sutura.domain.*;
import br.com.sutura.repository.*;
import br.com.sutura.web.Dtos.CandidatoDto;
import br.com.sutura.web.Dtos.ComparacaoCampoDto;
import br.com.sutura.web.Dtos.DecisaoRequest;
import br.com.sutura.web.Dtos.RegistroResumoDto;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Identificação de pacientes (record linkage).
 *
 * O score vem pronto do banco — ver V3__view_candidatos.sql. O que este serviço faz é
 * traduzir aquele número em algo que uma pessoa consiga auditar: qual campo bateu, qual
 * divergiu, qual estava ausente. Sem isso o operador estaria confiando num número que não
 * pode conferir, que é exatamente o que não se pode fazer com dado de saúde.
 */
@Service
public class IdentificacaoService {

    /** Abaixo deste peso de evidência comparável, nenhuma decisão é automática. */
    private static final int PISO_DE_EVIDENCIA = 60;

    private static final String IGUAL = "igual";
    private static final String DIVERGENTE = "divergente";
    private static final String AUSENTE = "ausente";

    private final CandidatoRepository candidatoRepository;
    private final RegistroOrigemRepository registroRepository;
    private final VinculoRegistroRepository vinculoRepository;
    private final PacienteMestreRepository pacienteRepository;
    private final DecisaoIdentificacaoRepository decisaoRepository;

    public IdentificacaoService(CandidatoRepository candidatoRepository,
                                RegistroOrigemRepository registroRepository,
                                VinculoRegistroRepository vinculoRepository,
                                PacienteMestreRepository pacienteRepository,
                                DecisaoIdentificacaoRepository decisaoRepository) {
        this.candidatoRepository = candidatoRepository;
        this.registroRepository = registroRepository;
        this.vinculoRepository = vinculoRepository;
        this.pacienteRepository = pacienteRepository;
        this.decisaoRepository = decisaoRepository;
    }

    @Transactional(readOnly = true)
    public List<CandidatoDto> listar() {
        List<CandidatoBruto> brutos = candidatoRepository.listar();
        if (brutos.isEmpty()) {
            return List.of();
        }

        List<Long> ids = brutos.stream()
                .flatMap(c -> java.util.stream.Stream.of(c.registroAId(), c.registroBId()))
                .distinct()
                .toList();

        Map<Long, RegistroOrigem> registros = registroRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(RegistroOrigem::getId, Function.identity()));

        return brutos.stream()
                .map(bruto -> montar(bruto, registros.get(bruto.registroAId()), registros.get(bruto.registroBId())))
                .filter(Objects::nonNull)
                .toList();
    }

    private CandidatoDto montar(CandidatoBruto bruto, RegistroOrigem a, RegistroOrigem b) {
        if (a == null || b == null) {
            return null;
        }
        List<ComparacaoCampoDto> campos = compararCampos(a, b);
        return new CandidatoDto(
                idDoCandidato(bruto.registroAId(), bruto.registroBId()),
                bruto.score().intValue(),
                bruto.recomendacao().name().toLowerCase(),
                justificar(campos, bruto),
                resumir(a),
                resumir(b),
                campos);
    }

    private List<ComparacaoCampoDto> compararCampos(RegistroOrigem a, RegistroOrigem b) {
        List<ComparacaoCampoDto> campos = new ArrayList<>();
        campos.add(comparar("CNS", Formatador.cns(a.getCns()), Formatador.cns(b.getCns()), a.getCns(), b.getCns()));
        campos.add(comparar("CPF", Formatador.cpf(a.getCpf()), Formatador.cpf(b.getCpf()), a.getCpf(), b.getCpf()));
        campos.add(comparar("Data de nascimento",
                Formatador.data(a.getDataNascimento()), Formatador.data(b.getDataNascimento()),
                a.getDataNascimento(), b.getDataNascimento()));
        campos.add(comparar("Nome da mãe",
                Formatador.texto(a.getNomeMae()), Formatador.texto(b.getNomeMae()),
                a.getNomeMae(), b.getNomeMae()));
        campos.add(comparar("Nome", a.getNome(), b.getNome(), a.getNome(), b.getNome()));
        return campos;
    }

    private ComparacaoCampoDto comparar(String rotulo, String exibicaoA, String exibicaoB,
                                        Object valorA, Object valorB) {
        String situacao;
        if (vazio(valorA) || vazio(valorB)) {
            situacao = AUSENTE;
        } else if (valorA.equals(valorB)) {
            situacao = IGUAL;
        } else {
            situacao = DIVERGENTE;
        }
        return new ComparacaoCampoDto(rotulo, exibicaoA, exibicaoB, situacao);
    }

    private boolean vazio(Object valor) {
        return valor == null || (valor instanceof String texto && texto.isBlank());
    }

    /**
     * Monta a frase que o operador lê antes de decidir. Não é enfeite: é a diferença entre
     * "o sistema disse 97%" e "CNS e data de nascimento conferem, o nome difere na grafia".
     */
    private String justificar(List<ComparacaoCampoDto> campos, CandidatoBruto bruto) {
        List<String> conferem = porSituacao(campos, IGUAL);
        List<String> divergem = porSituacao(campos, DIVERGENTE);
        List<String> ausentes = porSituacao(campos, AUSENTE);

        StringBuilder frase = new StringBuilder();
        if (!conferem.isEmpty()) {
            frase.append(listar(conferem))
                 .append(conferem.size() == 1 ? " confere." : " conferem.");
        }
        if (!divergem.isEmpty()) {
            if (!frase.isEmpty()) {
                frase.append(' ');
            }
            frase.append("Diverge").append(divergem.size() == 1 ? ": " : "m: ")
                 .append(listar(divergem)).append('.');
        }
        if (!ausentes.isEmpty()) {
            if (!frase.isEmpty()) {
                frase.append(' ');
            }
            frase.append("Sem informação nos dois lados para ").append(listar(ausentes)).append('.');
        }
        if (bruto.pesoComparavel() < PISO_DE_EVIDENCIA) {
            frase.append(" Evidência insuficiente para decisão automática — requer revisão humana.");
        }
        return frase.toString().trim();
    }

    private List<String> porSituacao(List<ComparacaoCampoDto> campos, String situacao) {
        return campos.stream()
                .filter(c -> c.situacao().equals(situacao))
                .map(c -> c.campo().toLowerCase())
                .toList();
    }

    private String listar(List<String> itens) {
        if (itens.size() == 1) {
            return itens.get(0);
        }
        return String.join(", ", itens.subList(0, itens.size() - 1)) + " e " + itens.get(itens.size() - 1);
    }

    private RegistroResumoDto resumir(RegistroOrigem registro) {
        return new RegistroResumoDto(
                registro.getSistema().getCodigo().toLowerCase(),
                registro.getNome(),
                registro.getIdentificadorOrigem(),
                registro.getSistema().getUnidade());
    }

    // ------------------------------------------------------------------ decisão

    @Transactional
    public void decidir(String idDoCandidato, DecisaoRequest requisicao) {
        long[] par = interpretarId(idDoCandidato);
        CandidatoBruto bruto = candidatoRepository.buscar(par[0], par[1])
                .orElseThrow(() -> new CandidatoIndisponivelException(
                        "Par %s não está na fila — pode já ter sido decidido.".formatted(idDoCandidato)));

        Decisao decisao = Decisao.valueOf(requisicao.decisao().toUpperCase());

        DecisaoIdentificacao registro = new DecisaoIdentificacao();
        registro.setRegistroAId(bruto.registroAId());
        registro.setRegistroBId(bruto.registroBId());
        registro.setScore(bruto.score());
        registro.setRecomendacao(bruto.recomendacao());
        registro.setDecisao(decisao);
        registro.setUsuario(requisicao.usuario());
        registro.setJustificativa(requisicao.justificativa());
        decisaoRepository.save(registro);

        if (decisao == Decisao.COSTURADO) {
            costurar(bruto.registroAId(), bruto.registroBId());
        }
        // SEPARADO não cria vínculo nenhum: a decisão registrada já tira o par da fila,
        // porque a view exclui pares que constam em decisao_identificacao.
    }

    private void costurar(Long registroAId, Long registroBId) {
        Optional<VinculoRegistro> vinculoA = vinculoRepository.findByRegistroOrigemId(registroAId);
        Optional<VinculoRegistro> vinculoB = vinculoRepository.findByRegistroOrigemId(registroBId);

        if (vinculoA.isPresent() && vinculoB.isPresent()) {
            Long mestreA = vinculoA.get().getPacienteMestre().getId();
            Long mestreB = vinculoB.get().getPacienteMestre().getId();
            if (!mestreA.equals(mestreB)) {
                // Fundir dois pacientes mestres já formados é outra operação, com outras
                // consequências de auditoria. Não acontece por acidente aqui.
                throw new CandidatoIndisponivelException(
                        "Os dois registros já pertencem a pacientes diferentes (%d e %d). A fusão de pacientes mestres não faz parte desta fase."
                                .formatted(mestreA, mestreB));
            }
            return;
        }

        if (vinculoA.isPresent()) {
            vincular(vinculoA.get().getPacienteMestre(), registroBId);
        } else if (vinculoB.isPresent()) {
            vincular(vinculoB.get().getPacienteMestre(), registroAId);
        } else {
            RegistroOrigem base = registroRepository.findById(registroAId).orElseThrow();
            PacienteMestre novo = new PacienteMestre();
            novo.setNomeCanonico(base.getNome());
            novo.setCns(base.getCns());
            novo.setCpf(base.getCpf());
            novo.setDataNascimento(base.getDataNascimento());
            PacienteMestre salvo = pacienteRepository.save(novo);
            vincular(salvo, registroAId);
            vincular(salvo, registroBId);
        }
    }

    private void vincular(PacienteMestre mestre, Long registroOrigemId) {
        VinculoRegistro vinculo = new VinculoRegistro();
        vinculo.setPacienteMestre(mestre);
        vinculo.setRegistroOrigem(registroRepository.findById(registroOrigemId).orElseThrow());
        vinculoRepository.save(vinculo);
    }

    // ------------------------------------------------------------------ id composto

    /** O candidato não é uma linha persistida: sua identidade é o par de registros. */
    private String idDoCandidato(Long a, Long b) {
        return a + "-" + b;
    }

    private long[] interpretarId(String id) {
        String[] partes = id.split("-");
        if (partes.length != 2) {
            throw new IllegalArgumentException("Identificador de candidato inválido: " + id);
        }
        return new long[]{Long.parseLong(partes[0]), Long.parseLong(partes[1])};
    }

    public static class CandidatoIndisponivelException extends RuntimeException {
        public CandidatoIndisponivelException(String mensagem) {
            super(mensagem);
        }
    }
}
