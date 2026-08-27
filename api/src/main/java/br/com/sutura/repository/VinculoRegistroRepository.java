package br.com.sutura.repository;

import br.com.sutura.domain.VinculoRegistro;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VinculoRegistroRepository extends JpaRepository<VinculoRegistro, Long> {

    List<VinculoRegistro> findByPacienteMestreIdOrderByIdAsc(Long pacienteMestreId);

    Optional<VinculoRegistro> findByRegistroOrigemId(Long registroOrigemId);
}
