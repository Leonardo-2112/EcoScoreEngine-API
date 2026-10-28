package br.com.soulprime.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

import org.springframework.stereotype.Repository;

import br.com.soulprime.exception.EntidadeNaoEncontradaException;
import br.com.soulprime.factory.ConnectionFactory;
import br.com.soulprime.model.Carteira;
import br.com.soulprime.model.Usuario;

//DAO da tabela TB_CARTEIRA (1:1 com o participante)
@Repository
public class CarteiraDao {

    private static final String INSERT_SQL = "INSERT INTO TB_CARTEIRA (ID_USUARIO, NR_CREDITOS_SALDO, VL_TOTAL_SACADO, DT_ULTIMA_ATUALIZACAO) VALUES (?, ?, ?, ?)";
    private static final String SELECT_POR_USUARIO = "SELECT * FROM TB_CARTEIRA WHERE ID_USUARIO = ?";
    private static final String UPDATE_SQL = "UPDATE TB_CARTEIRA SET NR_CREDITOS_SALDO = ?, VL_TOTAL_SACADO = ?, DT_ULTIMA_ATUALIZACAO = ? WHERE ID_CARTEIRA = ?";

    private final ConnectionFactory connectionFactory;

    public CarteiraDao(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    private Carteira getCarteira(ResultSet resultSet, Usuario usuario) throws SQLException {
        Carteira carteira = new Carteira(usuario);
        carteira.setId(resultSet.getInt("ID_CARTEIRA"));
        carteira.setCreditosSaldo(resultSet.getBigDecimal("NR_CREDITOS_SALDO"));
        carteira.setTotalSacado(resultSet.getBigDecimal("VL_TOTAL_SACADO"));
        carteira.setUltimaAtualizacao(resultSet.getObject("DT_ULTIMA_ATUALIZACAO", LocalDateTime.class));
        return carteira;
    }

    public void cadastrar(Connection conexao, Carteira carteira) throws SQLException {
        try (PreparedStatement stmt = conexao.prepareStatement(INSERT_SQL, new String[] {"ID_CARTEIRA"})) {
            stmt.setInt(1, carteira.getUsuario().getId());
            stmt.setBigDecimal(2, carteira.getCreditosSaldo());
            stmt.setBigDecimal(3, carteira.getTotalSacado());
            stmt.setObject(4, carteira.getUltimaAtualizacao());
            stmt.executeUpdate();
            ResultSet resultSet = stmt.getGeneratedKeys();
            if (resultSet.next()) {
                carteira.setId(resultSet.getInt(1));
            }
        }
    }

    public Carteira buscarPorUsuario(Usuario usuario) throws SQLException, EntidadeNaoEncontradaException {
        try (Connection conexao = connectionFactory.getConnection(); PreparedStatement stmt = conexao.prepareStatement(SELECT_POR_USUARIO)) {
            stmt.setInt(1, usuario.getId());
            ResultSet resultSet = stmt.executeQuery();
            if (!resultSet.next()) {
                throw new EntidadeNaoEncontradaException("Carteira nao encontrada: somente participantes possuem carteira.");
            }
            return getCarteira(resultSet, usuario);
        }
    }

    //Busca bloqueando a linha (FOR UPDATE) na conversao/saque
    public Carteira buscarPorUsuarioParaAtualizar(Connection conexao, Usuario usuario) throws SQLException, EntidadeNaoEncontradaException {
        try (PreparedStatement stmt = conexao.prepareStatement(SELECT_POR_USUARIO + " FOR UPDATE")) {
            stmt.setInt(1, usuario.getId());
            ResultSet resultSet = stmt.executeQuery();
            if (!resultSet.next()) {
                throw new EntidadeNaoEncontradaException("Carteira nao encontrada: somente participantes possuem carteira.");
            }
            return getCarteira(resultSet, usuario);
        }
    }

    public void atualizar(Connection conexao, Carteira carteira) throws SQLException {
        try (PreparedStatement stmt = conexao.prepareStatement(UPDATE_SQL)) {
            stmt.setBigDecimal(1, carteira.getCreditosSaldo());
            stmt.setBigDecimal(2, carteira.getTotalSacado());
            stmt.setObject(3, carteira.getUltimaAtualizacao());
            stmt.setInt(4, carteira.getId());
            stmt.executeUpdate();
        }
    }
}
