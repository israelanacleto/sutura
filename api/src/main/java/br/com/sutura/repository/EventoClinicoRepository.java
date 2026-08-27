package br.com.sutura.repository;

import br.com.sutura.domain.EventoClinico;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventoClinicoRepository extends JpaRepository<EventoClinico, Long> {

    List<EventoClinico> findByRegistroOrigemIdInOrderByDataEventoDesc(Collection<Long> registroOrigemIds);

    List<EventoClinico> findByRegistroOrigemIdOrderByDataEventoDesc(Long registroOrigemId);
}
