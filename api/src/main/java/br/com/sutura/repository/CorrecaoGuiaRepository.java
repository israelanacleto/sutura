package br.com.sutura.repository;

import br.com.sutura.domain.CorrecaoGuia;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CorrecaoGuiaRepository extends JpaRepository<CorrecaoGuia, Long> {

    List<CorrecaoGuia> findByGuiaIdOrderByIdAsc(Long guiaId);
}
