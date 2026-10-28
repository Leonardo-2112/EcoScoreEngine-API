package br.com.soulprime.dao;

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
import br.com.soulprime.model.CampanhaAcao;
import br.com.soulprime.model.CampanhaEsg;
import br.com.soulprime.model.EmpresaParceira;
import br.com.soulprime.model.enums.StatusCampanha;
import br.com.soulprime.model.enums.StatusEmpresa;

//DAO das tabelas TB_CAMPANHA_ESG e TB_CAMPANHA_ACAO (acoes patrocinadas)
@Repository
public class CampanhaEsgDao {

    private static final String SELECT_SQL = "SELECT c.*, e.NM_EMPRESA, e.DS_STATUS AS DS_STATUS_EMPRESA FROM TB_CAMPANHA_ESG c "
            + "JOIN TB_EMPRESA_PARCEIRA e ON e.ID_EMPRESA = c.ID_EMPRESA ";
    private static final String INSERT_SQL = "INSERT INTO TB_CAMPANHA_ESG (ID_EMPRESA, NM_CAMPANHA, DS_CAMPANHA, VL_ORCAMENTO, VL_ORCAMENTO_GASTO, "
            + "DT_INICIO, DT_FIM, NR_BONUS_CREDITOS, DS_STATUS, DT_CRIACAO) VALUES (?, ?, ?, ?, 0, ?, ?, 1, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE TB_CAMPANHA_ESG SET NM_CAMPANHA = ?, DS_CAMPANHA = ?, VL_ORCAMENTO = ?, DT_INICIO = ?, DT_FIM = ? WHERE ID_CAMPANHA = ?";
    private static final String UPDATE_STATUS_SQL = "UPDATE TB_CAMPANHA_ESG SET DS_STATUS = ? WHERE ID_CAMPANHA = ?";
    private static final String INSERT_ACAO_SQL = "INSERT INTO TB_CAMPANHA_ACAO (ID_CAMPANHA, ID_ACAO, NR_BONUS_EXTRA, DT_INCLUSAO) VALUES (?, ?, 1, ?)";
    private static final String DELETE_ACOES_SQL = "DELETE FROM TB_CAMPANHA_ACAO WHERE ID_CAMPANHA = ?";
    private static final String SELECT_ACOES_SQL = AcaoDao.SELECT_SQL.replace("SELECT a.*", "SELECT a.*, ca.NR_BONUS_EXTRA, ca.DT_INCLUSAO")
            + "JOIN TB_CAMPANHA_ACAO ca ON ca.ID_ACAO = a.ID_ACAO WHERE ca.ID_CAMPANHA = ? ORDER BY a.NR_IMPACTO";
    private static final String SELECT_VIGENTE = SELECT_SQL + "JOIN TB_CAMPANHA_ACAO ca ON ca.ID_CAMPANHA = c.ID_CAMPANHA "
            + "WHERE ca.ID_ACAO = ? AND c.DS_STATUS = 'ATIVA' AND ? BETWEEN c.DT_INICIO AND c.DT_FIM ORDER BY c.DT_INICIO DESC FETCH FIRST 1 ROWS ONLY";
    private static final String EXISTE_NOME = "SELECT COUNT(*) FROM TB_CAMPANHA_ESG WHERE UPPER(NM_CAMPANHA) = UPPER(?) AND ID_CAMPANHA <> ?";

    private final ConnectionFactory connectionFactory;

    public CampanhaEsgDao(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    private CampanhaEsg getCampanha(ResultSet resultSet) throws SQLException {
        CampanhaEsg campanha = new CampanhaEsg(resultSet.getInt("ID_CAMPANHA"));
        campanha.setNome(resultSet.getString("NM_CAMPANHA"));
        campanha.setDescricao(resultSet.getString("DS_CAMPANHA"));
        campanha.setOrcamento(resultSet.getBigDecimal("VL_ORCAMENTO"));
        campanha.setOrcamentoGasto(resultSet.getBigDecimal("VL_ORCAMENTO_GASTO"));
        campanha.setDataInicio(resultSet.getObject("DT_INICIO", LocalDate.class));
        campanha.setDataFim(resultSet.getObject("DT_FIM", LocalDate.class));
        campanha.setBonusCreditos(resultSet.getBigDecimal("NR_BONUS_CREDITOS"));
        campanha.setStatus(StatusCampanha.valueOf(resultSet.getString("DS_STATUS")));
        campanha.setDataCriacao(resultSet.getObject("DT_CRIACAO", LocalDateTime.class));
        EmpresaParceira empresa = new EmpresaParceira(resultSet.getInt("ID_EMPRESA"));
        empresa.setNome(resultSet.getString("NM_EMPRESA"));
        empresa.setStatus(StatusEmpresa.valueOf(resultSet.getString("DS_STATUS_EMPRESA")));
        campanha.setEmpresa(empresa);
        return campanha;
    }

    //Grava a campanha e as acoes patrocinadas na mesma transacao
    public void cadastrar(Connection conexao, CampanhaEsg campanha) throws SQLException {
        try (PreparedStatement stmt = conexao.prepareStatement(INSERT_SQL, new String[] {"ID_CAMPANHA"})) {
            stmt.setInt(1, campanha.getEmpresa().getId());
            stmt.setString(2, campanha.getNome());
            stmt.setString(3, campanha.getDescricao());
            stmt.setBigDecimal(4, campanha.getOrcamento());
            stmt.setObject(5, campanha.getDataInicio());
            stmt.setObject(6, campanha.getDataFim());
            stmt.setString(7, campanha.getStatus().name());
            stmt.setObject(8, campanha.getDataCriacao());
            stmt.executeUpdate();
            ResultSet resultSet = stmt.getGeneratedKeys();
            if (resultSet.next()) {
                campanha.setId(resultSet.getInt(1));
            }
        }
        cadastrarAcoes(conexao, campanha);
    }

    //Atualiza os dados e substitui a lista de acoes patrocinadas
    public void atualizar(Connection conexao, CampanhaEsg campanha) throws SQLException {
        try (PreparedStatement stmt = conexao.prepareStatement(UPDATE_SQL)) {
            stmt.setString(1, campanha.getNome());
            stmt.setString(2, campanha.getDescricao());
            stmt.setBigDecimal(3, campanha.getOrcamento());
            stmt.setObject(4, campanha.getDataInicio());
            stmt.setObject(5, campanha.getDataFim());
            stmt.setInt(6, campanha.getId());
            stmt.executeUpdate();
        }
        try (PreparedStatement stmt = conexao.prepareStatement(DELETE_ACOES_SQL)) {
            stmt.setInt(1, campanha.getId());
            stmt.executeUpdate();
        }
        cadastrarAcoes(conexao, campanha);
    }

    public void atualizarStatus(int id, StatusCampanha status) throws SQLException, EntidadeNaoEncontradaException {
        try (Connection conexao = connectionFactory.getConnection(); PreparedStatement stmt = conexao.prepareStatement(UPDATE_STATUS_SQL)) {
            stmt.setString(1, status.name());
            stmt.setInt(2, id);
            if (stmt.executeUpdate() == 0) {
                throw new EntidadeNaoEncontradaException("Campanha nao encontrada para atualizacao!");
            }
        }
    }

    public CampanhaEsg buscarPorId(int id) throws SQLException, EntidadeNaoEncontradaException {
        try (Connection conexao = connectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(SELECT_SQL + "WHERE c.ID_CAMPANHA = ?")) {
            stmt.setInt(1, id);
            ResultSet resultSet = stmt.executeQuery();
            if (!resultSet.next()) {
                throw new EntidadeNaoEncontradaException("Campanha " + id + " nao encontrada.");
            }
            CampanhaEsg campanha = getCampanha(resultSet);
            carregarAcoes(conexao, campanha);
            return campanha;
        }
    }

    //Filtros opcionais por status e empresa (nulo = sem filtro)
    public List<CampanhaEsg> listar(StatusCampanha status, Integer empresaId) throws SQLException {
        String sql = SELECT_SQL + "WHERE (? IS NULL OR c.DS_STATUS = ?) AND (? IS NULL OR c.ID_EMPRESA = ?) ORDER BY c.DT_INICIO DESC, c.NM_CAMPANHA";
        try (Connection conexao = connectionFactory.getConnection(); PreparedStatement stmt = conexao.prepareStatement(sql)) {
            String s = status == null ? null : status.name();
            stmt.setString(1, s);
            stmt.setString(2, s);
            if (empresaId == null) {
                stmt.setNull(3, Types.NUMERIC);
                stmt.setNull(4, Types.NUMERIC);
            } else {
                stmt.setInt(3, empresaId);
                stmt.setInt(4, empresaId);
            }
            ResultSet resultSet = stmt.executeQuery();
            List<CampanhaEsg> lista = new ArrayList<>();
            while (resultSet.next()) {
                lista.add(getCampanha(resultSet));
            }
            for (CampanhaEsg campanha : lista) {
                carregarAcoes(conexao, campanha);
            }
            return lista;
        }
    }

    //Campanha ATIVA que cobre a data e patrocina a acao (a mais recente). Retorna null se nao houver
    public CampanhaEsg buscarVigente(Connection conexao, int acaoId, LocalDate data) throws SQLException {
        try (PreparedStatement stmt = conexao.prepareStatement(SELECT_VIGENTE)) {
            stmt.setInt(1, acaoId);
            stmt.setObject(2, data);
            ResultSet resultSet = stmt.executeQuery();
            if (!resultSet.next()) {
                return null;
            }
            return getCampanha(resultSet);
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

    private void cadastrarAcoes(Connection conexao, CampanhaEsg campanha) throws SQLException {
        try (PreparedStatement stmt = conexao.prepareStatement(INSERT_ACAO_SQL)) {
            for (CampanhaAcao campanhaAcao : campanha.getAcoes()) {
                stmt.setInt(1, campanha.getId());
                stmt.setInt(2, campanhaAcao.getAcao().getId());
                stmt.setObject(3, campanhaAcao.getDataInclusao());
                stmt.executeUpdate();
            }
        }
    }

    private void carregarAcoes(Connection conexao, CampanhaEsg campanha) throws SQLException {
        try (PreparedStatement stmt = conexao.prepareStatement(SELECT_ACOES_SQL)) {
            stmt.setInt(1, campanha.getId());
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                CampanhaAcao campanhaAcao = new CampanhaAcao(campanha, AcaoDao.getAcao(resultSet));
                campanhaAcao.setBonusExtra(resultSet.getBigDecimal("NR_BONUS_EXTRA"));
                campanhaAcao.setDataInclusao(resultSet.getObject("DT_INCLUSAO", LocalDateTime.class));
                campanha.getAcoes().add(campanhaAcao);
            }
        }
    }
}
