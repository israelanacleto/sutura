package br.com.sutura.repository;

import br.com.sutura.domain.GuiaFaturamento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuiaFaturamentoRepository extends JpaRepository<GuiaFaturamento, Long> {

    /** Guias que ainda podem ser corrigidas antes do envio — o denominador da tela. */
    long countByStatusNot(String status);
}
