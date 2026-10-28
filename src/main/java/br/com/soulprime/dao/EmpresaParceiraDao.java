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
import br.com.soulprime.model.EmpresaParceira;
import br.com.soulprime.model.enums.StatusEmpresa;

//DAO da tabela TB_EMPRESA_PARCEIRA
@Repository
public class EmpresaParceiraDao {

    private static final String INSERT_SQL = "INSERT INTO TB_EMPRESA_PARCEIRA (NM_EMPRESA, NR_CNPJ, DS_EMAIL, DS_SEGMENTO, VL_ORCAMENTO_ESG, DT_CADASTRO, DS_STATUS) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE TB_EMPRESA_PARCEIRA SET NM_EMPRESA = ?, DS_EMAIL = ?, DS_SEGMENTO = ?, VL_ORCAMENTO_ESG = ? WHERE ID_EMPRESA = ?";
    private static final String UPDATE_STATUS_SQL = "UPDATE TB_EMPRESA_PARCEIRA SET DS_STATUS = ? WHERE ID_EMPRESA = ?";
    private static final String SELECT_SQL = "SELECT * FROM TB_EMPRESA_PARCEIRA ORDER BY NM_EMPRESA";
    private static final String SELECT_POR_STATUS = "SELECT * FROM TB_EMPRESA_PARCEIRA WHERE DS_STATUS = ? ORDER BY NM_EMPRESA";
    private static final String SELECT_POR_ID = "SELECT * FROM TB_EMPRESA_PARCEIRA WHERE ID_EMPRESA = ?";
    private static final String EXISTE_CNPJ = "SELECT COUNT(*) FROM TB_EMPRESA_PARCEIRA WHERE REGEXP_REPLACE(NR_CNPJ, '[^0-9]', '') = REGEXP_REPLACE(?, '[^0-9]', '')";
    private static final String EXISTE_EMAIL = "SELECT COUNT(*) FROM TB_EMPRESA_PARCEIRA WHERE UPPER(DS_EMAIL) = UPPER(?) AND ID_EMPRESA <> ?";

    private final ConnectionFactory connectionFactory;

    public EmpresaParceiraDao(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    private EmpresaParceira getEmpresa(ResultSet resultSet) throws SQLException {
        EmpresaParceira empresa = new EmpresaParceira(resultSet.getInt("ID_EMPRESA"));
        empresa.setNome(resultSet.getString("NM_EMPRESA"));
        empresa.setCnpj(resultSet.getString("NR_CNPJ"));
        empresa.setEmail(resultSet.getString("DS_EMAIL"));
        empresa.setSegmento(resultSet.getString("DS_SEGMENTO"));
        empresa.setOrcamentoEsg(resultSet.getBigDecimal("VL_ORCAMENTO_ESG"));
        empresa.setDataCadastro(resultSet.getObject("DT_CADASTRO", java.time.LocalDateTime.class));
        empresa.setStatus(StatusEmpresa.valueOf(resultSet.getString("DS_STATUS")));
        return empresa;
    }

    //Usa a conexao da transacao (empresa + representante sao gravados juntos)
    public void cadastrar(Connection conexao, EmpresaParceira empresa) throws SQLException {
        try (PreparedStatement stmt = conexao.prepareStatement(INSERT_SQL, new String[] {"ID_EMPRESA"})) {
            stmt.setString(1, empresa.getNome());
            stmt.setString(2, empresa.getCnpj());
            stmt.setString(3, empresa.getEmail());
            stmt.setString(4, empresa.getSegmento());
            stmt.setBigDecimal(5, empresa.getOrcamentoEsg());
            stmt.setObject(6, empresa.getDataCadastro());
            stmt.setString(7, empresa.getStatus().name());
            stmt.executeUpdate();
            ResultSet resultSet = stmt.getGeneratedKeys();
            if (resultSet.next()) {
                empresa.setId(resultSet.getInt(1));
            }
        }
    }

    public void atualizar(EmpresaParceira empresa) throws SQLException, EntidadeNaoEncontradaException {
        try (Connection conexao = connectionFactory.getConnection(); PreparedStatement stmt = conexao.prepareStatement(UPDATE_SQL)) {
            stmt.setString(1, empresa.getNome());
            stmt.setString(2, empresa.getEmail());
            stmt.setString(3, empresa.getSegmento());
            stmt.setBigDecimal(4, empresa.getOrcamentoEsg());
            stmt.setInt(5, empresa.getId());
            if (stmt.executeUpdate() == 0) {
                throw new EntidadeNaoEncontradaException("Empresa nao encontrada para atualizacao!");
            }
        }
    }

    public void atualizarStatus(int id, StatusEmpresa status) throws SQLException, EntidadeNaoEncontradaException {
        try (Connection conexao = connectionFactory.getConnection(); PreparedStatement stmt = conexao.prepareStatement(UPDATE_STATUS_SQL)) {
            stmt.setString(1, status.name());
            stmt.setInt(2, id);
            if (stmt.executeUpdate() == 0) {
                throw new EntidadeNaoEncontradaException("Empresa nao encontrada para atualizacao!");
            }
        }
    }

    public EmpresaParceira buscarPorId(int id) throws SQLException, EntidadeNaoEncontradaException {
        try (Connection conexao = connectionFactory.getConnection(); PreparedStatement stmt = conexao.prepareStatement(SELECT_POR_ID)) {
            stmt.setInt(1, id);
            ResultSet resultSet = stmt.executeQuery();
            if (!resultSet.next()) {
                throw new EntidadeNaoEncontradaException("Empresa " + id + " nao encontrada.");
            }
            return getEmpresa(resultSet);
        }
    }

    //Lista todas ou somente as de um status (status nulo = todas)
    public List<EmpresaParceira> listar(StatusEmpresa status) throws SQLException {
        try (Connection conexao = connectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(status == null ? SELECT_SQL : SELECT_POR_STATUS)) {
            if (status != null) {
                stmt.setString(1, status.name());
            }
            ResultSet resultSet = stmt.executeQuery();
            List<EmpresaParceira> lista = new ArrayList<>();
            while (resultSet.next()) {
                lista.add(getEmpresa(resultSet));
            }
            return lista;
        }
    }

    //Compara so os digitos (aceita CNPJ com ou sem mascara)
    public boolean existeCnpj(String cnpj) throws SQLException {
        try (Connection conexao = connectionFactory.getConnection(); PreparedStatement stmt = conexao.prepareStatement(EXISTE_CNPJ)) {
            stmt.setString(1, cnpj);
            ResultSet resultSet = stmt.executeQuery();
            return resultSet.next() && resultSet.getInt(1) > 0;
        }
    }

    //idIgnorado: na edicao, ignora a propria empresa (use 0 no cadastro)
    public boolean existeEmail(String email, int idIgnorado) throws SQLException {
        try (Connection conexao = connectionFactory.getConnection(); PreparedStatement stmt = conexao.prepareStatement(EXISTE_EMAIL)) {
            stmt.setString(1, email.trim());
            stmt.setInt(2, idIgnorado);
            ResultSet resultSet = stmt.executeQuery();
            return resultSet.next() && resultSet.getInt(1) > 0;
        }
    }
}
