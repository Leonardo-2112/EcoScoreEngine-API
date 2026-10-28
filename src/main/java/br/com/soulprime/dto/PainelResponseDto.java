package br.com.soulprime.dto;

import java.math.BigDecimal;

//Painel do participante (nivel, missao em andamento, carteira, CO2 e ranking).
public class PainelResponseDto {

    private UsuarioResumoDto usuario;

    private NivelResponseDto nivel;

    private NivelResponseDto proximoNivel;

    private int missoesConcluidas;

    private Integer missoesParaProximoNivel;

    private int acoesNaMissaoAtual;

    private int acoesPorMissao;

    private long pontosTotal;

    private long pontosSaldo;

    private BigDecimal creditos;

    private BigDecimal saldoEmReais;

    private BigDecimal co2EvitadoKg;

    private long registrosCreditados;

    private long registrosPendentes;

    private Integer posicaoRanking;

    public UsuarioResumoDto getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioResumoDto usuario) {
        this.usuario = usuario;
    }

    public NivelResponseDto getNivel() {
        return nivel;
    }

    public void setNivel(NivelResponseDto nivel) {
        this.nivel = nivel;
    }

    public NivelResponseDto getProximoNivel() {
        return proximoNivel;
    }

    public void setProximoNivel(NivelResponseDto proximoNivel) {
        this.proximoNivel = proximoNivel;
    }

    public int getMissoesConcluidas() {
        return missoesConcluidas;
    }

    public void setMissoesConcluidas(int missoesConcluidas) {
        this.missoesConcluidas = missoesConcluidas;
    }

    public Integer getMissoesParaProximoNivel() {
        return missoesParaProximoNivel;
    }

    public void setMissoesParaProximoNivel(Integer missoesParaProximoNivel) {
        this.missoesParaProximoNivel = missoesParaProximoNivel;
    }

    public int getAcoesNaMissaoAtual() {
        return acoesNaMissaoAtual;
    }

    public void setAcoesNaMissaoAtual(int acoesNaMissaoAtual) {
        this.acoesNaMissaoAtual = acoesNaMissaoAtual;
    }

    public int getAcoesPorMissao() {
        return acoesPorMissao;
    }

    public void setAcoesPorMissao(int acoesPorMissao) {
        this.acoesPorMissao = acoesPorMissao;
    }

    public long getPontosTotal() {
        return pontosTotal;
    }

    public void setPontosTotal(long pontosTotal) {
        this.pontosTotal = pontosTotal;
    }

    public long getPontosSaldo() {
        return pontosSaldo;
    }

    public void setPontosSaldo(long pontosSaldo) {
        this.pontosSaldo = pontosSaldo;
    }

    public BigDecimal getCreditos() {
        return creditos;
    }

    public void setCreditos(BigDecimal creditos) {
        this.creditos = creditos;
    }

    public BigDecimal getSaldoEmReais() {
        return saldoEmReais;
    }

    public void setSaldoEmReais(BigDecimal saldoEmReais) {
        this.saldoEmReais = saldoEmReais;
    }

    public BigDecimal getCo2EvitadoKg() {
        return co2EvitadoKg;
    }

    public void setCo2EvitadoKg(BigDecimal co2EvitadoKg) {
        this.co2EvitadoKg = co2EvitadoKg;
    }

    public long getRegistrosCreditados() {
        return registrosCreditados;
    }

    public void setRegistrosCreditados(long registrosCreditados) {
        this.registrosCreditados = registrosCreditados;
    }

    public long getRegistrosPendentes() {
        return registrosPendentes;
    }

    public void setRegistrosPendentes(long registrosPendentes) {
        this.registrosPendentes = registrosPendentes;
    }

    public Integer getPosicaoRanking() {
        return posicaoRanking;
    }

    public void setPosicaoRanking(Integer posicaoRanking) {
        this.posicaoRanking = posicaoRanking;
    }
}
