package br.com.soulprime.dto;

import java.math.BigDecimal;

import br.com.soulprime.model.enums.StatusAtivacao;

//Acao do catalogo.
public class AcaoResponseDto {

    private int id;

    private String nome;

    private String descricao;

    private CategoriaResponseDto categoria;

    private int impacto;

    private int pontosBase;

    private int limiteDiario;

    private boolean repetivel;

    private BigDecimal co2PorUnidade;

    private StatusAtivacao status;

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

    public CategoriaResponseDto getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaResponseDto categoria) {
        this.categoria = categoria;
    }

    public int getImpacto() {
        return impacto;
    }

    public void setImpacto(int impacto) {
        this.impacto = impacto;
    }

    public int getPontosBase() {
        return pontosBase;
    }

    public void setPontosBase(int pontosBase) {
        this.pontosBase = pontosBase;
    }

    public int getLimiteDiario() {
        return limiteDiario;
    }

    public void setLimiteDiario(int limiteDiario) {
        this.limiteDiario = limiteDiario;
    }

    public boolean isRepetivel() {
        return repetivel;
    }

    public void setRepetivel(boolean repetivel) {
        this.repetivel = repetivel;
    }

    public BigDecimal getCo2PorUnidade() {
        return co2PorUnidade;
    }

    public void setCo2PorUnidade(BigDecimal co2PorUnidade) {
        this.co2PorUnidade = co2PorUnidade;
    }

    public StatusAtivacao getStatus() {
        return status;
    }

    public void setStatus(StatusAtivacao status) {
        this.status = status;
    }
}
