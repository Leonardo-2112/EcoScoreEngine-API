package br.com.soulprime.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import br.com.soulprime.factory.ConnectionFactory;
import br.com.soulprime.model.Missao;
import br.com.soulprime.model.Usuario;
import br.com.soulprime.model.UsuarioMissao;
import br.com.soulprime.model.enums.StatusMissaoUsuario;

//DAO da tabela TB_USUARIO_MISSAO (progresso do participante nos desafios tematicos)
@Repository
public class UsuarioMissaoDao {

    private static final String SELECT_SQL = "SELECT um.ID_USUARIO_MISSAO, um.DS_STATUS AS DS_STATUS_UM, um.QT_ACOES_REALIZADAS, um.DT_INICIO, "
            + "um.DT_CONCLUSAO, m.* FROM TB_USUARIO_MISSAO um JOIN TB_MISSAO m ON m.ID_MISSAO = um.ID_MISSAO ";
    private static final String INSERT_SQL = "INSERT INTO TB_USUARIO_MISSAO (ID_USUARIO, ID_MISSAO, DS_STATUS, QT_ACOES_REALIZADAS, DT_INICIO, DT_CONCLUSAO) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE TB_USUARIO_MISSAO SET DS_STATUS = ?, QT_ACOES_REALIZADAS = ?, DT_CONCLUSAO = ? WHERE ID_USUARIO_MISSAO = ?";

    private final ConnectionFactory connectionFactory;

    public UsuarioMissaoDao(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    private UsuarioMissao getUsuarioMissao(ResultSet resultSet, Usuario usuario) throws SQLException {
        UsuarioMissao usuarioMissao = new UsuarioMissao(usuario, MissaoDao.getMissao(resultSet), resultSet.getObject("DT_INICIO", LocalDateTime.class));
        usuarioMissao.setId(resultSet.getInt("ID_USUARIO_MISSAO"));
        usuarioMissao.setStatus(StatusMissaoUsuario.valueOf(resultSet.getString("DS_STATUS_UM")));
        usuarioMissao.setQtAcoesRealizadas(resultSet.getInt("QT_ACOES_REALIZADAS"));
        usuarioMissao.setDataConclusao(resultSet.getObject("DT_CONCLUSAO", LocalDateTime.class));
        return usuarioMissao;
    }

    //Progresso do usuario no desafio (retorna null se ainda nao iniciou)
    public UsuarioMissao buscar(Connection conexao, Usuario usuario, Missao missao) throws SQLException {
        try (PreparedStatement stmt = conexao.prepareStatement(SELECT_SQL + "WHERE um.ID_USUARIO = ? AND um.ID_MISSAO = ?")) {
            stmt.setInt(1, usuario.getId());
            stmt.setInt(2, missao.getId());
            ResultSet resultSet = stmt.executeQuery();
            if (!resultSet.next()) {
                return null;
            }
            return getUsuarioMissao(resultSet, usuario);
        }
    }

    public List<UsuarioMissao> listarPorUsuario(Usuario usuario) throws SQLException {
        try (Connection conexao = connectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(SELECT_SQL + "WHERE um.ID_USUARIO = ? ORDER BY um.DS_STATUS DESC, m.NM_MISSAO")) {
            stmt.setInt(1, usuario.getId());
            ResultSet resultSet = stmt.executeQuery();
            List<UsuarioMissao> lista = new ArrayList<>();
            while (resultSet.next()) {
                lista.add(getUsuarioMissao(resultSet, usuario));
            }
            return lista;
        }
    }

    public void cadastrar(Connection conexao, UsuarioMissao usuarioMissao) throws SQLException {
        try (PreparedStatement stmt = conexao.prepareStatement(INSERT_SQL, new String[] {"ID_USUARIO_MISSAO"})) {
            stmt.setInt(1, usuarioMissao.getUsuario().getId());
            stmt.setInt(2, usuarioMissao.getMissao().getId());
            stmt.setString(3, usuarioMissao.getStatus().name());
            stmt.setInt(4, usuarioMissao.getQtAcoesRealizadas());
            stmt.setObject(5, usuarioMissao.getDataInicio());
            setDataConclusao(stmt, 6, usuarioMissao.getDataConclusao());
            stmt.executeUpdate();
            ResultSet resultSet = stmt.getGeneratedKeys();
            if (resultSet.next()) {
                usuarioMissao.setId(resultSet.getInt(1));
            }
        }
    }

    public void atualizar(Connection conexao, UsuarioMissao usuarioMissao) throws SQLException {
        try (PreparedStatement stmt = conexao.prepareStatement(UPDATE_SQL)) {
            stmt.setString(1, usuarioMissao.getStatus().name());
            stmt.setInt(2, usuarioMissao.getQtAcoesRealizadas());
            setDataConclusao(stmt, 3, usuarioMissao.getDataConclusao());
            stmt.setInt(4, usuarioMissao.getId());
            stmt.executeUpdate();
        }
    }

    private void setDataConclusao(PreparedStatement stmt, int posicao, LocalDateTime data) throws SQLException {
        if (data == null) {
            stmt.setNull(posicao, Types.TIMESTAMP);
        } else {
            stmt.setObject(posicao, data);
        }
    }
}
