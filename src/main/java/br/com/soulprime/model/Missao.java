package br.com.soulprime.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import br.com.soulprime.model.enums.StatusAtivacao;

/**
 * Desafio tematico opcional (TB_MISSAO). Nao altera o nivel (nivel = 3 acoes creditadas por missao).
 */
public class Missao {

    private int id;
    private String nome;
    private String descricao;
    private int qtAcoesExigidas;
    private StatusAtivacao status;
    private LocalDateTime dataCriacao;
    private List<MissaoAcao> acoes = new ArrayList<>();

    public Missao() {
    }

    public Missao(int id) {
        this.id = id;
    }

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

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public List<MissaoAcao> getAcoes() {
        return acoes;
    }

    public void setAcoes(List<MissaoAcao> acoes) {
        this.acoes = acoes;
    }
}
