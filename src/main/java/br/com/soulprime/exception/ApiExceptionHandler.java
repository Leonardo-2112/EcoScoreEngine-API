package br.com.soulprime.exception;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import br.com.soulprime.dto.ErrorResponseDto;

//Trata as excecoes da API e devolve o status HTTP correto com uma mensagem padronizada
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(EntidadeNaoEncontradaException.class)
    public ResponseEntity<ErrorResponseDto> notFound(EntidadeNaoEncontradaException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDto("Not Found", e.getMessage()));
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ErrorResponseDto> regraNegocio(RegraNegocioException e) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body(new ErrorResponseDto("Regra de negocio", e.getMessage()));
    }

    @ExceptionHandler(ConflitoException.class)
    public ResponseEntity<ErrorResponseDto> conflito(ConflitoException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponseDto("Conflict", e.getMessage()));
    }

    @ExceptionHandler(DadosInvalidosException.class)
    public ResponseEntity<ErrorResponseDto> dadosInvalidos(DadosInvalidosException e) {
        return ResponseEntity.badRequest().body(new ErrorResponseDto("Bad Request", e.getMessage()));
    }

    @ExceptionHandler(NaoAutenticadoException.class)
    public ResponseEntity<ErrorResponseDto> naoAutenticado(NaoAutenticadoException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponseDto("Unauthorized", e.getMessage()));
    }

    @ExceptionHandler(AcessoNegadoException.class)
    public ResponseEntity<ErrorResponseDto> acessoNegado(AcessoNegadoException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponseDto("Forbidden", e.getMessage()));
    }

    @ExceptionHandler(ContaBloqueadaException.class)
    public ResponseEntity<ErrorResponseDto> contaBloqueada(ContaBloqueadaException e) {
        return ResponseEntity.status(HttpStatus.LOCKED).body(new ErrorResponseDto("Locked", e.getMessage()));
    }

    //Erros do Bean Validation (@NotBlank, @Size...): devolve o campo e a mensagem
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> validacao(MethodArgumentNotValidException e) {
        Map<String, String> erros = new HashMap<>();
        for (FieldError field : e.getFieldErrors()) {
            erros.put(field.getField(), field.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(erros);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDto> jsonInvalido(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest().body(new ErrorResponseDto("Bad Request", "Corpo da requisicao ausente ou com JSON invalido."));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDto> parametroInvalido(MethodArgumentTypeMismatchException e) {
        return ResponseEntity.badRequest().body(new ErrorResponseDto("Bad Request", "Parametro '" + e.getName() + "' com valor invalido."));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponseDto> rotaInexistente(NoResourceFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDto("Not Found", "Endpoint nao encontrado."));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponseDto> metodoNaoSuportado(HttpRequestMethodNotSupportedException e) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(new ErrorResponseDto("Method Not Allowed", e.getMessage()));
    }

    //Restricao do banco violada (UNIQUE, FK ou CHECK)
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDto> integridade(SQLIntegrityConstraintViolationException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponseDto("Conflict", "Operacao viola uma restricao do banco de dados."));
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ErrorResponseDto> serverInternalError(SQLException e) {
        return ResponseEntity.internalServerError().body(new ErrorResponseDto("Procure o administrador", "Erro ao acessar o banco de dados."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> erroGeral(Exception e) {
        return ResponseEntity.internalServerError().body(new ErrorResponseDto("Procure o administrador", "Erro interno no servidor."));
    }
}
