package br.com.soulprime.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import br.com.soulprime.factory.ConnectionFactory;
import br.com.soulprime.model.HistoricoSaque;
import br.com.soulprime.model.Usuario;
import br.com.soulprime.model.enums.StatusSaque;

//DAO da tabela TB_HISTORICO_SAQUE
@Repository
public class HistoricoSaqueDao {

    private static final String INSERT_SQL = "INSERT INTO TB_HISTORICO_SAQUE (ID_USUARIO, NR_CREDITOS_USADOS, VL_SAQUE_REAIS, DS_CHAVE_PIX, DS_STATUS, "
            + "DT_SOLICITACAO, DT_PROCESSAMENTO, DS_OBSERVACAO) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SELECT_POR_USUARIO = "SELECT * FROM TB_HISTORICO_SAQUE WHERE ID_USUARIO = ? ORDER BY DT_SOLICITACAO DESC";

    private final ConnectionFactory connectionFactory;

    public HistoricoSaqueDao(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    private HistoricoSaque getSaque(ResultSet resultSet, Usuario usuario) throws SQLException {
        HistoricoSaque saque = new HistoricoSaque();
        saque.setId(resultSet.getInt("ID_SAQUE"));
        saque.setUsuario(usuario);
        saque.setCreditosUsados(resultSet.getBigDecimal("NR_CREDITOS_USADOS"));
        saque.setValorReais(resultSet.getBigDecimal("VL_SAQUE_REAIS"));
        saque.setChavePix(resultSet.getString("DS_CHAVE_PIX"));
        saque.setStatus(StatusSaque.valueOf(resultSet.getString("DS_STATUS")));
        saque.setDataSolicitacao(resultSet.getObject("DT_SOLICITACAO", LocalDateTime.class));
        saque.setDataProcessamento(resultSet.getObject("DT_PROCESSAMENTO", LocalDateTime.class));
        saque.setObservacao(resultSet.getString("DS_OBSERVACAO"));
        return saque;
    }

    public void cadastrar(Connection conexao, HistoricoSaque saque) throws SQLException {
        try (PreparedStatement stmt = conexao.prepareStatement(INSERT_SQL, new String[] {"ID_SAQUE"})) {
            stmt.setInt(1, saque.getUsuario().getId());
            stmt.setBigDecimal(2, saque.getCreditosUsados());
            stmt.setBigDecimal(3, saque.getValorReais());
            stmt.setString(4, saque.getChavePix());
            stmt.setString(5, saque.getStatus().name());
            stmt.setObject(6, saque.getDataSolicitacao());
            stmt.setObject(7, saque.getDataProcessamento());
            stmt.setString(8, saque.getObservacao());
            stmt.executeUpdate();
            ResultSet resultSet = stmt.getGeneratedKeys();
            if (resultSet.next()) {
                saque.setId(resultSet.getInt(1));
            }
        }
    }

    public List<HistoricoSaque> listarPorUsuario(Usuario usuario) throws SQLException {
        try (Connection conexao = connectionFactory.getConnection(); PreparedStatement stmt = conexao.prepareStatement(SELECT_POR_USUARIO)) {
            stmt.setInt(1, usuario.getId());
            ResultSet resultSet = stmt.executeQuery();
            List<HistoricoSaque> lista = new ArrayList<>();
            while (resultSet.next()) {
                lista.add(getSaque(resultSet, usuario));
            }
            return lista;
        }
    }
}
