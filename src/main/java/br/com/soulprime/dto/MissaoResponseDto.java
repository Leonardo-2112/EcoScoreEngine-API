package br.com.soulprime.dto;

import java.util.List;

import br.com.soulprime.model.enums.StatusAtivacao;

//Desafio tematico com as acoes que o compoem.
public class MissaoResponseDto {

    private int id;

    private String nome;

    private String descricao;

    private int qtAcoesExigidas;

    private StatusAtivacao status;

    private List<AcaoResumoDto> acoes;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getQtAcoesExigidas() {
        return qtAcoesExigidas;
    }

    public void setQtAcoesExigidas(int qtAcoesExigidas) {
        this.qtAcoesExigidas = qtAcoesExigidas;
    }

    public StatusAtivacao getStatus() {
        return status;
    }

    public void setStatus(StatusAtivacao status) {
        this.status = status;
    }

    public List<AcaoResumoDto> getAcoes() {
        return acoes;
    }

    public void setAcoes(List<AcaoResumoDto> acoes) {
        this.acoes = acoes;
    }
}
