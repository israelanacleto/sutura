package br.com.sutura.service;

import br.com.sutura.domain.EventoClinico;
import br.com.sutura.domain.PacienteMestre;
import br.com.sutura.domain.RegistroOrigem;
import br.com.sutura.domain.VinculoRegistro;
import br.com.sutura.repository.EventoClinicoRepository;
import br.com.sutura.repository.PacienteMestreRepository;
import br.com.sutura.repository.VinculoRegistroRepository;
import br.com.sutura.web.Dtos.CadastroFragmentadoDto;
import br.com.sutura.web.Dtos.EventoDto;
import br.com.sutura.web.Dtos.PacienteDto;
import java.time.LocalDate;
import java.time.Period;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Monta as DUAS visões da mesma pessoa num único payload: a linha do tempo costurada e os
 * cadastros fragmentados por sistema. O alternador "Antes da Sutura / Com a Sutura" da tela
 * precisa das duas ao mesmo tempo, e uma segunda chamada de rede no meio da demonstração
 * seria um risco desnecessário.
 */
@Service
public class PacienteService {

    private final PacienteMestreRepository pacienteRepository;
    private final VinculoRegistroRepository vinculoRepository;
    private final EventoClinicoRepository eventoRepository;

    public PacienteService(PacienteMestreRepository pacienteRepository,
                           VinculoRegistroRepository vinculoRepository,
                           EventoClinicoRepository eventoRepository) {
        this.pacienteRepository = pacienteRepository;
        this.vinculoRepository = vinculoRepository;
        this.eventoRepository = eventoRepository;
    }

    @Transactional(readOnly = true)
    public PacienteDto buscar(Long id) {
        PacienteMestre mestre = pacienteRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Paciente %d não encontrado".formatted(id)));

        List<RegistroOrigem> registros = vinculoRepository.findByPacienteMestreIdOrderByIdAsc(id).stream()
                .map(VinculoRegistro::getRegistroOrigem)
                .toList();

        List<CadastroFragmentadoDto> cadastros = registros.stream()
                .map(this::converterCadastro)
                .toList();

        List<EventoDto> linhaDoTempo = cadastros.stream()
                .flatMap(c -> c.eventos().stream())
                .sorted(Comparator.comparing((EventoDto e) -> LocalDate.parse(e.data(), Formatador.DATA)).reversed())
                .toList();

        List<String> fontes = registros.stream()
                .map(r -> r.getSistema().getCodigo().toLowerCase())
                .distinct()
                .toList();

        return new PacienteDto(
                String.valueOf(mestre.getId()),
                mestre.getNomeCanonico(),
                Formatador.data(mestre.getDataNascimento()),
                idade(mestre.getDataNascimento()),
                Formatador.cns(mestre.getCns()),
                Formatador.cpf(mestre.getCpf()),
                Formatador.texto(mestre.getConvenio()),
                Formatador.texto(mestre.getCarteirinha()),
                Formatador.texto(mestre.getDiagnostico()),
                fontes,
                cadastros,
                linhaDoTempo);
    }

    private CadastroFragmentadoDto converterCadastro(RegistroOrigem registro) {
        List<EventoDto> eventos = eventoRepository
                .findByRegistroOrigemIdOrderByDataEventoDesc(registro.getId()).stream()
                .map(evento -> converterEvento(evento, registro))
                .toList();

        return new CadastroFragmentadoDto(
                registro.getSistema().getCodigo().toLowerCase(),
                registro.getNome(),
                registro.getIdentificadorOrigem(),
                Formatador.cns(registro.getCns()),
                Formatador.cpf(registro.getCpf()),
                registro.getSistema().getUnidade(),
                eventos);
    }

    private EventoDto converterEvento(EventoClinico evento, RegistroOrigem registro) {
        return new EventoDto(
                String.valueOf(evento.getId()),
                Formatador.data(evento.getDataEvento()),
                evento.getTitulo(),
                evento.getCategoria().name().toLowerCase(),
                registro.getSistema().getCodigo().toLowerCase(),
                registro.getSistema().getUnidade(),
                evento.getDetalhe(),
                evento.getCiclo());
    }

    private int idade(LocalDate nascimento) {
        return nascimento == null ? 0 : Period.between(nascimento, LocalDate.now()).getYears();
    }
}
