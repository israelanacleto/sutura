package br.com.sutura.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * O evento pertence ao registro de origem, nunca ao paciente mestre. A ligação com a
 * pessoa é derivada do vínculo — por isso costurar ou desfazer reorganiza a linha do
 * tempo sem tocar em nenhum evento.
 */
@Entity
@Table(name = "evento_clinico")
@Getter
@Setter
@NoArgsConstructor
public class EventoClinico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "registro_origem_id", nullable = false)
    private RegistroOrigem registroOrigem;

    @Column(name = "data_evento", nullable = false)
    private LocalDate dataEvento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoriaEvento categoria;

    @Column(nullable = false)
    private String titulo;

    private String detalhe;
    private String ciclo;
}
