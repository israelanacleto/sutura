package br.com.sutura.web;

import br.com.sutura.service.ConexaoService;
import br.com.sutura.web.Dtos.ConexaoDto;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/conexoes")
public class ConexaoController {

    private final ConexaoService servico;

    public ConexaoController(ConexaoService servico) {
        this.servico = servico;
    }

    @GetMapping
    public List<ConexaoDto> listar() {
        return servico.listar();
    }
}
