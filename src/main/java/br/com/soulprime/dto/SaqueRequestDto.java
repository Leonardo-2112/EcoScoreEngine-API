package br.com.soulprime.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

//Saque de no minimo 10 creditos (RN13). Sem chave PIX, usa a do cadastro.
public class SaqueRequestDto {

    @NotNull(message = "Informe a quantidade de creditos.")
    @Min(value = 10, message = "O saque minimo e de 10 creditos.")
    private Integer creditos;

    @Size(max = 150, message = "A chave PIX deve ter no maximo 150 caracteres.")
    private String chavePix;

    public Integer getCreditos() {
        return creditos;
    }

    public void setCreditos(Integer creditos) {
        this.creditos = creditos;
    }

    public String getChavePix() {
        return chavePix;
    }

    public void setChavePix(String chavePix) {
        this.chavePix = chavePix;
    }
}
