package br.com.soulprime.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import br.com.soulprime.model.enums.StatusMissaoUsuario;

/**
 * Progresso de um participante em um desafio tematico (TB_USUARIO_MISSAO).
 */
public class UsuarioMissao {

    private int id;
    private Usuario usuario;
    private Missao missao;
    private StatusMissaoUsuario status;
    private int qtAcoesRealizadas;
    private LocalDateTime dataInicio;
    private LocalDateTime dataConclusao;

    public UsuarioMissao() {
    }

    public UsuarioMissao(Usuario usuario, Missao missao, LocalDateTime inicio) {
        this.usuario = usuario;
        this.missao = missao;
        this.dataInicio = inicio;
        this.status = StatusMissaoUsuario.EM_ANDAMENTO;
    }

    /** Percentual concluido do desafio (0 a 100). */
    public int getPercentual() {
        int exigidas = missao.getQtAcoesExigidas();
        return exigidas == 0 ? 0 : Math.min(100, Math.round(100f * qtAcoesRealizadas / exigidas));
    }

    /** Conta mais uma acao creditada no desafio e conclui ao atingir a meta. */
    public void registrarAcao(LocalDateTime quando) {
        if (status != StatusMissaoUsuario.EM_ANDAMENTO) {
            return;
        }
        qtAcoesRealizadas++;
        if (qtAcoesRealizadas >= missao.getQtAcoesExigidas()) {
            status = StatusMissaoUsuario.CONCLUIDA;
            dataConclusao = quando;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Missao getMissao() {
        return missao;
    }

    public void setMissao(Missao missao) {
        this.missao = missao;
    }

    public StatusMissaoUsuario getStatus() {
        return status;
    }

    public void setStatus(StatusMissaoUsuario status) {
        this.status = status;
    }

    public int getQtAcoesRealizadas() {
        return qtAcoesRealizadas;
    }

    public void setQtAcoesRealizadas(int qtAcoesRealizadas) {
        this.qtAcoesRealizadas = qtAcoesRealizadas;
    }

    public LocalDateTime getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDateTime dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDateTime getDataConclusao() {
        return dataConclusao;
    }

    public void setDataConclusao(LocalDateTime dataConclusao) {
        this.dataConclusao = dataConclusao;
    }
}
