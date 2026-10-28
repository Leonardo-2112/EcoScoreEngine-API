package br.com.soulprime.model;

/**
 * Acao que compoe um desafio tematico (TB_MISSAO_ACAO).
 */
public class MissaoAcao {

    private Missao missao;
    private Acao acao;
    private int ordem;

    public MissaoAcao() {
    }

    public MissaoAcao(Missao missao, Acao acao, int ordem) {
        this.missao = missao;
        this.acao = acao;
        this.ordem = ordem;
    }

    public Missao getMissao() {
        return missao;
    }

    public void setMissao(Missao missao) {
        this.missao = missao;
    }

    public Acao getAcao() {
        return acao;
    }

    public void setAcao(Acao acao) {
        this.acao = acao;
    }

    public int getOrdem() {
        return ordem;
    }

    public void setOrdem(int ordem) {
        this.ordem = ordem;
    }
}
