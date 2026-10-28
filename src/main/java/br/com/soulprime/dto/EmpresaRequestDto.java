package br.com.soulprime.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

//Dados da empresa parceira. O digito verificador do CNPJ e conferido no BO.
public class EmpresaRequestDto {

    @NotBlank(message = "Informe a razao social.")
    @Size(max = 150, message = "Nome com no maximo 150 caracteres.")
    private String nome;

    @NotBlank(message = "Informe o CNPJ.")
    @Pattern(regexp = "^\\d{2}\\.?\\d{3}\\.?\\d{3}/?\\d{4}-?\\d{2}$", message = "CNPJ deve ter 14 digitos.")
    private String cnpj;

    @NotBlank(message = "Informe o e-mail corporativo.")
    @Email(message = "E-mail invalido.")
    @Size(max = 150)
    private String email;

    @Size(max = 100, message = "Segmento com no maximo 100 caracteres.")
    private String segmento;

    @DecimalMin(value = "0", message = "Orcamento nao pode ser negativo.")
    private BigDecimal orcamentoEsg;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSegmento() {
        return segmento;
    }

    public void setSegmento(String segmento) {
        this.segmento = segmento;
    }

    public BigDecimal getOrcamentoEsg() {
        return orcamentoEsg;
    }

    public void setOrcamentoEsg(BigDecimal orcamentoEsg) {
        this.orcamentoEsg = orcamentoEsg;
    }
}
