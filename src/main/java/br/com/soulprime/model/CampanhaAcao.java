package br.com.soulprime.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Acao patrocinada por uma campanha (TB_CAMPANHA_ACAO). NR_BONUS_EXTRA = 1,00 (informativo).
 */
public class CampanhaAcao {

    private CampanhaEsg campanha;
    private Acao acao;
    private BigDecimal bonusExtra = BigDecimal.ONE;
    private LocalDateTime dataInclusao;

    public CampanhaAcao() {
    }

    public CampanhaAcao(CampanhaEsg campanha, Acao acao) {
        this.campanha = campanha;
        this.acao = acao;
    }

    public CampanhaEsg getCampanha() {
        return campanha;
    }

    public void setCampanha(CampanhaEsg campanha) {
        this.campanha = campanha;
    }

    public Acao getAcao() {
        return acao;
    }

    public void setAcao(Acao acao) {
        this.acao = acao;
    }

    public BigDecimal getBonusExtra() {
        return bonusExtra;
    }

    public void setBonusExtra(BigDecimal bonusExtra) {
        this.bonusExtra = bonusExtra;
    }

    public LocalDateTime getDataInclusao() {
        return dataInclusao;
    }

    public void setDataInclusao(LocalDateTime dataInclusao) {
        this.dataInclusao = dataInclusao;
    }
}
