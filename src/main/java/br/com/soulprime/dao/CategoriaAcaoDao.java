package br.com.soulprime.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import br.com.soulprime.exception.EntidadeNaoEncontradaException;
import br.com.soulprime.factory.ConnectionFactory;
import br.com.soulprime.model.CategoriaAcao;

//DAO da tabela TB_CATEGORIA_ACAO (CRUD completo)
@Repository
public class CategoriaAcaoDao {

    private static final String INSERT_SQL = "INSERT INTO TB_CATEGORIA_ACAO (NM_CATEGORIA, DS_CATEGORIA) VALUES (?, ?)";
    private static final String SELECT_SQL = "SELECT * FROM TB_CATEGORIA_ACAO ORDER BY NM_CATEGORIA";
    private static final String SELECT_POR_ID = "SELECT * FROM TB_CATEGORIA_ACAO WHERE ID_CATEGORIA = ?";
    private static final String UPDATE_SQL = "UPDATE TB_CATEGORIA_ACAO SET NM_CATEGORIA = ?, DS_CATEGORIA = ? WHERE ID_CATEGORIA = ?";
    private static final String DELETE_SQL = "DELETE FROM TB_CATEGORIA_ACAO WHERE ID_CATEGORIA = ?";
    private static final String EXISTE_NOME = "SELECT COUNT(*) FROM TB_CATEGORIA_ACAO WHERE UPPER(NM_CATEGORIA) = UPPER(?) AND ID_CATEGORIA <> ?";
    private static final String CONTAR_ACOES = "SELECT COUNT(*) FROM TB_ACAO WHERE ID_CATEGORIA = ?";

    private final ConnectionFactory connectionFactory;

    public CategoriaAcaoDao(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    static CategoriaAcao getCategoria(ResultSet resultSet) throws SQLException {
        CategoriaAcao categoria = new CategoriaAcao(resultSet.getInt("ID_CATEGORIA"));
        categoria.setNome(resultSet.getString("NM_CATEGORIA"));
        categoria.setDescricao(resultSet.getString("DS_CATEGORIA"));
        return categoria;
    }

    public void cadastrar(CategoriaAcao categoria) throws SQLException {
        try (Connection conexao = connectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(INSERT_SQL, new String[] {"ID_CATEGORIA"})) {
            stmt.setString(1, categoria.getNome());
            stmt.setString(2, categoria.getDescricao());
            stmt.executeUpdate();
            ResultSet resultSet = stmt.getGeneratedKeys();
            if (resultSet.next()) {
                categoria.setId(resultSet.getInt(1));
            }
        }
    }

    public List<CategoriaAcao> listar() throws SQLException {
        try (Connection conexao = connectionFactory.getConnection(); PreparedStatement stmt = conexao.prepareStatement(SELECT_SQL)) {
            ResultSet resultSet = stmt.executeQuery();
            List<CategoriaAcao> lista = new ArrayList<>();
            while (resultSet.next()) {
                lista.add(getCategoria(resultSet));
            }
            return lista;
        }
    }

    public CategoriaAcao buscarPorId(int id) throws SQLException, EntidadeNaoEncontradaException {
        try (Connection conexao = connectionFactory.getConnection(); PreparedStatement stmt = conexao.prepareStatement(SELECT_POR_ID)) {
            stmt.setInt(1, id);
            ResultSet resultSet = stmt.executeQuery();
            if (!resultSet.next()) {
                throw new EntidadeNaoEncontradaException("Categoria " + id + " nao encontrada.");
            }
            return getCategoria(resultSet);
        }
    }

    public void atualizar(CategoriaAcao categoria) throws SQLException, EntidadeNaoEncontradaException {
        try (Connection conexao = connectionFactory.getConnection(); PreparedStatement stmt = conexao.prepareStatement(UPDATE_SQL)) {
            stmt.setString(1, categoria.getNome());
            stmt.setString(2, categoria.getDescricao());
            stmt.setInt(3, categoria.getId());
            if (stmt.executeUpdate() == 0) {
                throw new EntidadeNaoEncontradaException("Categoria nao encontrada para atualizacao!");
            }
        }
    }

    public void remover(int id) throws SQLException, EntidadeNaoEncontradaException {
        try (Connection conexao = connectionFactory.getConnection(); PreparedStatement stmt = conexao.prepareStatement(DELETE_SQL)) {
            stmt.setInt(1, id);
            if (stmt.executeUpdate() == 0) {
                throw new EntidadeNaoEncontradaException("Categoria nao encontrada para exclusao!");
            }
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

    public int contarAcoes(int id) throws SQLException {
        try (Connection conexao = connectionFactory.getConnection(); PreparedStatement stmt = conexao.prepareStatement(CONTAR_ACOES)) {
            stmt.setInt(1, id);
            ResultSet resultSet = stmt.executeQuery();
            return resultSet.next() ? resultSet.getInt(1) : 0;
        }
    }
}
