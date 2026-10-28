package br.com.soulprime.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import br.com.soulprime.exception.EntidadeNaoEncontradaException;
import br.com.soulprime.factory.ConnectionFactory;
import br.com.soulprime.model.Acao;
import br.com.soulprime.model.CampanhaEsg;
import br.com.soulprime.model.RegistroAcao;
import br.com.soulprime.model.Usuario;
import br.com.soulprime.model.enums.StatusValidacao;

//DAO da tabela TB_REGISTRO_ACAO. O SELECT monta por JOIN o Usuario, a Acao, a CampanhaEsg e o validador
@Repository
public class RegistroAcaoDao {

    private static final String SELECT_SQL = "SELECT r.*, u.NM_USUARIO, a.NM_ACAO, a.NR_IMPACTO, a.NR_CO2_POR_UNIDADE, c.NM_CAMPANHA, "
            + "v.NM_USUARIO AS NM_VALIDADOR FROM TB_REGISTRO_ACAO r "
            + "JOIN TB_USUARIO u ON u.ID_USUARIO = r.ID_USUARIO JOIN TB_ACAO a ON a.ID_ACAO = r.ID_ACAO "
            + "LEFT JOIN TB_CAMPANHA_ESG c ON c.ID_CAMPANHA = r.ID_CAMPANHA LEFT JOIN TB_USUARIO v ON v.ID_USUARIO = r.ID_VALIDADOR ";
    private static final String INSERT_SQL = "INSERT INTO TB_REGISTRO_ACAO (ID_USUARIO, ID_ACAO, ID_CAMPANHA, ID_VALIDADOR, DT_REALIZACAO, NR_REPETICOES, "
            + "NR_CONFIABILIDADE, NR_PONTOS_GERADOS, DS_EVIDENCIA, DS_VALIDACAO, DS_OBSERVACAO, DT_VALIDACAO) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_VALIDACAO_SQL = "UPDATE TB_REGISTRO_ACAO SET DS_VALIDACAO = ?, NR_CONFIABILIDADE = ?, NR_PONTOS_GERADOS = ?, "
            + "DS_OBSERVACAO = ?, DT_VALIDACAO = ?, ID_VALIDADOR = ? WHERE ID_REGISTRO = ?";
    private static final String CONTAR_NO_DIA = "SELECT COUNT(*) FROM TB_REGISTRO_ACAO WHERE ID_USUARIO = ? AND ID_ACAO = ? AND TRUNC(DT_REALIZACAO) = ?";
    private static final String EXISTE_REGISTRO = "SELECT COUNT(*) FROM TB_REGISTRO_ACAO WHERE ID_USUARIO = ? AND ID_ACAO = ?";
    private static final String CONTAR_CREDITADOS = "SELECT COUNT(*) FROM TB_REGISTRO_ACAO WHERE ID_USUARIO = ? AND DS_VALIDACAO IN ('VALIDADA', 'PARCIAL')";
    private static final String CONTAR_PENDENTES = "SELECT COUNT(*) FROM TB_REGISTRO_ACAO WHERE ID_USUARIO = ? AND DS_VALIDACAO = 'PENDENTE'";
    private static final String SOMAR_CO2 = "SELECT NVL(SUM(a.NR_CO2_POR_UNIDADE), 0) FROM TB_REGISTRO_ACAO r JOIN TB_ACAO a ON a.ID_ACAO = r.ID_ACAO "
            + "WHERE r.ID_USUARIO = ? AND r.DS_VALIDACAO IN ('VALIDADA', 'PARCIAL')";

    private final ConnectionFactory connectionFactory;

    public RegistroAcaoDao(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    private RegistroAcao getRegistro(ResultSet resultSet) throws SQLException {
        RegistroAcao registro = new RegistroAcao(resultSet.getInt("ID_REGISTRO"));
        Usuario usuario = new Usuario(resultSet.getInt("ID_USUARIO"));
        usuario.setNome(resultSet.getString("NM_USUARIO"));
        registro.setUsuario(usuario);
        Acao acao = new Acao(resultSet.getInt("ID_ACAO"));
        acao.setNome(resultSet.getString("NM_ACAO"));
        acao.setImpacto(resultSet.getInt("NR_IMPACTO"));
        acao.setCo2PorUnidade(resultSet.getBigDecimal("NR_CO2_POR_UNIDADE"));
        registro.setAcao(acao);
        int idCampanha = resultSet.getInt("ID_CAMPANHA");
        if (!resultSet.wasNull()) {
            CampanhaEsg campanha = new CampanhaEsg(idCampanha);
            campanha.setNome(resultSet.getString("NM_CAMPANHA"));
            registro.setCampanha(campanha);
        }
        int idValidador = resultSet.getInt("ID_VALIDADOR");
        if (!resultSet.wasNull()) {
            Usuario validador = new Usuario(idValidador);
            validador.setNome(resultSet.getString("NM_VALIDADOR"));
            registro.setValidador(validador);
        }
        registro.setDataRealizacao(resultSet.getObject("DT_REALIZACAO", LocalDateTime.class));
        registro.setRepeticoes(resultSet.getInt("NR_REPETICOES"));
        registro.setConfiabilidade(resultSet.getBigDecimal("NR_CONFIABILIDADE"));
        registro.setPontosGerados(resultSet.getLong("NR_PONTOS_GERADOS"));
        registro.setEvidencia(resultSet.getString("DS_EVIDENCIA"));
        registro.setValidacao(StatusValidacao.valueOf(resultSet.getString("DS_VALIDACAO")));
        registro.setObservacao(resultSet.getString("DS_OBSERVACAO"));
        registro.setDataValidacao(resultSet.getObject("DT_VALIDACAO", LocalDateTime.class));
        return registro;
    }

    public void cadastrar(Connection conexao, RegistroAcao registro) throws SQLException {
        try (PreparedStatement stmt = conexao.prepareStatement(INSERT_SQL, new String[] {"ID_REGISTRO"})) {
            stmt.setInt(1, registro.getUsuario().getId());
            stmt.setInt(2, registro.getAcao().getId());
            if (registro.getCampanha() == null) {
                stmt.setNull(3, Types.NUMERIC);
            } else {
                stmt.setInt(3, registro.getCampanha().getId());
            }
            stmt.setNull(4, Types.NUMERIC); //validador: definido apenas na validacao
            stmt.setObject(5, registro.getDataRealizacao());
            stmt.setInt(6, registro.getRepeticoes());
            stmt.setBigDecimal(7, registro.getConfiabilidade());
            stmt.setLong(8, registro.getPontosGerados());
            stmt.setString(9, registro.getEvidencia());
            stmt.setString(10, registro.getValidacao().name());
            stmt.setString(11, registro.getObservacao());
            if (registro.getDataValidacao() == null) {
                stmt.setNull(12, Types.TIMESTAMP);
            } else {
                stmt.setObject(12, registro.getDataValidacao());
            }
            stmt.executeUpdate();
            ResultSet resultSet = stmt.getGeneratedKeys();
            if (resultSet.next()) {
                registro.setId(resultSet.getInt(1));
            }
        }
    }

    //Grava a decisao do validador
    public void atualizarValidacao(Connection conexao, RegistroAcao registro) throws SQLException {
        try (PreparedStatement stmt = conexao.prepareStatement(UPDATE_VALIDACAO_SQL)) {
            stmt.setString(1, registro.getValidacao().name());
            stmt.setBigDecimal(2, registro.getConfiabilidade());
            stmt.setLong(3, registro.getPontosGerados());
            stmt.setString(4, registro.getObservacao());
            stmt.setObject(5, registro.getDataValidacao());
            stmt.setInt(6, registro.getValidador().getId());
            stmt.setInt(7, registro.getId());
            stmt.executeUpdate();
        }
    }

    public RegistroAcao buscarPorId(int id) throws SQLException, EntidadeNaoEncontradaException {
        try (Connection conexao = connectionFactory.getConnection()) {
            return buscar(conexao, SELECT_SQL + "WHERE r.ID_REGISTRO = ?", id);
        }
    }

    //Busca bloqueando a linha: impede dois validadores de decidirem o mesmo registro ao mesmo tempo
    public RegistroAcao buscarPorIdParaAtualizar(Connection conexao, int id) throws SQLException, EntidadeNaoEncontradaException {
        return buscar(conexao, SELECT_SQL + "WHERE r.ID_REGISTRO = ? FOR UPDATE OF r.DS_VALIDACAO", id);
    }

    //Filtros opcionais por status e participante. Pendentes: do mais antigo para o mais recente
    public List<RegistroAcao> listar(StatusValidacao status, Integer usuarioId) throws SQLException {
        String ordem = status == StatusValidacao.PENDENTE ? "r.DT_REALIZACAO" : "r.DT_REALIZACAO DESC";
        String sql = SELECT_SQL + "WHERE (? IS NULL OR r.DS_VALIDACAO = ?) AND (? IS NULL OR r.ID_USUARIO = ?) ORDER BY " + ordem;
        try (Connection conexao = connectionFactory.getConnection(); PreparedStatement stmt = conexao.prepareStatement(sql)) {
            String s = status == null ? null : status.name();
            stmt.setString(1, s);
            stmt.setString(2, s);
            if (usuarioId == null) {
                stmt.setNull(3, Types.NUMERIC);
                stmt.setNull(4, Types.NUMERIC);
            } else {
                stmt.setInt(3, usuarioId);
                stmt.setInt(4, usuarioId);
            }
            ResultSet resultSet = stmt.executeQuery();
            List<RegistroAcao> lista = new ArrayList<>();
            while (resultSet.next()) {
                lista.add(getRegistro(resultSet));
            }
            return lista;
        }
    }

    //Registros do participante na mesma acao no dia (limite diario e repeticoes no dia)
    public int contarNoDia(Connection conexao, int usuarioId, int acaoId, LocalDate dia) throws SQLException {
        try (PreparedStatement stmt = conexao.prepareStatement(CONTAR_NO_DIA)) {
            stmt.setInt(1, usuarioId);
            stmt.setInt(2, acaoId);
            stmt.setObject(3, dia);
            ResultSet resultSet = stmt.executeQuery();
            return resultSet.next() ? resultSet.getInt(1) : 0;
        }
    }

    public boolean existeRegistro(Connection conexao, int usuarioId, int acaoId) throws SQLException {
        try (PreparedStatement stmt = conexao.prepareStatement(EXISTE_REGISTRO)) {
            stmt.setInt(1, usuarioId);
            stmt.setInt(2, acaoId);
            ResultSet resultSet = stmt.executeQuery();
            return resultSet.next() && resultSet.getInt(1) > 0;
        }
    }

    //Registros VALIDADOS ou PARCIAIS (ja creditados) - usado no calculo das missoes
    public int contarCreditados(Connection conexao, int usuarioId) throws SQLException {
        try (PreparedStatement stmt = conexao.prepareStatement(CONTAR_CREDITADOS)) {
            stmt.setInt(1, usuarioId);
            ResultSet resultSet = stmt.executeQuery();
            return resultSet.next() ? resultSet.getInt(1) : 0;
        }
    }

    public int contarCreditados(int usuarioId) throws SQLException {
        try (Connection conexao = connectionFactory.getConnection()) {
            return contarCreditados(conexao, usuarioId);
        }
    }

    public int contarPendentes(int usuarioId) throws SQLException {
        try (Connection conexao = connectionFactory.getConnection(); PreparedStatement stmt = conexao.prepareStatement(CONTAR_PENDENTES)) {
            stmt.setInt(1, usuarioId);
            ResultSet resultSet = stmt.executeQuery();
            return resultSet.next() ? resultSet.getInt(1) : 0;
        }
    }

    //CO2 evitado = soma do CO2 das acoes dos registros creditados
    public BigDecimal somarCo2Creditado(int usuarioId) throws SQLException {
        try (Connection conexao = connectionFactory.getConnection(); PreparedStatement stmt = conexao.prepareStatement(SOMAR_CO2)) {
            stmt.setInt(1, usuarioId);
            ResultSet resultSet = stmt.executeQuery();
            return resultSet.next() ? resultSet.getBigDecimal(1) : BigDecimal.ZERO;
        }
    }

    private RegistroAcao buscar(Connection conexao, String sql, int id) throws SQLException, EntidadeNaoEncontradaException {
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet resultSet = stmt.executeQuery();
            if (!resultSet.next()) {
                throw new EntidadeNaoEncontradaException("Registro " + id + " nao encontrado.");
            }
            return getRegistro(resultSet);
        }
    }
}
