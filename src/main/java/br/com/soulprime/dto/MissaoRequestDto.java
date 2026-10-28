package br.com.soulprime.dto;

import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

//Cadastro/edicao de desafio tematico (perfil VALIDADOR).
public class MissaoRequestDto {

    @NotBlank(message = "Informe o nome do desafio.")
    @Size(max = 150, message = "Nome com no maximo 150 caracteres.")
    private String nome;

    @Size(max = 500, message = "Descricao com no maximo 500 caracteres.")
    private String descricao;

    @NotNull(message = "Informe a quantidade de acoes exigidas.")
    @Min(value = 1, message = "Minimo de 1 acao.")
    private Integer qtAcoesExigidas;

    @NotEmpty(message = "Informe ao menos uma acao do desafio.")
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

    public Integer getQtAcoesExigidas() {
        return qtAcoesExigidas;
    }

    public void setQtAcoesExigidas(Integer qtAcoesExigidas) {
        this.qtAcoesExigidas = qtAcoesExigidas;
    }

    public List<Integer> getAcoesIds() {
        return acoesIds;
    }

    public void setAcoesIds(List<Integer> acoesIds) {
        this.acoesIds = acoesIds;
    }
}
