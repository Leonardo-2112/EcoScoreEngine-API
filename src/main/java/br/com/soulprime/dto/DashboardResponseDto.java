package br.com.soulprime.dto;

import java.math.BigDecimal;
import java.util.List;

//Dashboard ESG: somente dados agregados (RN17).
public class DashboardResponseDto {

    private int totalCampanhas;

    private long acoesCreditadas;

    private long pontos;

    private BigDecimal co2EvitadoKg;

    private List<CampanhaIndicadoresDto> campanhas;

    public int getTotalCampanhas() {
        return totalCampanhas;
    }

    public void setTotalCampanhas(int totalCampanhas) {
        this.totalCampanhas = totalCampanhas;
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

    public BigDecimal getCo2EvitadoKg() {
        return co2EvitadoKg;
    }

    public void setCo2EvitadoKg(BigDecimal co2EvitadoKg) {
        this.co2EvitadoKg = co2EvitadoKg;
    }

    public List<CampanhaIndicadoresDto> getCampanhas() {
        return campanhas;
    }

    public void setCampanhas(List<CampanhaIndicadoresDto> campanhas) {
        this.campanhas = campanhas;
    }
}
