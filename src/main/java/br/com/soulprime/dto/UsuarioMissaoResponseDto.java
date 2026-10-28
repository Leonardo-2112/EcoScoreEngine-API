package br.com.soulprime.dto;

import java.time.LocalDateTime;

import br.com.soulprime.model.enums.StatusMissaoUsuario;

//Progresso do participante em um desafio tematico.
public class UsuarioMissaoResponseDto {

    private int id;

    private int missaoId;

    private String missaoNome;

    private int missaoQtAcoesExigidas;

    private int qtAcoesRealizadas;

    private int percentual;

    private StatusMissaoUsuario status;

    private LocalDateTime dataInicio;

    private LocalDateTime dataConclusao;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMissaoId() {
        return missaoId;
    }

    public void setMissaoId(int missaoId) {
        this.missaoId = missaoId;
    }

    public String getMissaoNome() {
        return missaoNome;
    }

    public void setMissaoNome(String missaoNome) {
        this.missaoNome = missaoNome;
    }

    public int getMissaoQtAcoesExigidas() {
        return missaoQtAcoesExigidas;
    }

    public void setMissaoQtAcoesExigidas(int missaoQtAcoesExigidas) {
        this.missaoQtAcoesExigidas = missaoQtAcoesExigidas;
    }

    public int getQtAcoesRealizadas() {
        return qtAcoesRealizadas;
    }

    public void setQtAcoesRealizadas(int qtAcoesRealizadas) {
        this.qtAcoesRealizadas = qtAcoesRealizadas;
    }

    public int getPercentual() {
        return percentual;
    }

    public void setPercentual(int percentual) {
        this.percentual = percentual;
    }

    public StatusMissaoUsuario getStatus() {
        return status;
    }

    public void setStatus(StatusMissaoUsuario status) {
        this.status = status;
    }

    public LocalDateTime getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDateTime dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDateTime getDataConclusao() {
        return dataConclusao;
    }

    public void setDataConclusao(LocalDateTime dataConclusao) {
        this.dataConclusao = dataConclusao;
    }
}
