package br.com.sutura.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** A identidade costurada a partir de vários registros de origem. */
@Entity
@Table(name = "paciente_mestre")
@Getter
@Setter
@NoArgsConstructor
public class PacienteMestre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome_canonico", nullable = false)
    private String nomeCanonico;

    private String cns;
    private String cpf;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    private String convenio;
    private String carteirinha;
    private String diagnostico;

    @Column(name = "criado_em", insertable = false, updatable = false)
    private OffsetDateTime criadoEm;
}
