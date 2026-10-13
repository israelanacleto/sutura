package br.com.sutura.web;

import br.com.sutura.ingest.FhirBundleParser.BundleInvalidoException;
import br.com.sutura.service.GlosaService.CorrecaoIndisponivelException;
import br.com.sutura.service.IdentificacaoService.CandidatoIndisponivelException;
import java.time.OffsetDateTime;
import java.util.NoSuchElementException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class TratadorDeErros {

    @ExceptionHandler(NoSuchElementException.class)
    public ProblemDetail naoEncontrado(NoSuchElementException e) {
        return montar(HttpStatus.NOT_FOUND, e.getMessage());
    }

    /**
     * Os dois casos em que a operação é legítima mas o estado atual não permite: par que
     * saiu da fila, e guia sem cadastro unificado de onde copiar. Nenhum dos dois é erro
     * de quem chamou — é o sistema recusando uma ação que faria estrago.
     */
    @ExceptionHandler({CandidatoIndisponivelException.class, CorrecaoIndisponivelException.class})
    public ProblemDetail conflito(RuntimeException e) {
        return montar(HttpStatus.CONFLICT, e.getMessage());
    }

    /**
     * A violação de unicidade em vinculo_registro.registro_origem_id chega aqui: é o banco
     * recusando que um registro pertença a dois pacientes. Isso é 409, não 500 — o sistema
     * funcionou exatamente como deveria.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail integridade(DataIntegrityViolationException e) {
        return montar(HttpStatus.CONFLICT,
                "Operação recusada pelo banco: o registro já pertence a outro paciente.");
    }

    @ExceptionHandler({BundleInvalidoException.class, IllegalArgumentException.class})
    public ProblemDetail requisicaoInvalida(RuntimeException e) {
        return montar(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    private ProblemDetail montar(HttpStatus status, String detalhe) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(status, detalhe);
        problema.setProperty("momento", OffsetDateTime.now().toString());
        return problema;
    }
}
