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

import br.com.soulprime.exception.EntidadeNaoEncontradaException;
import br.com.soulprime.factory.ConnectionFactory;
import br.com.soulprime.model.Acao;
import br.com.soulprime.model.enums.StatusAtivacao;

//DAO da tabela TB_ACAO. O SELECT traz a CategoriaAcao por JOIN (Acao.categoria)
@Repository
public class AcaoDao {

    static final String SELECT_SQL = "SELECT a.*, c.NM_CATEGORIA, c.DS_CATEGORIA FROM TB_ACAO a "
            + "JOIN TB_CATEGORIA_ACAO c ON c.ID_CATEGORIA = a.ID_CATEGORIA ";
    private static final String INSERT_SQL = "INSERT INTO TB_ACAO (ID_CATEGORIA, NM_ACAO, DS_ACAO, NR_IMPACTO, NR_LIMITE_DIARIO, FL_REPETIVEL, "
            + "NR_CO2_POR_UNIDADE, DT_CRIACAO, DS_STATUS) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE TB_ACAO SET ID_CATEGORIA = ?, NM_ACAO = ?, DS_ACAO = ?, NR_IMPACTO = ?, NR_LIMITE_DIARIO = ?, "
            + "FL_REPETIVEL = ?, NR_CO2_POR_UNIDADE = ? WHERE ID_ACAO = ?";
    private static final String UPDATE_STATUS_SQL = "UPDATE TB_ACAO SET DS_STATUS = ? WHERE ID_ACAO = ?";
    private static final String EXISTE_NOME = "SELECT COUNT(*) FROM TB_ACAO WHERE UPPER(NM_ACAO) = UPPER(?) AND ID_ACAO <> ?";

    private final ConnectionFactory connectionFactory;

    public AcaoDao(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    //Monta a Acao com a CategoriaAcao (tambem usado pelos DAOs de campanha e missao)
    static Acao getAcao(ResultSet resultSet) throws SQLException {
        Acao acao = new Acao(resultSet.getInt("ID_ACAO"));
        acao.setNome(resultSet.getString("NM_ACAO"));
        acao.setDescricao(resultSet.getString("DS_ACAO"));
        acao.setImpacto(resultSet.getInt("NR_IMPACTO"));
        acao.setLimiteDiario(resultSet.getInt("NR_LIMITE_DIARIO"));
        acao.setRepetivel(resultSet.getString("FL_REPETIVEL").equals("S"));
        acao.setCo2PorUnidade(resultSet.getBigDecimal("NR_CO2_POR_UNIDADE"));
        acao.setDataCriacao(resultSet.getObject("DT_CRIACAO", LocalDateTime.class));
        acao.setStatus(StatusAtivacao.valueOf(resultSet.getString("DS_STATUS")));
        acao.setCategoria(CategoriaAcaoDao.getCategoria(resultSet));
        return acao;
    }

    public void cadastrar(Acao acao) throws SQLException {
        try (Connection conexao = connectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(INSERT_SQL, new String[] {"ID_ACAO"})) {
            preencher(stmt, acao);
            stmt.setObject(8, acao.getDataCriacao());
            stmt.setString(9, acao.getStatus().name());
            stmt.executeUpdate();
            ResultSet resultSet = stmt.getGeneratedKeys();
            if (resultSet.next()) {
                acao.setId(resultSet.getInt(1));
            }
        }
    }

    public void atualizar(Acao acao) throws SQLException, EntidadeNaoEncontradaException {
        try (Connection conexao = connectionFactory.getConnection(); PreparedStatement stmt = conexao.prepareStatement(UPDATE_SQL)) {
            preencher(stmt, acao);
            stmt.setInt(8, acao.getId());
            if (stmt.executeUpdate() == 0) {
                throw new EntidadeNaoEncontradaException("Acao nao encontrada para atualizacao!");
            }
        }
    }

    public void atualizarStatus(int id, StatusAtivacao status) throws SQLException, EntidadeNaoEncontradaException {
        try (Connection conexao = connectionFactory.getConnection(); PreparedStatement stmt = conexao.prepareStatement(UPDATE_STATUS_SQL)) {
            stmt.setString(1, status.name());
            stmt.setInt(2, id);
            if (stmt.executeUpdate() == 0) {
                throw new EntidadeNaoEncontradaException("Acao nao encontrada para atualizacao!");
            }
        }
    }

    public Acao buscarPorId(int id) throws SQLException, EntidadeNaoEncontradaException {
        try (Connection conexao = connectionFactory.getConnection()) {
            return buscarPorId(conexao, id);
        }
    }

    public Acao buscarPorId(Connection conexao, int id) throws SQLException, EntidadeNaoEncontradaException {
        try (PreparedStatement stmt = conexao.prepareStatement(SELECT_SQL + "WHERE a.ID_ACAO = ?")) {
            stmt.setInt(1, id);
            ResultSet resultSet = stmt.executeQuery();
            if (!resultSet.next()) {
                throw new EntidadeNaoEncontradaException("Acao " + id + " nao encontrada.");
            }
            return getAcao(resultSet);
        }
    }

    //Filtros opcionais por status e categoria (nulo = sem filtro)
    public List<Acao> listar(StatusAtivacao status, Integer categoriaId) throws SQLException {
        String sql = SELECT_SQL + "WHERE (? IS NULL OR a.DS_STATUS = ?) AND (? IS NULL OR a.ID_CATEGORIA = ?) ORDER BY a.NR_IMPACTO, a.NM_ACAO";
        try (Connection conexao = connectionFactory.getConnection(); PreparedStatement stmt = conexao.prepareStatement(sql)) {
            String s = status == null ? null : status.name();
            stmt.setString(1, s);
            stmt.setString(2, s);
            if (categoriaId == null) {
                stmt.setNull(3, Types.NUMERIC);
                stmt.setNull(4, Types.NUMERIC);
            } else {
                stmt.setInt(3, categoriaId);
                stmt.setInt(4, categoriaId);
            }
            ResultSet resultSet = stmt.executeQuery();
            List<Acao> lista = new ArrayList<>();
            while (resultSet.next()) {
                lista.add(getAcao(resultSet));
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

    private void preencher(PreparedStatement stmt, Acao acao) throws SQLException {
        stmt.setInt(1, acao.getCategoria().getId());
        stmt.setString(2, acao.getNome());
        stmt.setString(3, acao.getDescricao());
        stmt.setInt(4, acao.getImpacto());
        stmt.setInt(5, acao.getLimiteDiario());
        stmt.setString(6, acao.isRepetivel() ? "S" : "N");
        stmt.setBigDecimal(7, acao.getCo2PorUnidade());
    }
}
