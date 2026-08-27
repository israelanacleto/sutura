package br.com.sutura.ingest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Component;

/**
 * Lê um Bundle FHIR R4 e extrai o subconjunto que a Sutura precisa.
 *
 * Deliberadamente não usa HAPI FHIR: o objetivo aqui é provar interoperabilidade de
 * entrada, não implementar o padrão inteiro. O documento original é preservado em
 * registro_origem.payload_bruto, então quando o parser evoluir (ou quando HAPI entrar no
 * lugar dele) dá para reprocessar tudo sem pedir os dados de novo ao ERP.
 *
 * Recursos reconhecidos:
 *   Patient      -> registro de origem
 *   Procedure    -> evento (infusão / cirurgia / consulta, pelo texto do código)
 *   Observation  -> evento (exame)
 */
@Component
public class FhirBundleParser {

    private final ObjectMapper mapper;

    public FhirBundleParser(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public BundleLido ler(String json) {
        try {
            JsonNode raiz = mapper.readTree(json);
            if (!"Bundle".equals(texto(raiz.path("resourceType")))) {
                throw new BundleInvalidoException("O documento não é um Bundle FHIR.");
            }

            List<PacienteLido> pacientes = new ArrayList<>();
            List<EventoLido> eventos = new ArrayList<>();

            for (JsonNode entrada : raiz.path("entry")) {
                JsonNode recurso = entrada.path("resource");
                switch (texto(recurso.path("resourceType"))) {
                    case "Patient" -> pacientes.add(lerPaciente(recurso));
                    case "Procedure" -> eventos.add(lerProcedimento(recurso));
                    case "Observation" -> eventos.add(lerObservacao(recurso));
                    default -> {
                        // recurso não usado nesta fase; o payload bruto continua guardado
                    }
                }
            }

            if (pacientes.isEmpty()) {
                throw new BundleInvalidoException("O Bundle não contém nenhum recurso Patient.");
            }
            return new BundleLido(pacientes, eventos);

        } catch (BundleInvalidoException e) {
            throw e;
        } catch (Exception e) {
            throw new BundleInvalidoException("Falha ao interpretar o Bundle: " + e.getMessage());
        }
    }

    private PacienteLido lerPaciente(JsonNode recurso) {
        JsonNode nome = recurso.path("name").path(0);
        String dados = String.join(" ", textos(nome.path("given")));
        String completo = (dados + " " + texto(nome.path("family"))).trim();

        return new PacienteLido(
                texto(recurso.path("id")),
                completo.toUpperCase(Locale.ROOT),
                identificadorPor(recurso, "cns"),
                identificadorPor(recurso, "cpf"),
                nomeDaMae(recurso),
                data(recurso.path("birthDate")),
                sexo(texto(recurso.path("gender"))),
                recurso.toString());
    }

    /**
     * CNS e CPF chegam em Patient.identifier, distinguidos pelo campo system. Aceita
     * qualquer system cujo texto contenha "cns"/"cns" ou "cpf" — os ERPs brasileiros
     * variam a URL, e travar numa URL exata quebraria na primeira integração real.
     */
    private String identificadorPor(JsonNode recurso, String tipo) {
        for (JsonNode identificador : recurso.path("identifier")) {
            String system = texto(identificador.path("system")).toLowerCase(Locale.ROOT);
            if (system.contains(tipo)) {
                return texto(identificador.path("value")).replaceAll("\\D", "");
            }
        }
        return null;
    }

    private String nomeDaMae(JsonNode recurso) {
        // Padrão brasileiro: contato com relationship MTH.
        for (JsonNode contato : recurso.path("contact")) {
            for (JsonNode relacao : contato.path("relationship")) {
                for (JsonNode codigo : relacao.path("coding")) {
                    if ("MTH".equalsIgnoreCase(texto(codigo.path("code")))) {
                        JsonNode nome = contato.path("name");
                        String dados = String.join(" ", textos(nome.path("given")));
                        return (dados + " " + texto(nome.path("family"))).trim().toUpperCase(Locale.ROOT);
                    }
                }
            }
        }
        return null;
    }

    private EventoLido lerProcedimento(JsonNode recurso) {
        String titulo = texto(recurso.path("code").path("text"));
        return new EventoLido(
                referencia(recurso),
                data(recurso.path("performedDateTime")),
                categoriaPorTitulo(titulo),
                titulo,
                texto(recurso.path("note").path(0).path("text")),
                null);
    }

    private EventoLido lerObservacao(JsonNode recurso) {
        return new EventoLido(
                referencia(recurso),
                data(recurso.path("effectiveDateTime")),
                "EXAME",
                texto(recurso.path("code").path("text")),
                texto(recurso.path("valueString")),
                null);
    }

    /** A categoria vem do texto do procedimento — heurística, e assumidamente uma. */
    private String categoriaPorTitulo(String titulo) {
        String t = titulo.toLowerCase(Locale.ROOT);
        if (t.contains("infus") || t.contains("quimio")) {
            return "INFUSAO";
        }
        if (t.contains("cirurg") || t.contains("ectomia") || t.contains("plastia")) {
            return "CIRURGIA";
        }
        return "CONSULTA";
    }

    private String referencia(JsonNode recurso) {
        String ref = texto(recurso.path("subject").path("reference"));
        return ref.contains("/") ? ref.substring(ref.indexOf('/') + 1) : ref;
    }

    private LocalDate data(JsonNode no) {
        String valor = texto(no);
        if (valor.isBlank()) {
            return null;
        }
        return LocalDate.parse(valor.length() > 10 ? valor.substring(0, 10) : valor);
    }

    private String sexo(String genero) {
        return switch (genero) {
            case "female" -> "F";
            case "male" -> "M";
            default -> null;
        };
    }

    private String texto(JsonNode no) {
        return no.isMissingNode() || no.isNull() ? "" : no.asText();
    }

    private List<String> textos(JsonNode array) {
        List<String> valores = new ArrayList<>();
        array.forEach(no -> valores.add(no.asText()));
        return valores;
    }

    public record PacienteLido(
            String idFhir,
            String nome,
            String cns,
            String cpf,
            String nomeMae,
            LocalDate nascimento,
            String sexo,
            String payloadBruto) {
    }

    public record EventoLido(
            String idFhirDoPaciente,
            LocalDate data,
            String categoria,
            String titulo,
            String detalhe,
            String ciclo) {
    }

    public record BundleLido(List<PacienteLido> pacientes, List<EventoLido> eventos) {
    }

    public static class BundleInvalidoException extends RuntimeException {
        public BundleInvalidoException(String mensagem) {
            super(mensagem);
        }
    }
}
