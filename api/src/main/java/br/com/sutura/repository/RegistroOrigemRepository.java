package br.com.sutura.repository;

import br.com.sutura.domain.RegistroOrigem;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistroOrigemRepository extends JpaRepository<RegistroOrigem, Long> {

    long countBySistemaId(Long sistemaId);

    Optional<RegistroOrigem> findBySistemaIdAndIdentificadorOrigem(Long sistemaId, String identificadorOrigem);
}
