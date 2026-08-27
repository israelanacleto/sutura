package br.com.sutura.ingest;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

/**
 * Carrega o Bundle FHIR de demonstração que acompanha a aplicação.
 *
 * Existe para que o botão "Sincronizar agora" da tela de conexões faça uma ingestão de
 * verdade em vez de simular uma. É dado fictício, mas o caminho percorrido é o real:
 * parser, gravação com payload bruto preservado e reavaliação da fila de identificação.
 */
@Component
public class BundleDeExemplo {

    private static final String CAMINHO = "fhir/bundle-exemplo.json";

    public String conteudo() {
        try {
            return new ClassPathResource(CAMINHO)
                    .getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Bundle de demonstração não encontrado em " + CAMINHO, e);
        }
    }
}
