package br.com.soulprime.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

//Conversao de pontos em creditos.
public class ConversaoResponseDto {

    private int id;

    private int usuarioId;

    private long pontosUtilizados;

    private BigDecimal creditosGerados;

    private LocalDateTime dataConversao;

    private String observacao;

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
