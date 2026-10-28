package br.com.soulprime.model;

/**
 * Categoria das acoes sustentaveis (TB_CATEGORIA_ACAO).
 */
public class CategoriaAcao {

    private int id;
    private String nome;
    private String descricao;

    public CategoriaAcao() {
    }

    public CategoriaAcao(int id) {
        this.id = id;
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

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
