package br.com.soulprime.exception;

//Violacao de uma regra de negocio, ex.: limite diario, saldo insuficiente (HTTP 422)
public class RegraNegocioException extends RuntimeException {

    public RegraNegocioException(String msg) {
        super(msg);
    }
}
