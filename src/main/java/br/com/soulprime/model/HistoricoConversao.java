package br.com.soulprime.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Conversao de pontos em creditos (TB_HISTORICO_CONVERSAO): 100 pontos = 1 credito.
 */
public class HistoricoConversao {

    private int id;
    private Usuario usuario;
    private long pontosUtilizados;
    private BigDecimal creditosGerados;
    private LocalDateTime dataConversao;
    private String observacao;

    public HistoricoConversao() {
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

    public long getPontosUtilizados() {
        return pontosUtilizados;
    }

    public void setPontosUtilizados(long pontosUtilizados) {
        this.pontosUtilizados = pontosUtilizados;
    }

    public BigDecimal getCreditosGerados() {
        return creditosGerados;
    }

    public void setCreditosGerados(BigDecimal creditosGerados) {
        this.creditosGerados = creditosGerados;
    }

    public LocalDateTime getDataConversao() {
        return dataConversao;
    }

    public void setDataConversao(LocalDateTime dataConversao) {
        this.dataConversao = dataConversao;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
