package br.com.sutura.domain;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A costura. O UNIQUE em registro_origem_id (no schema) garante que um registro
 * pertence a no máximo um paciente mestre — é o banco recusando o prontuário falso.
 */
@Entity
@Table(name = "vinculo_registro")
@Getter
@Setter
@NoArgsConstructor
public class VinculoRegistro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "paciente_mestre_id", nullable = false)
    private PacienteMestre pacienteMestre;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "registro_origem_id", nullable = false, unique = true)
    private RegistroOrigem registroOrigem;

    @Column(name = "criado_em", insertable = false, updatable = false)
    private OffsetDateTime criadoEm;
}
