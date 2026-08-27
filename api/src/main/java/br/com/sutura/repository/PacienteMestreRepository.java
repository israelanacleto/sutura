package br.com.sutura.repository;

import br.com.sutura.domain.PacienteMestre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PacienteMestreRepository extends JpaRepository<PacienteMestre, Long> {
}
