package br.com.soulprime.exception;

//Conta suspensa apos 5 tentativas de login erradas - RN02 (HTTP 423)
public class ContaBloqueadaException extends RuntimeException {

    public ContaBloqueadaException(String msg) {
        super(msg);
    }
}
