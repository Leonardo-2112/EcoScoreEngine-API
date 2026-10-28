package br.com.soulprime.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import br.com.soulprime.model.enums.StatusCampanha;

/**
 * Campanha ESG patrocinada por uma empresa (TB_CAMPANHA_ESG).
 * Associacoes como objeto: EmpresaParceira empresa e a lista de acoes patrocinadas.
 */
public class CampanhaEsg {

    private int id;
    private EmpresaParceira empresa;
    private String nome;
    private String descricao;
    private BigDecimal orcamento;
    private BigDecimal orcamentoGasto = BigDecimal.ZERO;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private BigDecimal bonusCreditos = BigDecimal.ONE;
    private StatusCampanha status;
    private LocalDateTime dataCriacao;
    private List<CampanhaAcao> acoes = new ArrayList<>();

    public CampanhaEsg() {
    }

    public CampanhaEsg(int id) {
        this.id = id;
    }

    /** A campanha cobre a data informada? */
    public boolean vigenteEm(LocalDate data) {
        return !data.isBefore(dataInicio) && !data.isAfter(dataFim);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public EmpresaParceira getEmpresa() {
        return empresa;
    }

    public void setEmpresa(EmpresaParceira empresa) {
        this.empresa = empresa;
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

    public BigDecimal getOrcamento() {
        return orcamento;
    }

    public void setOrcamento(BigDecimal orcamento) {
        this.orcamento = orcamento;
    }

    public BigDecimal getOrcamentoGasto() {
        return orcamentoGasto;
    }

    public void setOrcamentoGasto(BigDecimal orcamentoGasto) {
        this.orcamentoGasto = orcamentoGasto;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }

    public BigDecimal getBonusCreditos() {
        return bonusCreditos;
    }

    public void setBonusCreditos(BigDecimal bonusCreditos) {
        this.bonusCreditos = bonusCreditos;
    }

    public StatusCampanha getStatus() {
        return status;
    }

    public void setStatus(StatusCampanha status) {
        this.status = status;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public List<CampanhaAcao> getAcoes() {
        return acoes;
    }

    public void setAcoes(List<CampanhaAcao> acoes) {
        this.acoes = acoes;
    }
}
