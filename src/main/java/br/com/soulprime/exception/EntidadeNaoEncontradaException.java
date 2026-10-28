package br.com.soulprime.exception;

//Lancada quando o registro procurado nao existe no banco (HTTP 404)
public class EntidadeNaoEncontradaException extends Exception {

    public EntidadeNaoEncontradaException(String msg) {
        super(msg);
    }
}
