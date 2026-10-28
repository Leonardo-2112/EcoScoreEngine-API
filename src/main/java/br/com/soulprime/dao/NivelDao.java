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
import br.com.soulprime.model.Nivel;

//DAO da tabela TB_NIVEL (tabela de dominio: somente leitura)
@Repository
public class NivelDao {

    private static final String SELECT_SQL = "SELECT * FROM TB_NIVEL ORDER BY NR_MISSOES_MIN";
    private static final String SELECT_POR_MISSOES = "SELECT * FROM TB_NIVEL WHERE ? >= NR_MISSOES_MIN AND (NR_MISSOES_MAX IS NULL OR ? <= NR_MISSOES_MAX)";
    private static final String SELECT_PROXIMO = "SELECT * FROM TB_NIVEL WHERE NR_MISSOES_MIN > ? ORDER BY NR_MISSOES_MIN FETCH FIRST 1 ROWS ONLY";

    private final ConnectionFactory connectionFactory;

    public NivelDao(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    //Monta o objeto Nivel com os dados da linha do banco
    static Nivel getNivel(ResultSet resultSet) throws SQLException {
        Nivel nivel = new Nivel(resultSet.getInt("ID_NIVEL"));
        nivel.setDescricao(resultSet.getString("DS_NIVEL"));
        nivel.setMissoesMinimas(resultSet.getInt("NR_MISSOES_MIN"));
        nivel.setMissoesMaximas(resultSet.getObject("NR_MISSOES_MAX", Integer.class));
        nivel.setBonusPontos(resultSet.getBigDecimal("NR_BONUS_PONTOS"));
        return nivel;
    }

    public List<Nivel> listar() throws SQLException {
        try (Connection conexao = connectionFactory.getConnection(); PreparedStatement stmt = conexao.prepareStatement(SELECT_SQL)) {
            ResultSet resultSet = stmt.executeQuery();
            List<Nivel> lista = new ArrayList<>();
            while (resultSet.next()) {
                lista.add(getNivel(resultSet));
            }
            return lista;
        }
    }

    //Nivel cuja faixa contem a quantidade de missoes concluidas
    public Nivel buscarPorMissoes(Connection conexao, int missoes) throws SQLException, EntidadeNaoEncontradaException {
        try (PreparedStatement stmt = conexao.prepareStatement(SELECT_POR_MISSOES)) {
            stmt.setInt(1, missoes);
            stmt.setInt(2, missoes);
            ResultSet resultSet = stmt.executeQuery();
            if (!resultSet.next()) {
                throw new EntidadeNaoEncontradaException("Nivel nao encontrado para " + missoes + " missoes.");
            }
            return getNivel(resultSet);
        }
    }

    //Proximo nivel acima do atual (retorna null no nivel maximo)
    public Nivel buscarProximo(Nivel atual) throws SQLException {
        try (Connection conexao = connectionFactory.getConnection(); PreparedStatement stmt = conexao.prepareStatement(SELECT_PROXIMO)) {
            stmt.setInt(1, atual.getMissoesMinimas());
            ResultSet resultSet = stmt.executeQuery();
            if (!resultSet.next()) {
                return null;
            }
            return getNivel(resultSet);
        }
    }
}
