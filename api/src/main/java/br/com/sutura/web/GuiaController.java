package br.com.sutura.web;

import br.com.sutura.service.GlosaService;
import br.com.sutura.web.Dtos.CorrecaoRequest;
import br.com.sutura.web.Dtos.GuiaDetalheDto;
import br.com.sutura.web.Dtos.PainelGuiasDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/guias")
public class GuiaController {

    private final GlosaService servico;

    public GuiaController(GlosaService servico) {
        this.servico = servico;
    }

    /** A tela inteira: o valor em risco no topo e a lista, em uma chamada só. */
    @GetMapping
    public PainelGuiasDto listar() {
        return servico.listar();
    }

    @GetMapping("/{id}")
    public GuiaDetalheDto detalhar(@PathVariable Long id) {
        return servico.detalhar(id);
    }

    @PostMapping("/{id}/corrigir")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void corrigir(@PathVariable Long id, @RequestBody CorrecaoRequest requisicao) {
        servico.corrigir(id, requisicao);
    }
}
