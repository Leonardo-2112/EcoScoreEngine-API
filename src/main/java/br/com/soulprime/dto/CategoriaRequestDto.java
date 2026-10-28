package br.com.soulprime.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

//Cadastro/edicao de categoria (perfil VALIDADOR).
public class CategoriaRequestDto {

    @NotBlank(message = "Informe o nome da categoria.")
    @Size(max = 50, message = "Nome com no maximo 50 caracteres.")
    private String nome;

    @Size(max = 200, message = "Descricao com no maximo 200 caracteres.")
    private String descricao;

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
}
