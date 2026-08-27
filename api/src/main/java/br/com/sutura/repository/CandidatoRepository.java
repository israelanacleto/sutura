package br.com.sutura.repository;

import br.com.sutura.domain.Recomendacao;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

/**
 * Lê o motor de identificação. É JdbcClient e não JPA de propósito: o score é o coração
 * do produto e fica calculado em SQL, com UTL_MATCH do próprio Oracle. Esconder isso
 * atrás de um ORM tornaria ilegível justamente o que importa.
 */
@Repository
public class CandidatoRepository {

    private static final String COLUNAS = """
            SELECT registro_a_id, registro_b_id, peso_comparavel,
                   similaridade_nome, score, recomendacao
              FROM vw_candidato_identificacao
            """;

    private final JdbcClient jdbc;

    public CandidatoRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    public List<CandidatoBruto> listar() {
        return jdbc.sql(COLUNAS + " ORDER BY score DESC, registro_a_id")
                .query(this::mapear)
                .list();
    }

    public Optional<CandidatoBruto> buscar(Long registroAId, Long registroBId) {
        return jdbc.sql(COLUNAS + " WHERE registro_a_id = :a AND registro_b_id = :b")
                .param("a", registroAId)
                .param("b", registroBId)
                .query(this::mapear)
                .optional();
    }

    private CandidatoBruto mapear(java.sql.ResultSet rs, int linha) throws java.sql.SQLException {
        return new CandidatoBruto(
                rs.getLong("registro_a_id"),
                rs.getLong("registro_b_id"),
                rs.getInt("peso_comparavel"),
                rs.getInt("similaridade_nome"),
                rs.getBigDecimal("score"),
                Recomendacao.valueOf(rs.getString("recomendacao")));
    }
}
