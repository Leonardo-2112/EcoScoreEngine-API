package br.com.soulprime.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import br.com.soulprime.model.enums.StatusCampanha;

//Indicadores agregados de uma campanha (Dashboard ESG).
public class CampanhaIndicadoresDto {

    private int campanhaId;

    private String campanha;

    private int empresaId;

    private String empresa;

    private StatusCampanha status;

    private LocalDate dataInicio;

    private LocalDate dataFim;

    private long participantes;

    private long acoesCreditadas;

    private long pontos;

    private BigDecimal mediaPontos;

    private BigDecimal co2EvitadoKg;

    public int getCampanhaId() {
        return campanhaId;
    }

    public void setCampanhaId(int campanhaId) {
        this.campanhaId = campanhaId;
    }

    public String getCampanha() {
        return campanha;
    }

    public void setCampanha(String campanha) {
        this.campanha = campanha;
    }

    public int getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(int empresaId) {
        this.empresaId = empresaId;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public StatusCampanha getStatus() {
        return status;
    }

    public void setStatus(StatusCampanha status) {
        this.status = status;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }

    public long getParticipantes() {
        return participantes;
    }

    public void setParticipantes(long participantes) {
        this.participantes = participantes;
    }

    public long getAcoesCreditadas() {
        return acoesCreditadas;
    }

    public void setAcoesCreditadas(long acoesCreditadas) {
        this.acoesCreditadas = acoesCreditadas;
    }

    public long getPontos() {
        return pontos;
    }

    public void setPontos(long pontos) {
        this.pontos = pontos;
    }

    public BigDecimal getMediaPontos() {
        return mediaPontos;
    }

    public void setMediaPontos(BigDecimal mediaPontos) {
        this.mediaPontos = mediaPontos;
    }

    public BigDecimal getCo2EvitadoKg() {
        return co2EvitadoKg;
    }

    public void setCo2EvitadoKg(BigDecimal co2EvitadoKg) {
        this.co2EvitadoKg = co2EvitadoKg;
    }
}
