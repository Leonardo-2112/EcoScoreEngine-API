package br.com.soulprime.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Metrica agregada de uma campanha em uma data (TB_METRICA_ESG). Sem dados pessoais (RN17).
 */
public class MetricaEsg {

    private int id;
    private CampanhaEsg campanha;
    private LocalDate dataReferencia;
    private long usuariosAtivos;
    private long acoesRealizadas;
    private long pontosTotais;
    private BigDecimal creditosGerados;
    private BigDecimal co2Estimado;
    private LocalDateTime dataGeracao;

    public MetricaEsg() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public CampanhaEsg getCampanha() {
        return campanha;
    }

    public void setCampanha(CampanhaEsg campanha) {
        this.campanha = campanha;
    }

    public LocalDate getDataReferencia() {
        return dataReferencia;
    }

    public void setDataReferencia(LocalDate dataReferencia) {
        this.dataReferencia = dataReferencia;
    }

    public long getUsuariosAtivos() {
        return usuariosAtivos;
    }

    public void setUsuariosAtivos(long usuariosAtivos) {
        this.usuariosAtivos = usuariosAtivos;
    }

    public long getAcoesRealizadas() {
        return acoesRealizadas;
    }

    public void setAcoesRealizadas(long acoesRealizadas) {
        this.acoesRealizadas = acoesRealizadas;
    }

    public long getPontosTotais() {
        return pontosTotais;
    }

    public void setPontosTotais(long pontosTotais) {
        this.pontosTotais = pontosTotais;
    }

    public BigDecimal getCreditosGerados() {
        return creditosGerados;
    }

    public void setCreditosGerados(BigDecimal creditosGerados) {
        this.creditosGerados = creditosGerados;
    }

    public BigDecimal getCo2Estimado() {
        return co2Estimado;
    }

    public void setCo2Estimado(BigDecimal co2Estimado) {
        this.co2Estimado = co2Estimado;
    }

    public LocalDateTime getDataGeracao() {
        return dataGeracao;
    }

    public void setDataGeracao(LocalDateTime dataGeracao) {
        this.dataGeracao = dataGeracao;
    }
}
