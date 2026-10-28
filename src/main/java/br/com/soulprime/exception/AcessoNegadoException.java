package br.com.soulprime.exception;

//O perfil do usuario nao tem permissao para a operacao (HTTP 403)
public class AcessoNegadoException extends RuntimeException {

    public AcessoNegadoException(String msg) {
        super(msg);
    }
}
