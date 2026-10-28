package br.com.soulprime.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

//Decisao do validador. confiabilidade opcional (1.0, 0.7 ou 0.4); observacao obrigatoria ao negar.
public class ValidacaoRequestDto {

    @NotNull(message = "Informe se o registro foi aprovado (true) ou negado (false).")
    private Boolean aprovado;

    private BigDecimal confiabilidade;

    @Size(max = 300, message = "A observacao deve ter no maximo 300 caracteres.")
    private String observacao;

    public Boolean getAprovado() {
        return aprovado;
    }

    public void setAprovado(Boolean aprovado) {
        this.aprovado = aprovado;
    }

    public BigDecimal getConfiabilidade() {
        return confiabilidade;
    }

    public void setConfiabilidade(BigDecimal confiabilidade) {
        this.confiabilidade = confiabilidade;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
