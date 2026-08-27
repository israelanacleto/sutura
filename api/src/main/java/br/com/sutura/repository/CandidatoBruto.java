package br.com.sutura.repository;

import br.com.sutura.domain.Recomendacao;
import java.math.BigDecimal;

/** Uma linha da view vw_candidato_identificacao, como o banco a devolve. */
public record CandidatoBruto(
        Long registroAId,
        Long registroBId,
        int pesoComparavel,
        int similaridadeNome,
        BigDecimal score,
        Recomendacao recomendacao) {
}
