package br.com.soulprime.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Carteira de creditos do participante (TB_CARTEIRA), relacao 1:1 com Usuario.
 */
public class Carteira {

    private int id;
    private Usuario usuario;
    private BigDecimal creditosSaldo = BigDecimal.ZERO;
    private BigDecimal totalSacado = BigDecimal.ZERO;
    private LocalDateTime ultimaAtualizacao;

    public Carteira() {
    }

    public Carteira(Usuario usuario) {
        this.usuario = usuario;
    }

    public void creditar(BigDecimal creditos, LocalDateTime quando) {
        this.creditosSaldo = creditosSaldo.add(creditos);
        this.ultimaAtualizacao = quando;
    }

    /** Valor dos creditos em reais: 10 creditos = R$ 1,00 (RN12). */
    public BigDecimal getSaldoEmReais() {
        return creditosSaldo.multiply(new BigDecimal("0.10")).setScale(2, java.math.RoundingMode.DOWN);
    }

    /** Debita os creditos sacados e acumula o valor recebido em reais. */
    public void registrarSaque(BigDecimal creditos, BigDecimal valorReais, LocalDateTime quando) {
        this.creditosSaldo = creditosSaldo.subtract(creditos);
        this.totalSacado = totalSacado.add(valorReais);
        this.ultimaAtualizacao = quando;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public BigDecimal getCreditosSaldo() {
        return creditosSaldo;
    }

    public void setCreditosSaldo(BigDecimal creditosSaldo) {
        this.creditosSaldo = creditosSaldo;
    }

    public BigDecimal getTotalSacado() {
        return totalSacado;
    }

    public void setTotalSacado(BigDecimal totalSacado) {
        this.totalSacado = totalSacado;
    }

    public LocalDateTime getUltimaAtualizacao() {
        return ultimaAtualizacao;
    }

    public void setUltimaAtualizacao(LocalDateTime ultimaAtualizacao) {
        this.ultimaAtualizacao = ultimaAtualizacao;
    }
}
