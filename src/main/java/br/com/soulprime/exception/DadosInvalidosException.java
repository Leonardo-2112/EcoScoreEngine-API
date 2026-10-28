package br.com.soulprime.exception;

//Dado de entrada invalido que o Bean Validation nao detecta, ex.: CPF com digito errado (HTTP 400)
public class DadosInvalidosException extends RuntimeException {

    public DadosInvalidosException(String msg) {
        super(msg);
    }
}
