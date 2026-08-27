package br.com.sutura.web;

import br.com.sutura.service.PacienteService;
import br.com.sutura.web.Dtos.PacienteDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/pacientes")
public class PacienteController {

    private final PacienteService servico;

    public PacienteController(PacienteService servico) {
        this.servico = servico;
    }

    @GetMapping("/{id}")
    public PacienteDto buscar(@PathVariable Long id) {
        return servico.buscar(id);
    }
}
