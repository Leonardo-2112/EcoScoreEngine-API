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
import br.com.soulprime.model.EmpresaParceira;
import br.com.soulprime.model.Usuario;
import br.com.soulprime.model.enums.Perfil;
import br.com.soulprime.model.enums.StatusEmpresa;
import br.com.soulprime.model.enums.StatusUsuario;

//DAO da tabela TB_USUARIO. O SELECT traz o Nivel e a Empresa por JOIN (Usuario.nivel e Usuario.empresa)
@Repository
public class UsuarioDao {

    private static final String SELECT_SQL = "SELECT u.*, n.DS_NIVEL, n.NR_MISSOES_MIN, n.NR_MISSOES_MAX, n.NR_BONUS_PONTOS, "
            + "e.NM_EMPRESA, e.DS_STATUS AS DS_STATUS_EMPRESA FROM TB_USUARIO u "
            + "JOIN TB_NIVEL n ON n.ID_NIVEL = u.ID_NIVEL LEFT JOIN TB_EMPRESA_PARCEIRA e ON e.ID_EMPRESA = u.ID_EMPRESA ";
    private static final String INSERT_SQL = "INSERT INTO TB_USUARIO (NM_USUARIO, DS_EMAIL, DS_SENHA, NR_CPF, NR_TELEFONE, DT_NASCIMENTO, NR_CEP, "
            + "DS_LOGRADOURO, NM_CIDADE, SG_UF, DS_CHAVE_PIX, DS_PERFIL, ID_EMPRESA, ID_NIVEL, NR_PONTOS_TOTAL, NR_PONTOS_SALDO, "
            + "QT_MISSOES_CONCLUIDAS, NR_TENTATIVAS_LOGIN, DT_CADASTRO, DS_STATUS) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0, 0, 0, 0, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE TB_USUARIO SET NM_USUARIO = ?, DS_EMAIL = ?, DS_SENHA = ?, NR_TELEFONE = ?, DT_NASCIMENTO = ?, "
            + "NR_CEP = ?, DS_LOGRADOURO = ?, NM_CIDADE = ?, SG_UF = ?, DS_CHAVE_PIX = ? WHERE ID_USUARIO = ?";
    private static final String UPDATE_PONTUACAO_SQL = "UPDATE TB_USUARIO SET NR_PONTOS_TOTAL = ?, NR_PONTOS_SALDO = ?, QT_MISSOES_CONCLUIDAS = ?, ID_NIVEL = ? WHERE ID_USUARIO = ?";
    private static final String UPDATE_ACESSO_SQL = "UPDATE TB_USUARIO SET NR_TENTATIVAS_LOGIN = ?, DS_STATUS = ? WHERE ID_USUARIO = ?";
    private static final String EXISTE_EMAIL = "SELECT COUNT(*) FROM TB_USUARIO WHERE UPPER(DS_EMAIL) = UPPER(?) AND ID_USUARIO <> ?";
    private static final String EXISTE_CPF = "SELECT COUNT(*) FROM TB_USUARIO WHERE NR_CPF = ?";

    private final ConnectionFactory connectionFactory;

    public UsuarioDao(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    //Monta o Usuario com os objetos associados (Nivel e EmpresaParceira)
    private Usuario getUsuario(ResultSet resultSet) throws SQLException {
        Usuario usuario = new Usuario(resultSet.getInt("ID_USUARIO"));
        usuario.setNome(resultSet.getString("NM_USUARIO"));
        usuario.setEmail(resultSet.getString("DS_EMAIL"));
        usuario.setSenha(resultSet.getString("DS_SENHA"));
        usuario.setCpf(resultSet.getString("NR_CPF"));
        usuario.setTelefone(resultSet.getString("NR_TELEFONE"));
        usuario.setDataNascimento(resultSet.getObject("DT_NASCIMENTO", LocalDate.class));
        usuario.setCep(resultSet.getString("NR_CEP"));
        usuario.setLogradouro(resultSet.getString("DS_LOGRADOURO"));
        usuario.setCidade(resultSet.getString("NM_CIDADE"));
        usuario.setUf(resultSet.getString("SG_UF"));
        usuario.setChavePix(resultSet.getString("DS_CHAVE_PIX"));
        usuario.setPerfil(Perfil.valueOf(resultSet.getString("DS_PERFIL")));
        int idEmpresa = resultSet.getInt("ID_EMPRESA");
        if (!resultSet.wasNull()) {
            EmpresaParceira empresa = new EmpresaParceira(idEmpresa);
            empresa.setNome(resultSet.getString("NM_EMPRESA"));
            empresa.setStatus(StatusEmpresa.valueOf(resultSet.getString("DS_STATUS_EMPRESA")));
            usuario.setEmpresa(empresa);
        }
        usuario.setNivel(NivelDao.getNivel(resultSet));
        usuario.setPontosTotal(resultSet.getLong("NR_PONTOS_TOTAL"));
        usuario.setPontosSaldo(resultSet.getLong("NR_PONTOS_SALDO"));
        usuario.setMissoesConcluidas(resultSet.getInt("QT_MISSOES_CONCLUIDAS"));
        usuario.setTentativasLogin(resultSet.getInt("NR_TENTATIVAS_LOGIN"));
        usuario.setDataCadastro(resultSet.getObject("DT_CADASTRO", LocalDateTime.class));
        usuario.setStatus(StatusUsuario.valueOf(resultSet.getString("DS_STATUS")));
        return usuario;
    }

    //Usa a conexao da transacao (usuario + carteira sao gravados juntos)
    public void cadastrar(Connection conexao, Usuario usuario) throws SQLException {
        try (PreparedStatement stmt = conexao.prepareStatement(INSERT_SQL, new String[] {"ID_USUARIO"})) {
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getSenha());
            stmt.setString(4, usuario.getCpf());
            stmt.setString(5, usuario.getTelefone());
            stmt.setObject(6, usuario.getDataNascimento());
            stmt.setString(7, usuario.getCep());
            stmt.setString(8, usuario.getLogradouro());
            stmt.setString(9, usuario.getCidade());
            stmt.setString(10, usuario.getUf());
            stmt.setString(11, usuario.getChavePix());
            stmt.setString(12, usuario.getPerfil().name());
            if (usuario.getEmpresa() == null) {
                stmt.setNull(13, Types.NUMERIC);
            } else {
                stmt.setInt(13, usuario.getEmpresa().getId());
            }
            stmt.setInt(14, usuario.getNivel().getId());
            stmt.setObject(15, usuario.getDataCadastro());
            stmt.setString(16, usuario.getStatus().name());
            stmt.executeUpdate();
            ResultSet resultSet = stmt.getGeneratedKeys();
            if (resultSet.next()) {
                usuario.setId(resultSet.getInt(1));
            }
        }
    }

    //Atualiza os dados cadastrais (o CPF nao muda)
    public void atualizar(Usuario usuario) throws SQLException, EntidadeNaoEncontradaException {
        try (Connection conexao = connectionFactory.getConnection(); PreparedStatement stmt = conexao.prepareStatement(UPDATE_SQL)) {
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getSenha());
            stmt.setString(4, usuario.getTelefone());
            stmt.setObject(5, usuario.getDataNascimento());
            stmt.setString(6, usuario.getCep());
            stmt.setString(7, usuario.getLogradouro());
            stmt.setString(8, usuario.getCidade());
            stmt.setString(9, usuario.getUf());
            stmt.setString(10, usuario.getChavePix());
            stmt.setInt(11, usuario.getId());
            if (stmt.executeUpdate() == 0) {
                throw new EntidadeNaoEncontradaException("Usuario nao encontrado para atualizacao!");
            }
        }
    }

    //Grava pontos, saldo, missoes e nivel (usa a conexao da transacao)
    public void atualizarPontuacao(Connection conexao, Usuario usuario) throws SQLException {
        try (PreparedStatement stmt = conexao.prepareStatement(UPDATE_PONTUACAO_SQL)) {
            stmt.setLong(1, usuario.getPontosTotal());
            stmt.setLong(2, usuario.getPontosSaldo());
            stmt.setInt(3, usuario.getMissoesConcluidas());
            stmt.setInt(4, usuario.getNivel().getId());
            stmt.setInt(5, usuario.getId());
            stmt.executeUpdate();
        }
    }

    //Grava tentativas de login e status (bloqueio/desbloqueio/inativacao)
    public void atualizarAcesso(Usuario usuario) throws SQLException {
        try (Connection conexao = connectionFactory.getConnection(); PreparedStatement stmt = conexao.prepareStatement(UPDATE_ACESSO_SQL)) {
            stmt.setInt(1, usuario.getTentativasLogin());
            stmt.setString(2, usuario.getStatus().name());
            stmt.setInt(3, usuario.getId());
            stmt.executeUpdate();
        }
    }

    public Usuario buscarPorId(int id) throws SQLException, EntidadeNaoEncontradaException {
        try (Connection conexao = connectionFactory.getConnection()) {
            return buscar(conexao, SELECT_SQL + "WHERE u.ID_USUARIO = ?", id);
        }
    }

    //Busca bloqueando a linha (FOR UPDATE) para alterar pontos/saldo sem conflito entre requisicoes
    public Usuario buscarPorIdParaAtualizar(Connection conexao, int id) throws SQLException, EntidadeNaoEncontradaException {
        return buscar(conexao, SELECT_SQL + "WHERE u.ID_USUARIO = ? FOR UPDATE OF u.NR_PONTOS_SALDO", id);
    }

    public Usuario buscarPorEmail(String email) throws SQLException, EntidadeNaoEncontradaException {
        try (Connection conexao = connectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(SELECT_SQL + "WHERE UPPER(u.DS_EMAIL) = UPPER(?)")) {
            stmt.setString(1, email.trim());
            ResultSet resultSet = stmt.executeQuery();
            if (!resultSet.next()) {
                throw new EntidadeNaoEncontradaException("Usuario nao encontrado.");
            }
            return getUsuario(resultSet);
        }
    }

    //Filtros opcionais: perfil e status (nulo = sem filtro)
    public List<Usuario> listar(Perfil perfil, StatusUsuario status) throws SQLException {
        String sql = SELECT_SQL + "WHERE (? IS NULL OR u.DS_PERFIL = ?) AND (? IS NULL OR u.DS_STATUS = ?) ORDER BY u.NM_USUARIO";
        try (Connection conexao = connectionFactory.getConnection(); PreparedStatement stmt = conexao.prepareStatement(sql)) {
            String p = perfil == null ? null : perfil.name();
            String s = status == null ? null : status.name();
            stmt.setString(1, p);
            stmt.setString(2, p);
            stmt.setString(3, s);
            stmt.setString(4, s);
            ResultSet resultSet = stmt.executeQuery();
            List<Usuario> lista = new ArrayList<>();
            while (resultSet.next()) {
                lista.add(getUsuario(resultSet));
            }
            return lista;
        }
    }

    //idIgnorado: na edicao, ignora o proprio usuario (use 0 no cadastro)
    public boolean existeEmail(String email, int idIgnorado) throws SQLException {
        try (Connection conexao = connectionFactory.getConnection(); PreparedStatement stmt = conexao.prepareStatement(EXISTE_EMAIL)) {
            stmt.setString(1, email.trim());
            stmt.setInt(2, idIgnorado);
            ResultSet resultSet = stmt.executeQuery();
            return resultSet.next() && resultSet.getInt(1) > 0;
        }
    }

    public boolean existeCpf(String cpf) throws SQLException {
        try (Connection conexao = connectionFactory.getConnection(); PreparedStatement stmt = conexao.prepareStatement(EXISTE_CPF)) {
            stmt.setString(1, cpf);
            ResultSet resultSet = stmt.executeQuery();
            return resultSet.next() && resultSet.getInt(1) > 0;
        }
    }

    private Usuario buscar(Connection conexao, String sql, int id) throws SQLException, EntidadeNaoEncontradaException {
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet resultSet = stmt.executeQuery();
            if (!resultSet.next()) {
                throw new EntidadeNaoEncontradaException("Usuario " + id + " nao encontrado.");
            }
            return getUsuario(resultSet);
        }
    }
}
