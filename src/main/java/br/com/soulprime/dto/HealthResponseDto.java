package br.com.soulprime.dto;

import java.time.LocalDateTime;

//Situacao da API e do banco.
public class HealthResponseDto {

    private String status;

    private String banco;

    private String versao;

    private LocalDateTime horario;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public String getVersao() {
        return versao;
    }

    public void setVersao(String versao) {
        this.versao = versao;
    }

    public LocalDateTime getHorario() {
        return horario;
    }

    public void setHorario(LocalDateTime horario) {
        this.horario = horario;
    }
}
