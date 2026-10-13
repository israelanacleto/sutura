package br.com.sutura.repository;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Uma linha da view vw_risco_glosa: uma guia e UMA regra violada. A mesma guia aparece
 * tantas vezes quantos forem os seus problemas.
 *
 * `campo`, `valorNaGuia` e `valorNoCadastro` vêm nulos nas regras que não têm o que
 * corrigir na guia — paciente não costurado e duplicidade pendente. Nesses casos o
 * problema está uma camada abaixo, na identificação.
 */
public record AlertaGlosaBruto(
        Long guiaId,
        String numeroGuia,
        Long registroOrigemId,
        Long pacienteMestreId,
        String nomePaciente,
        String convenio,
        String procedimento,
        BigDecimal valor,
        LocalDate dataEmissao,
        String status,
        String codigoRegra,
        String descricao,
        String orientacao,
        String severidade,
        int ordemSeveridade,
        String campo,
        String valorNaGuia,
        String valorNoCadastro,
        Integer similaridadeNome) {

    /** Regras cuja solução não está na guia, e sim na fila de identificação. */
    public boolean bloqueiaPorIdentificacao() {
        return campo == null;
    }
}
