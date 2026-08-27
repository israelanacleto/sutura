package br.com.sutura.service;

import br.com.sutura.domain.SistemaOrigem;
import br.com.sutura.repository.RegistroOrigemRepository;
import br.com.sutura.repository.SistemaOrigemRepository;
import br.com.sutura.web.Dtos.ConexaoDto;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConexaoService {

    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("dd/MM 'às' HH:mm");

    private final SistemaOrigemRepository sistemaRepository;
    private final RegistroOrigemRepository registroRepository;

    public ConexaoService(SistemaOrigemRepository sistemaRepository,
                          RegistroOrigemRepository registroRepository) {
        this.sistemaRepository = sistemaRepository;
        this.registroRepository = registroRepository;
    }

    @Transactional(readOnly = true)
    public List<ConexaoDto> listar() {
        return sistemaRepository.findAll().stream()
                .sorted(Comparator.comparing(SistemaOrigem::getId))
                .map(this::converter)
                .toList();
    }

    private ConexaoDto converter(SistemaOrigem sistema) {
        return new ConexaoDto(
                sistema.getCodigo().toLowerCase(),
                sistema.getNome(),
                sistema.getFornecedor(),
                sistema.getUnidade(),
                sistema.getProtocolo(),
                sistema.getStatus().name().toLowerCase(),
                formatar(sistema.getUltimaSync()),
                registroRepository.countBySistemaId(sistema.getId()),
                sistema.getObservacao());
    }

    private String formatar(OffsetDateTime momento) {
        return momento == null ? "nunca" : momento.format(HORA);
    }
}
