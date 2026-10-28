package br.com.soulprime.dto;

import java.math.BigDecimal;

//Nivel de progressao.
public class NivelResponseDto {

    private int id;

    private String descricao;

    private int missoesMinimas;

    private Integer missoesMaximas;

    private BigDecimal bonusPontos;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getMissoesMinimas() {
        return missoesMinimas;
    }

    public void setMissoesMinimas(int missoesMinimas) {
        this.missoesMinimas = missoesMinimas;
    }

    public Integer getMissoesMaximas() {
        return missoesMaximas;
    }

    public void setMissoesMaximas(Integer missoesMaximas) {
        this.missoesMaximas = missoesMaximas;
    }

    public BigDecimal getBonusPontos() {
        return bonusPontos;
    }

    public void setBonusPontos(BigDecimal bonusPontos) {
        this.bonusPontos = bonusPontos;
    }
}
