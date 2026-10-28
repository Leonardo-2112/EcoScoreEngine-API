package br.com.soulprime.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

//Campanha ESG criada pela empresa (nasce PENDENTE ate aprovacao - RN16).
public class CampanhaRequestDto {

    @NotBlank(message = "Informe o nome da campanha.")
    @Size(max = 150, message = "Nome com no maximo 150 caracteres.")
    private String nome;

    @Size(max = 500, message = "Descricao com no maximo 500 caracteres.")
    private String descricao;

    @NotNull(message = "Informe o orcamento.")
    @DecimalMin(value = "0", inclusive = false, message = "Orcamento deve ser maior que zero.")
    private BigDecimal orcamento;

    @NotNull(message = "Informe a data de inicio.")
    private LocalDate dataInicio;

    @NotNull(message = "Informe a data de fim.")
    private LocalDate dataFim;

    @NotEmpty(message = "Informe ao menos uma acao patrocinada.")
    private List<Integer> acoesIds;

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

    public List<Integer> getAcoesIds() {
        return acoesIds;
    }

    public void setAcoesIds(List<Integer> acoesIds) {
        this.acoesIds = acoesIds;
    }
}
