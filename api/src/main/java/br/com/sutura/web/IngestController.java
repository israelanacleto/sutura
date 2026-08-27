package br.com.sutura.web;

import br.com.sutura.ingest.IngestService;
import br.com.sutura.web.Dtos.IngestResumoDto;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/v1/ingest")
public class IngestController {

    private final IngestService servico;

    public IngestController(IngestService servico) {
        this.servico = servico;
    }

    /**
     * Envio direto do JSON. Na demonstração:
     * curl -X POST "http://localhost:8080/v1/ingest/fhir?sistema=TASY"
     *      -H "Content-Type: application/json" --data-binary "@bundle-exemplo.json"
     */
    @PostMapping(value = "/fhir", consumes = MediaType.APPLICATION_JSON_VALUE)
    public IngestResumoDto ingerir(@RequestParam(defaultValue = "TASY") String sistema,
                                   @RequestBody String bundle) {
        return servico.ingerir(sistema, bundle);
    }

    /** Mesma operação por upload de arquivo, para quem preferir demonstrar assim. */
    @PostMapping(value = "/fhir", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public IngestResumoDto ingerirArquivo(@RequestParam(defaultValue = "TASY") String sistema,
                                          @RequestPart("arquivo") MultipartFile arquivo) throws IOException {
        return servico.ingerir(sistema, new String(arquivo.getBytes(), StandardCharsets.UTF_8));
    }
}
