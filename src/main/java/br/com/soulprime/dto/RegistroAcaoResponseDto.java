package br.com.soulprime.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import br.com.soulprime.model.enums.StatusValidacao;

//Registro de acao sustentavel.
public class RegistroAcaoResponseDto {

    private int id;

    private UsuarioResumoDto usuario;

    private AcaoResumoDto acao;

    private CampanhaResumoDto campanha;

    private UsuarioResumoDto validador;

    private LocalDateTime dataRealizacao;

    private int repeticoes;

    private BigDecimal confiabilidade;

    private long pontosGerados;

    private String evidencia;

    private StatusValidacao validacao;

    private String observacao;

    private LocalDateTime dataValidacao;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UsuarioResumoDto getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioResumoDto usuario) {
        this.usuario = usuario;
    }

    public AcaoResumoDto getAcao() {
        return acao;
    }

    public void setAcao(AcaoResumoDto acao) {
        this.acao = acao;
    }

    public CampanhaResumoDto getCampanha() {
        return campanha;
    }

    public void setCampanha(CampanhaResumoDto campanha) {
        this.campanha = campanha;
    }

    public UsuarioResumoDto getValidador() {
        return validador;
    }

    public void setValidador(UsuarioResumoDto validador) {
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
