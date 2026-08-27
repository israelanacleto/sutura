package br.com.sutura.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Libera o front do protótipo (ng serve) a chamar a API durante o desenvolvimento. */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    private final String origemPermitida;

    public CorsConfig(@Value("${sutura.cors.origem-permitida}") String origemPermitida) {
        this.origemPermitida = origemPermitida;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/v1/**")
                .allowedOrigins(origemPermitida)
                .allowedMethods("GET", "POST", "DELETE");
    }
}
