package br.com.soulprime.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import br.com.soulprime.model.enums.StatusValidacao;

/**
 * Registro de uma acao sustentavel realizada (TB_REGISTRO_ACAO).
 * Associacoes como objeto: Usuario usuario, Acao acao, CampanhaEsg campanha e Usuario validador.
 */
public class RegistroAcao {

    private int id;
    private Usuario usuario;
    private Acao acao;
    private CampanhaEsg campanha;
    private Usuario validador;
    private LocalDateTime dataRealizacao;
    private int repeticoes;
    private BigDecimal confiabilidade;
    private long pontosGerados;
    private String evidencia;
    private StatusValidacao validacao;
    private String observacao;
    private LocalDateTime dataValidacao;

    public RegistroAcao() {
    }

    public RegistroAcao(int id) {
        this.id = id;
    }

    /** Registros VALIDADOS ou PARCIAIS ja tiveram os pontos creditados. */
    public boolean isCreditado() {
        return validacao == StatusValidacao.VALIDADA || validacao == StatusValidacao.PARCIAL;
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

    public Acao getAcao() {
        return acao;
    }

    public void setAcao(Acao acao) {
        this.acao = acao;
    }

    public CampanhaEsg getCampanha() {
        return campanha;
    }

    public void setCampanha(CampanhaEsg campanha) {
        this.campanha = campanha;
    }

    public Usuario getValidador() {
        return validador;
    }

    public void setValidador(Usuario validador) {
        this.validador = validador;
    }

    public LocalDateTime getDataRealizacao() {
        return dataRealizacao;
    }

    public void setDataRealizacao(LocalDateTime dataRealizacao) {
        this.dataRealizacao = dataRealizacao;
    }

    public int getRepeticoes() {
        return repeticoes;
    }

    public void setRepeticoes(int repeticoes) {
        this.repeticoes = repeticoes;
    }

    public BigDecimal getConfiabilidade() {
        return confiabilidade;
    }

    public void setConfiabilidade(BigDecimal confiabilidade) {
        this.confiabilidade = confiabilidade;
    }

    public long getPontosGerados() {
        return pontosGerados;
    }

    public void setPontosGerados(long pontosGerados) {
        this.pontosGerados = pontosGerados;
    }

    public String getEvidencia() {
        return evidencia;
    }

    public void setEvidencia(String evidencia) {
        this.evidencia = evidencia;
    }

    public StatusValidacao getValidacao() {
        return validacao;
    }

    public void setValidacao(StatusValidacao validacao) {
        this.validacao = validacao;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public LocalDateTime getDataValidacao() {
        return dataValidacao;
    }

    public void setDataValidacao(LocalDateTime dataValidacao) {
        this.dataValidacao = dataValidacao;
    }
}
