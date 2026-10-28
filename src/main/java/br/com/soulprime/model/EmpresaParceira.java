package br.com.soulprime.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import br.com.soulprime.model.enums.StatusEmpresa;

/**
 * Empresa patrocinadora de campanhas ESG (TB_EMPRESA_PARCEIRA).
 */
public class EmpresaParceira {

    private int id;
    private String nome;
    private String cnpj;
    private String email;
    private String segmento;
    private BigDecimal orcamentoEsg;
    private LocalDateTime dataCadastro;
    private StatusEmpresa status;

    public EmpresaParceira() {
    }

    public EmpresaParceira(int id) {
        this.id = id;
    }

    public boolean isAtiva() {
        return status == StatusEmpresa.ATIVA;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public StatusEmpresa getStatus() {
        return status;
    }

    public void setStatus(StatusEmpresa status) {
        this.status = status;
    }
}
