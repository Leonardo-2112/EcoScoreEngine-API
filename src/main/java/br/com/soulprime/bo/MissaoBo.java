package br.com.soulprime.bo;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.soulprime.dao.AcaoDao;
import br.com.soulprime.dao.MissaoDao;
import br.com.soulprime.dto.MissaoRequestDto;
import br.com.soulprime.exception.ConflitoException;
import br.com.soulprime.exception.EntidadeNaoEncontradaException;
import br.com.soulprime.exception.RegraNegocioException;
import br.com.soulprime.factory.ConnectionFactory;
import br.com.soulprime.model.Missao;
import br.com.soulprime.model.MissaoAcao;
import br.com.soulprime.model.enums.Perfil;
import br.com.soulprime.model.enums.StatusAtivacao;

//Desafios tematicos (opcionais, nao alteram o nivel): leitura publica, escrita pelo perfil VALIDADOR
@Service
public class MissaoBo {

    private final MissaoDao missaoDao;
    private final AcaoDao acaoDao;
    private final AcessoBo acessoBo;
    private final ConnectionFactory connectionFactory;

    public MissaoBo(MissaoDao missaoDao, AcaoDao acaoDao, AcessoBo acessoBo, ConnectionFactory connectionFactory) {
        this.missaoDao = missaoDao;
        this.acaoDao = acaoDao;
        this.acessoBo = acessoBo;
        this.connectionFactory = connectionFactory;
    }

    public List<Missao> listar(String status) throws SQLException {
        StatusAtivacao filtro = StatusAtivacao.ATIVA;
        if (status != null) {
            filtro = status.equalsIgnoreCase("TODAS") ? null : ValidadorDados.converterEnum(StatusAtivacao.class, status);
        }
        return missaoDao.listar(filtro);
    }

    public Missao buscar(int id) throws SQLException, EntidadeNaoEncontradaException {
        return missaoDao.buscarPorId(id);
    }

    //Grava o desafio e as acoes que o compoem na mesma transacao
    public Missao cadastrar(Integer usuarioLogado, MissaoRequestDto dto) throws SQLException, EntidadeNaoEncontradaException {
        acessoBo.exigirPerfil(usuarioLogado, Perfil.VALIDADOR);
        if (missaoDao.existeNome(dto.getNome(), 0)) {
            throw new ConflitoException("Ja existe um desafio com esse nome.");
        }
        Missao missao = new Missao();
        preencher(missao, dto);
        missao.setStatus(StatusAtivacao.ATIVA);
        missao.setDataCriacao(LocalDateTime.now().withNano(0));
        Connection conexao = connectionFactory.getConnection();
        try {
            conexao.setAutoCommit(false);
            missaoDao.cadastrar(conexao, missao);
            conexao.commit();
            return missao;
        } catch (Exception e) {
            conexao.rollback();
            throw e;
        } finally {
            conexao.close();
        }
    }

    public Missao atualizar(Integer usuarioLogado, int id, MissaoRequestDto dto) throws SQLException, EntidadeNaoEncontradaException {
        acessoBo.exigirPerfil(usuarioLogado, Perfil.VALIDADOR);
        Missao missao = missaoDao.buscarPorId(id);
        if (missaoDao.existeNome(dto.getNome(), id)) {
            throw new ConflitoException("Ja existe um desafio com esse nome.");
        }
        missao.getAcoes().clear();
        preencher(missao, dto);
        Connection conexao = connectionFactory.getConnection();
        try {
            conexao.setAutoCommit(false);
            missaoDao.atualizar(conexao, missao);
            conexao.commit();
            return missao;
        } catch (Exception e) {
            conexao.rollback();
            throw e;
        } finally {
            conexao.close();
        }
    }

    //Exclusao logica (INATIVA): preserva o progresso dos participantes
    public void inativar(Integer usuarioLogado, int id) throws SQLException, EntidadeNaoEncontradaException {
        acessoBo.exigirPerfil(usuarioLogado, Perfil.VALIDADOR);
        Missao missao = missaoDao.buscarPorId(id);
        if (missao.getStatus() == StatusAtivacao.INATIVA) {
            throw new RegraNegocioException("Desafio ja esta inativo.");
        }
        missaoDao.atualizarStatus(id, StatusAtivacao.INATIVA);
    }

    private void preencher(Missao missao, MissaoRequestDto dto) throws SQLException, EntidadeNaoEncontradaException {
        missao.setNome(ValidadorDados.texto(dto.getNome()));
        missao.setDescricao(ValidadorDados.texto(dto.getDescricao()));
        missao.setQtAcoesExigidas(dto.getQtAcoesExigidas());
        int ordem = 1;
        for (Integer acaoId : new LinkedHashSet<>(dto.getAcoesIds())) {
            missao.getAcoes().add(new MissaoAcao(missao, acaoDao.buscarPorId(acaoId), ordem));
            ordem++;
        }
    }
}
