package br.com.soulprime.dto;

//Resumo da acao usado dentro de outras respostas.
public class AcaoResumoDto {

    private int id;

    private String nome;

    private int impacto;

    private int pontosBase;

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

    public int getImpacto() {
        return impacto;
    }

    public void setImpacto(int impacto) {
        this.impacto = impacto;
    }

    public int getPontosBase() {
        return pontosBase;
    }

    public void setPontosBase(int pontosBase) {
        this.pontosBase = pontosBase;
    }
}
