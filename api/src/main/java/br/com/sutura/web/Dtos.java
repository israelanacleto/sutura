package br.com.sutura.web;

import java.util.List;

/**
 * Contratos da API. Os nomes dos campos espelham o que as telas já consomem hoje em
 * web/src/app/core/models.ts — a troca do store por HTTP não deve exigir renomear nada
 * nos componentes.
 */
public final class Dtos {

    private Dtos() {
    }

    public record ConexaoDto(
            String id,
            String nome,
            String fornecedor,
            String unidade,
            String protocolo,
            String status,
            String ultimaSync,
            long registros,
            String observacao) {
    }

    public record RegistroResumoDto(
            String sistema,
            String nome,
            String identificador,
            String unidade) {
    }

    public record ComparacaoCampoDto(
            String campo,
            String a,
            String b,
            String situacao) {
    }

    public record CandidatoDto(
            String id,
            int score,
            String recomendacao,
            String justificativa,
            RegistroResumoDto ladoA,
            RegistroResumoDto ladoB,
            List<ComparacaoCampoDto> campos) {
    }

    public record DecisaoRequest(
            String decisao,
            String usuario,
            String justificativa) {
    }

    public record EventoDto(
            String id,
            String data,
            String titulo,
            String categoria,
            String sistema,
            String unidade,
            String detalhe,
            String ciclo) {
    }

    public record CadastroFragmentadoDto(
            String sistema,
            String nome,
            String identificador,
            String cns,
            String cpf,
            String unidade,
            List<EventoDto> eventos) {
    }

    public record PacienteDto(
            String id,
            String nome,
            String nascimento,
            int idade,
            String cns,
            String cpf,
            String convenio,
            String carteirinha,
            String diagnostico,
            List<String> fontes,
            List<CadastroFragmentadoDto> cadastros,
            List<EventoDto> eventos) {
    }

    public record IngestResumoDto(
            String sistema,
            int registrosCriados,
            int registrosJaExistentes,
            int eventosCriados) {
    }

    // ------------------------------------------------------------------ glosa

    /**
     * Um alerta de risco. Quando `campo` é nulo não há o que corrigir na guia — o problema
     * está na identificação, e a tela troca o botão de correção pelo link para a fila.
     */
    public record AlertaDto(
            String codigo,
            String descricao,
            String orientacao,
            String severidade,
            String campo,
            String valorNaGuia,
            String valorNoCadastro,
            Integer similaridadeNome,
            boolean corrigivel) {
    }

    public record GuiaResumoDto(
            String id,
            String numeroGuia,
            String paciente,
            String convenio,
            String procedimento,
            String valor,
            String dataEmissao,
            String severidade,
            List<String> alertas,
            boolean bloqueadaPorIdentificacao) {
    }

    /** A tela inteira em uma chamada: o número do topo e a lista. */
    public record PainelGuiasDto(
            String valorEmRisco,
            int guiasComAlerta,
            long guiasProntas,
            List<GuiaResumoDto> guias) {
    }

    public record GuiaDetalheDto(
            String id,
            String numeroGuia,
            String paciente,
            String convenio,
            String procedimento,
            String valor,
            String dataEmissao,
            String status,
            String sistema,
            String unidade,
            String parPendenteId,
            List<AlertaDto> alertas,
            List<ComparacaoCampoDto> campos) {
    }

    public record CorrecaoRequest(
            List<String> campos,
            String usuario) {
    }
}
