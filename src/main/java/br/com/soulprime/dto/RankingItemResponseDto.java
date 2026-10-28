package br.com.soulprime.dto;

import java.math.BigDecimal;

//Linha do ranking de participantes.
public class RankingItemResponseDto {

    private int posicao;

    private int usuarioId;

    private String nome;

    private String nivel;

    private int missoesConcluidas;

    private long pontosAcumulados;

    private long acoesCreditadas;

    private BigDecimal co2EvitadoKg;

    public int getPosicao() {
        return posicao;
    }

    public void setPosicao(int posicao) {
        this.posicao = posicao;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public int getMissoesConcluidas() {
        return missoesConcluidas;
    }

    public void setMissoesConcluidas(int missoesConcluidas) {
        this.missoesConcluidas = missoesConcluidas;
    }

    public long getPontosAcumulados() {
        return pontosAcumulados;
    }

    public void setPontosAcumulados(long pontosAcumulados) {
        this.pontosAcumulados = pontosAcumulados;
    }

    public long getAcoesCreditadas() {
        return acoesCreditadas;
    }

    public void setAcoesCreditadas(long acoesCreditadas) {
        this.acoesCreditadas = acoesCreditadas;
    }

    public BigDecimal getCo2EvitadoKg() {
        return co2EvitadoKg;
    }

    public void setCo2EvitadoKg(BigDecimal co2EvitadoKg) {
        this.co2EvitadoKg = co2EvitadoKg;
    }
}
