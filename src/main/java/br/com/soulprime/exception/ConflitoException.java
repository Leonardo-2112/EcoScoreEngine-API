package br.com.soulprime.exception;

//Dado duplicado, ex.: e-mail ou CPF ja cadastrado (HTTP 409)
public class ConflitoException extends RuntimeException {

    public ConflitoException(String msg) {
        super(msg);
    }
}
