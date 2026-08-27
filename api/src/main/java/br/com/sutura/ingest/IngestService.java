package br.com.sutura.ingest;

import br.com.sutura.domain.CategoriaEvento;
import br.com.sutura.domain.EventoClinico;
import br.com.sutura.domain.RegistroOrigem;
import br.com.sutura.domain.SistemaOrigem;
import br.com.sutura.ingest.FhirBundleParser.BundleLido;
import br.com.sutura.ingest.FhirBundleParser.EventoLido;
import br.com.sutura.ingest.FhirBundleParser.PacienteLido;
import br.com.sutura.repository.EventoClinicoRepository;
import br.com.sutura.repository.RegistroOrigemRepository;
import br.com.sutura.repository.SistemaOrigemRepository;
import br.com.sutura.web.Dtos.IngestResumoDto;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Ingestão. O registro é gravado como veio: nenhuma tentativa de adivinhar identidade
 * acontece aqui. Quem decide se dois registros são a mesma pessoa é o motor de
 * identificação — e, depois dele, uma pessoa.
 */
@Service
public class IngestService {

    private final FhirBundleParser parser;
    private final SistemaOrigemRepository sistemaRepository;
    private final RegistroOrigemRepository registroRepository;
    private final EventoClinicoRepository eventoRepository;

    public IngestService(FhirBundleParser parser,
                         SistemaOrigemRepository sistemaRepository,
                         RegistroOrigemRepository registroRepository,
                         EventoClinicoRepository eventoRepository) {
        this.parser = parser;
        this.sistemaRepository = sistemaRepository;
        this.registroRepository = registroRepository;
        this.eventoRepository = eventoRepository;
    }

    @Transactional
    public IngestResumoDto ingerir(String codigoDoSistema, String bundleJson) {
        SistemaOrigem sistema = sistemaRepository.findByCodigo(codigoDoSistema.toUpperCase())
                .orElseThrow(() -> new NoSuchElementException(
                        "Sistema de origem desconhecido: " + codigoDoSistema));

        BundleLido bundle = parser.ler(bundleJson);

        int criados = 0;
        int jaExistentes = 0;
        Map<String, RegistroOrigem> porIdFhir = new HashMap<>();

        for (PacienteLido paciente : bundle.pacientes()) {
            String identificador = "FHIR/" + paciente.idFhir();
            Optional<RegistroOrigem> existente = registroRepository
                    .findBySistemaIdAndIdentificadorOrigem(sistema.getId(), identificador);

            if (existente.isPresent()) {
                porIdFhir.put(paciente.idFhir(), existente.get());
                jaExistentes++;
                continue;
            }

            RegistroOrigem registro = new RegistroOrigem();
            registro.setSistema(sistema);
            registro.setIdentificadorOrigem(identificador);
            registro.setNome(paciente.nome());
            registro.setNomeMae(paciente.nomeMae());
            registro.setCns(paciente.cns());
            registro.setCpf(paciente.cpf());
            registro.setDataNascimento(paciente.nascimento());
            registro.setSexo(paciente.sexo());
            registro.setPayloadBruto(paciente.payloadBruto());

            porIdFhir.put(paciente.idFhir(), registroRepository.save(registro));
            criados++;
        }

        int eventosCriados = 0;
        for (EventoLido lido : bundle.eventos()) {
            RegistroOrigem dono = porIdFhir.get(lido.idFhirDoPaciente());
            if (dono == null || lido.data() == null || lido.titulo().isBlank()) {
                continue;
            }
            EventoClinico evento = new EventoClinico();
            evento.setRegistroOrigem(dono);
            evento.setDataEvento(lido.data());
            evento.setCategoria(CategoriaEvento.valueOf(lido.categoria()));
            evento.setTitulo(lido.titulo());
            evento.setDetalhe(lido.detalhe());
            evento.setCiclo(lido.ciclo());
            eventoRepository.save(evento);
            eventosCriados++;
        }

        sistema.setUltimaSync(OffsetDateTime.now());
        sistemaRepository.save(sistema);

        return new IngestResumoDto(sistema.getCodigo().toLowerCase(), criados, jaExistentes, eventosCriados);
    }
}
