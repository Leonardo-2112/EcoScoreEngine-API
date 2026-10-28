package br.com.soulprime.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

//Carteira do participante (saldoEmReais = creditos x R$ 0,10).
public class CarteiraResponseDto {

    private int id;

    private int usuarioId;

    private BigDecimal creditosSaldo;

    private BigDecimal saldoEmReais;

    private BigDecimal totalSacado;

    private LocalDateTime ultimaAtualizacao;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public BigDecimal getCreditosSaldo() {
        return creditosSaldo;
    }

    public void setCreditosSaldo(BigDecimal creditosSaldo) {
        this.creditosSaldo = creditosSaldo;
    }

    public BigDecimal getSaldoEmReais() {
        return saldoEmReais;
    }

    public void setSaldoEmReais(BigDecimal saldoEmReais) {
        this.saldoEmReais = saldoEmReais;
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
