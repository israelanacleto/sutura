package br.com.sutura.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * O registro como ele existe no sistema de origem. Nunca é sobrescrito nem normalizado:
 * a identidade unificada é construída por cima, não no lugar dele.
 */
@Entity
@Table(name = "registro_origem")
@Getter
@Setter
@NoArgsConstructor
public class RegistroOrigem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "sistema_id", nullable = false)
    private SistemaOrigem sistema;

    @Column(name = "identificador_origem", nullable = false)
    private String identificadorOrigem;

    @Column(nullable = false)
    private String nome;

    @Column(name = "nome_mae")
    private String nomeMae;

    private String cns;
    private String cpf;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    private String sexo;

    /** Documento original recebido (FHIR/HL7), preservado para auditoria e reprocessamento. */
    @Lob
    @Column(name = "payload_bruto")
    private String payloadBruto;

    @Column(name = "ingerido_em", insertable = false, updatable = false)
    private OffsetDateTime ingeridoEm;
}
