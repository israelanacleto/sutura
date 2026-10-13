package br.com.sutura.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A guia como o faturamento a emitiu.
 *
 * Os dados do paciente aqui são uma CÓPIA, não uma referência: nome, CNS, CPF, carteirinha
 * e nascimento foram gravados no momento da emissão. É por serem cópia que podem divergir
 * do cadastro unificado — e é essa divergência que a operadora glosa.
 *
 * Se estes campos fossem um join para o paciente mestre, não haveria problema para
 * detectar, porque não haveria divergência possível.
 */
@Entity
@Table(name = "guia_faturamento")
@Getter
@Setter
@NoArgsConstructor
public class GuiaFaturamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "registro_origem_id")
    private RegistroOrigem registroOrigem;

    @Column(name = "evento_clinico_id")
    private Long eventoClinicoId;

    @Column(name = "numero_guia", nullable = false)
    private String numeroGuia;

    @Column(nullable = false)
    private String procedimento;

    private String convenio;
    private String carteirinha;

    @Column(name = "nome_paciente", nullable = false)
    private String nomePaciente;

    @Column(name = "cns_paciente")
    private String cnsPaciente;

    @Column(name = "cpf_paciente")
    private String cpfPaciente;

    @Column(name = "nascimento_paciente")
    private LocalDate nascimentoPaciente;

    @Column(nullable = false)
    private BigDecimal valor;

    @Column(name = "data_emissao", nullable = false)
    private LocalDate dataEmissao;

    @Column(nullable = false)
    private String status;
}
