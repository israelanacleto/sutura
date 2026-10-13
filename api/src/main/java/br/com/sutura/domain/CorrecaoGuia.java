package br.com.sutura.domain;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Uma linha por campo corrigido. Guia com três campos errados gera três linhas.
 *
 * Guardar o valor anterior é o que permite responder "o que estava na guia antes de alguém
 * mexer" — sem isso a correção seria indistinguível de um dado que sempre esteve certo, e
 * a auditoria perderia o sentido.
 */
@Entity
@Table(name = "correcao_guia")
@Getter
@Setter
@NoArgsConstructor
public class CorrecaoGuia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "guia_id")
    private GuiaFaturamento guia;

    @Column(nullable = false)
    private String campo;

    @Column(name = "valor_anterior")
    private String valorAnterior;

    @Column(name = "valor_novo")
    private String valorNovo;

    @Column(nullable = false)
    private String usuario;

    @Column(name = "corrigido_em", insertable = false, updatable = false)
    private OffsetDateTime corrigidoEm;
}
