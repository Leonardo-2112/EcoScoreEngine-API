package br.com.soulprime.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import br.com.soulprime.exception.EntidadeNaoEncontradaException;
import br.com.soulprime.factory.ConnectionFactory;
import br.com.soulprime.model.Missao;
import br.com.soulprime.model.MissaoAcao;
import br.com.soulprime.model.enums.StatusAtivacao;

//DAO das tabelas TB_MISSAO (desafios tematicos) e TB_MISSAO_ACAO
@Repository
public class MissaoDao {

    private static final String SELECT_SQL = "SELECT * FROM TB_MISSAO ";
    private static final String INSERT_SQL = "INSERT INTO TB_MISSAO (NM_MISSAO, DS_MISSAO, QT_ACOES_EXIGIDAS, DS_STATUS, DT_CRIACAO) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE TB_MISSAO SET NM_MISSAO = ?, DS_MISSAO = ?, QT_ACOES_EXIGIDAS = ? WHERE ID_MISSAO = ?";
    private static final String UPDATE_STATUS_SQL = "UPDATE TB_MISSAO SET DS_STATUS = ? WHERE ID_MISSAO = ?";
    private static final String INSERT_ACAO_SQL = "INSERT INTO TB_MISSAO_ACAO (ID_MISSAO, ID_ACAO, NR_ORDEM) VALUES (?, ?, ?)";
    private static final String DELETE_ACOES_SQL = "DELETE FROM TB_MISSAO_ACAO WHERE ID_MISSAO = ?";
    private static final String SELECT_ACOES_SQL = AcaoDao.SELECT_SQL.replace("SELECT a.*", "SELECT a.*, ma.NR_ORDEM")
            + "JOIN TB_MISSAO_ACAO ma ON ma.ID_ACAO = a.ID_ACAO WHERE ma.ID_MISSAO = ? ORDER BY ma.NR_ORDEM";
    private static final String SELECT_ATIVAS_POR_ACAO = "SELECT m.* FROM TB_MISSAO m JOIN TB_MISSAO_ACAO ma ON ma.ID_MISSAO = m.ID_MISSAO "
            + "WHERE ma.ID_ACAO = ? AND m.DS_STATUS = 'ATIVA'";
    private static final String EXISTE_NOME = "SELECT COUNT(*) FROM TB_MISSAO WHERE UPPER(NM_MISSAO) = UPPER(?) AND ID_MISSAO <> ?";

    private final ConnectionFactory connectionFactory;

    public MissaoDao(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    static Missao getMissao(ResultSet resultSet) throws SQLException {
        Missao missao = new Missao(resultSet.getInt("ID_MISSAO"));
        missao.setNome(resultSet.getString("NM_MISSAO"));
        missao.setDescricao(resultSet.getString("DS_MISSAO"));
        missao.setQtAcoesExigidas(resultSet.getInt("QT_ACOES_EXIGIDAS"));
        missao.setStatus(StatusAtivacao.valueOf(resultSet.getString("DS_STATUS")));
        missao.setDataCriacao(resultSet.getObject("DT_CRIACAO", LocalDateTime.class));
        return missao;
    }

    public void cadastrar(Connection conexao, Missao missao) throws SQLException {
        try (PreparedStatement stmt = conexao.prepareStatement(INSERT_SQL, new String[] {"ID_MISSAO"})) {
            stmt.setString(1, missao.getNome());
            stmt.setString(2, missao.getDescricao());
            stmt.setInt(3, missao.getQtAcoesExigidas());
            stmt.setString(4, missao.getStatus().name());
            stmt.setObject(5, missao.getDataCriacao());
            stmt.executeUpdate();
            ResultSet resultSet = stmt.getGeneratedKeys();
            if (resultSet.next()) {
                missao.setId(resultSet.getInt(1));
            }
        }
        cadastrarAcoes(conexao, missao);
    }

    public void atualizar(Connection conexao, Missao missao) throws SQLException {
        try (PreparedStatement stmt = conexao.prepareStatement(UPDATE_SQL)) {
            stmt.setString(1, missao.getNome());
            stmt.setString(2, missao.getDescricao());
            stmt.setInt(3, missao.getQtAcoesExigidas());
            stmt.setInt(4, missao.getId());
            stmt.executeUpdate();
        }
        try (PreparedStatement stmt = conexao.prepareStatement(DELETE_ACOES_SQL)) {
            stmt.setInt(1, missao.getId());
            stmt.executeUpdate();
        }
        cadastrarAcoes(conexao, missao);
    }

    public void atualizarStatus(int id, StatusAtivacao status) throws SQLException, EntidadeNaoEncontradaException {
        try (Connection conexao = connectionFactory.getConnection(); PreparedStatement stmt = conexao.prepareStatement(UPDATE_STATUS_SQL)) {
            stmt.setString(1, status.name());
            stmt.setInt(2, id);
            if (stmt.executeUpdate() == 0) {
                throw new EntidadeNaoEncontradaException("Desafio nao encontrado para atualizacao!");
            }
        }
    }

    public Missao buscarPorId(int id) throws SQLException, EntidadeNaoEncontradaException {
        try (Connection conexao = connectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(SELECT_SQL + "WHERE ID_MISSAO = ?")) {
            stmt.setInt(1, id);
            ResultSet resultSet = stmt.executeQuery();
            if (!resultSet.next()) {
                throw new EntidadeNaoEncontradaException("Desafio " + id + " nao encontrado.");
            }
            Missao missao = getMissao(resultSet);
            carregarAcoes(conexao, missao);
            return missao;
        }
    }

    public List<Missao> listar(StatusAtivacao status) throws SQLException {
        String sql = SELECT_SQL + "WHERE (? IS NULL OR DS_STATUS = ?) ORDER BY QT_ACOES_EXIGIDAS, NM_MISSAO";
        try (Connection conexao = connectionFactory.getConnection(); PreparedStatement stmt = conexao.prepareStatement(sql)) {
            String s = status == null ? null : status.name();
            stmt.setString(1, s);
            stmt.setString(2, s);
            ResultSet resultSet = stmt.executeQuery();
            List<Missao> lista = new ArrayList<>();
            while (resultSet.next()) {
                lista.add(getMissao(resultSet));
            }
            for (Missao missao : lista) {
                carregarAcoes(conexao, missao);
            }
            return lista;
        }
    }

    //Desafios ATIVOS que contem a acao (para avancar o progresso ao creditar um registro)
    public List<Missao> listarAtivasPorAcao(Connection conexao, int acaoId) throws SQLException {
        try (PreparedStatement stmt = conexao.prepareStatement(SELECT_ATIVAS_POR_ACAO)) {
            stmt.setInt(1, acaoId);
            ResultSet resultSet = stmt.executeQuery();
            List<Missao> lista = new ArrayList<>();
            while (resultSet.next()) {
                lista.add(getMissao(resultSet));
            }
            return lista;
        }
    }

    public boolean existeNome(String nome, int idIgnorado) throws SQLException {
        try (Connection conexao = connectionFactory.getConnection(); PreparedStatement stmt = conexao.prepareStatement(EXISTE_NOME)) {
            stmt.setString(1, nome.trim());
            stmt.setInt(2, idIgnorado);
            ResultSet resultSet = stmt.executeQuery();
            return resultSet.next() && resultSet.getInt(1) > 0;
        }
    }

    private void cadastrarAcoes(Connection conexao, Missao missao) throws SQLException {
        try (PreparedStatement stmt = conexao.prepareStatement(INSERT_ACAO_SQL)) {
            for (MissaoAcao missaoAcao : missao.getAcoes()) {
                stmt.setInt(1, missao.getId());
                stmt.setInt(2, missaoAcao.getAcao().getId());
                stmt.setInt(3, missaoAcao.getOrdem());
                stmt.executeUpdate();
            }
        }
    }

    private void carregarAcoes(Connection conexao, Missao missao) throws SQLException {
        try (PreparedStatement stmt = conexao.prepareStatement(SELECT_ACOES_SQL)) {
            stmt.setInt(1, missao.getId());
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                missao.getAcoes().add(new MissaoAcao(missao, AcaoDao.getAcao(resultSet), resultSet.getInt("NR_ORDEM")));
            }
        }
    }
}
