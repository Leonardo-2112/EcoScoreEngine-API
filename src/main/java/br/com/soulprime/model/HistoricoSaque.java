package br.com.soulprime.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import br.com.soulprime.model.enums.StatusSaque;

/**
 * Saque de creditos em reais (TB_HISTORICO_SAQUE). Associacao como objeto: Usuario usuario.
 */
public class HistoricoSaque {

    private int id;
    private Usuario usuario;
    private BigDecimal creditosUsados;
    private BigDecimal valorReais;
    private String chavePix;
    private StatusSaque status;
    private LocalDateTime dataSolicitacao;
    private LocalDateTime dataProcessamento;
    private String observacao;

    public HistoricoSaque() {
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

    public BigDecimal getCreditosUsados() {
        return creditosUsados;
    }

    public void setCreditosUsados(BigDecimal creditosUsados) {
        this.creditosUsados = creditosUsados;
    }

    public BigDecimal getValorReais() {
        return valorReais;
    }

    public void setValorReais(BigDecimal valorReais) {
        this.valorReais = valorReais;
    }

    public String getChavePix() {
        return chavePix;
    }

    public void setChavePix(String chavePix) {
        this.chavePix = chavePix;
    }

    public StatusSaque getStatus() {
        return status;
    }

    public void setStatus(StatusSaque status) {
        this.status = status;
    }

    public LocalDateTime getDataSolicitacao() {
        return dataSolicitacao;
    }

    public void setDataSolicitacao(LocalDateTime dataSolicitacao) {
        this.dataSolicitacao = dataSolicitacao;
    }

    public LocalDateTime getDataProcessamento() {
        return dataProcessamento;
    }

    public void setDataProcessamento(LocalDateTime dataProcessamento) {
        this.dataProcessamento = dataProcessamento;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
