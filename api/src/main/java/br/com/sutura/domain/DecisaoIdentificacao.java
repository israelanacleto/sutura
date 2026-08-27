package br.com.sutura.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Trilha de auditoria. Guarda inclusive as decisões de separar, e guarda a recomendação
 * que o motor havia dado — é assim que se responde "quem decidiu, quando, e contrariando
 * o quê".
 */
@Entity
@Table(name = "decisao_identificacao")
@Getter
@Setter
@NoArgsConstructor
public class DecisaoIdentificacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "registro_a_id", nullable = false)
    private Long registroAId;

    @Column(name = "registro_b_id", nullable = false)
    private Long registroBId;

    @Column(nullable = false)
    private BigDecimal score;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Recomendacao recomendacao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Decisao decisao;

    @Column(nullable = false)
    private String usuario;

    private String justificativa;

    @Column(name = "decidido_em", insertable = false, updatable = false)
    private OffsetDateTime decididoEm;
}
