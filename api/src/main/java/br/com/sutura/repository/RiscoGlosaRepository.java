package br.com.sutura.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

/**
 * Lê o motor de regras de glosa. JdbcClient e não JPA, pela mesma razão do
 * CandidatoRepository: as regras são o produto e ficam legíveis em SQL, na V11. Um ORM
 * aqui esconderia justamente o que precisa ser auditável.
 */
@Repository
public class RiscoGlosaRepository {

    private static final String COLUNAS = """
            SELECT guia_id, numero_guia, registro_origem_id, paciente_mestre_id,
                   nome_paciente, convenio, procedimento, valor, data_emissao, status,
                   codigo_regra, descricao, orientacao, severidade, ordem_severidade,
                   campo, valor_na_guia, valor_no_cadastro, similaridade_nome
              FROM vw_risco_glosa
            """;

    /** Mais grave primeiro, e dentro da mesma gravidade o dinheiro maior primeiro. */
    private static final String ORDEM = " ORDER BY ordem_severidade, valor DESC, guia_id, codigo_regra";

    private final JdbcClient jdbc;

    public RiscoGlosaRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    public List<AlertaGlosaBruto> listar() {
        return jdbc.sql(COLUNAS + ORDEM).query(this::mapear).list();
    }

    public List<AlertaGlosaBruto> porGuia(Long guiaId) {
        return jdbc.sql(COLUNAS + " WHERE guia_id = :id" + ORDEM)
                .param("id", guiaId)
                .query(this::mapear)
                .list();
    }

    /**
     * O primeiro par ainda pendente que envolve este registro, para o link "resolver na
     * fila de identificação".
     *
     * Dois ramos com UNION ALL em vez de um OR: é a mesma correção que a V8 aplicou na
     * view de candidatos, onde o OR levava o otimizador a um MERGE JOIN e o UNION o levava
     * a HASH JOIN. Aqui o volume é pequeno, mas não vale manter na base o padrão que já
     * custou caro uma vez.
     */
    public Optional<long[]> parPendenteDe(Long registroOrigemId) {
        return jdbc.sql("""
                        SELECT * FROM (
                          SELECT registro_a_id AS a, registro_b_id AS b
                            FROM vw_candidato_identificacao WHERE registro_a_id = :r
                          UNION ALL
                          SELECT registro_a_id, registro_b_id
                            FROM vw_candidato_identificacao WHERE registro_b_id = :r
                        ) FETCH FIRST 1 ROWS ONLY
                        """)
                .param("r", registroOrigemId)
                .query((ResultSet rs, int linha) -> new long[]{rs.getLong("a"), rs.getLong("b")})
                .optional();
    }

    private AlertaGlosaBruto mapear(ResultSet rs, int linha) throws SQLException {
        Long mestreId = rs.getObject("paciente_mestre_id", Long.class);
        Integer similaridade = rs.getObject("similaridade_nome", Integer.class);
        return new AlertaGlosaBruto(
                rs.getLong("guia_id"),
                rs.getString("numero_guia"),
                rs.getLong("registro_origem_id"),
                mestreId,
                rs.getString("nome_paciente"),
                rs.getString("convenio"),
                rs.getString("procedimento"),
                rs.getBigDecimal("valor"),
                rs.getObject("data_emissao", java.time.LocalDate.class),
                rs.getString("status"),
                rs.getString("codigo_regra"),
                rs.getString("descricao"),
                rs.getString("orientacao"),
                rs.getString("severidade"),
                rs.getInt("ordem_severidade"),
                rs.getString("campo"),
                rs.getString("valor_na_guia"),
                rs.getString("valor_no_cadastro"),
                similaridade);
    }
}
