package br.com.soulprime.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import br.com.soulprime.model.enums.StatusCampanha;

//Campanha ESG com as acoes patrocinadas.
public class CampanhaResponseDto {

    private int id;

    private EmpresaResumoDto empresa;

    private String nome;

    private String descricao;

    private BigDecimal orcamento;

    private BigDecimal orcamentoGasto;

    private LocalDate dataInicio;

    private LocalDate dataFim;

    private StatusCampanha status;

    private LocalDateTime dataCriacao;

    private List<AcaoResumoDto> acoes;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public EmpresaResumoDto getEmpresa() {
        return empresa;
    }

    public void setEmpresa(EmpresaResumoDto empresa) {
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

    public List<AcaoResumoDto> getAcoes() {
        return acoes;
    }

    public void setAcoes(List<AcaoResumoDto> acoes) {
        this.acoes = acoes;
    }
}
