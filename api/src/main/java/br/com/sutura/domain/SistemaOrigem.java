package br.com.sutura.domain;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Um ERP ou sistema clínico conectado à camada Sutura. */
@Entity
@Table(name = "sistema_origem")
@Getter
@Setter
@NoArgsConstructor
public class SistemaOrigem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String codigo;

    @Column(nullable = false)
    private String nome;

    private String fornecedor;
    private String unidade;
    private String protocolo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusConexao status = StatusConexao.CONECTADO;

    @Column(name = "ultima_sync")
    private OffsetDateTime ultimaSync;

    private String observacao;
}
