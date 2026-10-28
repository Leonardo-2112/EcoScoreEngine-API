package br.com.soulprime.dto;

import jakarta.validation.constraints.NotBlank;

//Novo status de empresa ou campanha (perfil VALIDADOR).
public class StatusRequestDto {

    @NotBlank(message = "Informe o novo status.")
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
