package br.com.soulprime.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

//Cadastro/edicao de acao do catalogo (perfil VALIDADOR).
public class AcaoRequestDto {

    @NotNull(message = "Informe a categoria.")
    private Integer categoriaId;

    @NotBlank(message = "Informe o nome da acao.")
    @Size(max = 100, message = "Nome com no maximo 100 caracteres.")
    private String nome;

    @Size(max = 500, message = "Descricao com no maximo 500 caracteres.")
    private String descricao;

    @NotNull(message = "Informe o impacto.")
    @Min(value = 1, message = "Impacto minimo 1.")
    @Max(value = 10, message = "Impacto maximo 10.")
    private Integer impacto;

    @Min(value = 1, message = "Limite diario minimo 1.")
    private Integer limiteDiario;

    private Boolean repetivel;

    @NotNull(message = "Informe o CO2 evitado por unidade.")
    @DecimalMin(value = "0", message = "CO2 nao pode ser negativo.")
    private BigDecimal co2PorUnidade;

    public Integer getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Integer categoriaId) {
        this.categoriaId = categoriaId;
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

    public Integer getImpacto() {
        return impacto;
    }

    public void setImpacto(Integer impacto) {
        this.impacto = impacto;
    }

    public Integer getLimiteDiario() {
        return limiteDiario;
    }

    public void setLimiteDiario(Integer limiteDiario) {
        this.limiteDiario = limiteDiario;
    }

    public Boolean getRepetivel() {
        return repetivel;
    }

    public void setRepetivel(Boolean repetivel) {
        this.repetivel = repetivel;
    }

    public BigDecimal getCo2PorUnidade() {
        return co2PorUnidade;
    }

    public void setCo2PorUnidade(BigDecimal co2PorUnidade) {
        this.co2PorUnidade = co2PorUnidade;
    }
}
