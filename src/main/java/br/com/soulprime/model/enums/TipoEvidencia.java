package br.com.soulprime.model.enums;

import java.math.BigDecimal;

/**
 * Evidencia declarada pelo participante ao registrar uma acao e o fator de
 * confiabilidade correspondente (slides da mentoria / RN06).
 */
public enum TipoEvidencia {
    COMPROVADA(new BigDecimal("1.0")),
    PARCIAL(new BigDecimal("0.7")),
    SEM_COMPROVACAO(new BigDecimal("0.4"));

    private final BigDecimal confiabilidade;

    TipoEvidencia(BigDecimal confiabilidade) {
        this.confiabilidade = confiabilidade;
    }

    public BigDecimal getConfiabilidade() {
        return confiabilidade;
    }
}
