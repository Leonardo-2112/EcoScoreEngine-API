package br.com.soulprime.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

//Metrica agregada de campanha (sem dados pessoais - RN17).
public class MetricaResponseDto {

    private int id;

    private int campanhaId;

    private LocalDate dataReferencia;

    private long usuariosAtivos;

    private long acoesRealizadas;

    private long pontosTotais;

    private BigDecimal creditosGerados;

    private BigDecimal co2Estimado;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCampanhaId() {
        return campanhaId;
    }

    public void setCampanhaId(int campanhaId) {
        this.campanhaId = campanhaId;
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
}
