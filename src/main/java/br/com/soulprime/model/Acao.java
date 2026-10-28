package br.com.soulprime.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import br.com.soulprime.model.enums.StatusAtivacao;

/**
 * Acao sustentavel do catalogo (TB_ACAO). Associacao como objeto: CategoriaAcao categoria.
 */
public class Acao {

    private int id;
    private CategoriaAcao categoria;
    private String nome;
    private String descricao;
    private int impacto;
    private int limiteDiario;
    private boolean repetivel;
    private BigDecimal co2PorUnidade;
    private LocalDateTime dataCriacao;
    private StatusAtivacao status;

    public Acao() {
    }

    public Acao(int id) {
        this.id = id;
    }

    /** Pontos base da acao = impacto x 10. */
    public int getPontosBase() {
        return impacto * 10;
    }

    public boolean isAtiva() {
        return status == StatusAtivacao.ATIVA;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public CategoriaAcao getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaAcao categoria) {
        this.categoria = categoria;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getImpacto() {
        return impacto;
    }

    public void setImpacto(int impacto) {
        this.impacto = impacto;
    }

    public int getLimiteDiario() {
        return limiteDiario;
    }

    public void setLimiteDiario(int limiteDiario) {
        this.limiteDiario = limiteDiario;
    }

    public boolean isRepetivel() {
        return repetivel;
    }

    public void setRepetivel(boolean repetivel) {
        this.repetivel = repetivel;
    }

    public BigDecimal getCo2PorUnidade() {
        return co2PorUnidade;
    }

    public void setCo2PorUnidade(BigDecimal co2PorUnidade) {
        this.co2PorUnidade = co2PorUnidade;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public StatusAtivacao getStatus() {
        return status;
    }

    public void setStatus(StatusAtivacao status) {
        this.status = status;
    }
}
