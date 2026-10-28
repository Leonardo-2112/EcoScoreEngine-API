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
import br.com.soulprime.model.HistoricoConversao;
import br.com.soulprime.model.Usuario;

//DAO da tabela TB_HISTORICO_CONVERSAO
@Repository
public class HistoricoConversaoDao {

    private static final String INSERT_SQL = "INSERT INTO TB_HISTORICO_CONVERSAO (ID_USUARIO, NR_PONTOS_UTILIZADOS, NR_CREDITOS_GERADOS, DT_CONVERSAO, DS_OBSERVACAO) VALUES (?, ?, ?, ?, ?)";
    private static final String SELECT_POR_USUARIO = "SELECT * FROM TB_HISTORICO_CONVERSAO WHERE ID_USUARIO = ? ORDER BY DT_CONVERSAO DESC";

    private final ConnectionFactory connectionFactory;

    public HistoricoConversaoDao(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    private HistoricoConversao getConversao(ResultSet resultSet, Usuario usuario) throws SQLException {
        HistoricoConversao conversao = new HistoricoConversao();
        conversao.setId(resultSet.getInt("ID_CONVERSAO"));
        conversao.setUsuario(usuario);
        conversao.setPontosUtilizados(resultSet.getLong("NR_PONTOS_UTILIZADOS"));
        conversao.setCreditosGerados(resultSet.getBigDecimal("NR_CREDITOS_GERADOS"));
        conversao.setDataConversao(resultSet.getObject("DT_CONVERSAO", LocalDateTime.class));
        conversao.setObservacao(resultSet.getString("DS_OBSERVACAO"));
        return conversao;
    }

    public void cadastrar(Connection conexao, HistoricoConversao conversao) throws SQLException {
        try (PreparedStatement stmt = conexao.prepareStatement(INSERT_SQL, new String[] {"ID_CONVERSAO"})) {
            stmt.setInt(1, conversao.getUsuario().getId());
            stmt.setLong(2, conversao.getPontosUtilizados());
            stmt.setBigDecimal(3, conversao.getCreditosGerados());
            stmt.setObject(4, conversao.getDataConversao());
            stmt.setString(5, conversao.getObservacao());
            stmt.executeUpdate();
            ResultSet resultSet = stmt.getGeneratedKeys();
            if (resultSet.next()) {
                conversao.setId(resultSet.getInt(1));
            }
        }
    }

    public List<HistoricoConversao> listarPorUsuario(Usuario usuario) throws SQLException {
        try (Connection conexao = connectionFactory.getConnection(); PreparedStatement stmt = conexao.prepareStatement(SELECT_POR_USUARIO)) {
            stmt.setInt(1, usuario.getId());
            ResultSet resultSet = stmt.executeQuery();
            List<HistoricoConversao> lista = new ArrayList<>();
            while (resultSet.next()) {
                lista.add(getConversao(resultSet, usuario));
            }
            return lista;
        }
    }
}
