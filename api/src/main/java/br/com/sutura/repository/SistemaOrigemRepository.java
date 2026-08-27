package br.com.sutura.repository;

import br.com.sutura.domain.SistemaOrigem;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SistemaOrigemRepository extends JpaRepository<SistemaOrigem, Long> {
    Optional<SistemaOrigem> findByCodigo(String codigo);
}
