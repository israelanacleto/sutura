package br.com.sutura.web;

import br.com.sutura.service.IdentificacaoService;
import br.com.sutura.web.Dtos.CandidatoDto;
import br.com.sutura.web.Dtos.DecisaoRequest;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/candidatos")
public class CandidatoController {

    private final IdentificacaoService servico;

    public CandidatoController(IdentificacaoService servico) {
        this.servico = servico;
    }

    @GetMapping
    public List<CandidatoDto> listar() {
        return servico.listar();
    }

    @PostMapping("/{id}/decisao")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void decidir(@PathVariable String id, @RequestBody DecisaoRequest requisicao) {
        servico.decidir(id, requisicao);
    }
}
