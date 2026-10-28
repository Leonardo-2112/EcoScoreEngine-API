package br.com.soulprime.exception;

//Usuario nao identificado ou e-mail/senha invalidos (HTTP 401)
public class NaoAutenticadoException extends RuntimeException {

    public NaoAutenticadoException(String msg) {
        super(msg);
    }
}
