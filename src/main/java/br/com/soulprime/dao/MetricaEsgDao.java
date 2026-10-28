package br.com.soulprime.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import br.com.soulprime.factory.ConnectionFactory;
import br.com.soulprime.model.CampanhaEsg;
import br.com.soulprime.model.MetricaEsg;

//DAO da tabela TB_METRICA_ESG (indicadores agregados por campanha)
@Repository
public class MetricaEsgDao {

    private static final String SELECT_POR_CAMPANHA = "SELECT * FROM TB_METRICA_ESG WHERE ID_CAMPANHA = ? ORDER BY DT_REFERENCIA";

    private final ConnectionFactory connectionFactory;

    public MetricaEsgDao(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    private MetricaEsg getMetrica(ResultSet resultSet, CampanhaEsg campanha) throws SQLException {
        MetricaEsg metrica = new MetricaEsg();
        metrica.setId(resultSet.getInt("ID_METRICA"));
        metrica.setCampanha(campanha);
        metrica.setDataReferencia(resultSet.getObject("DT_REFERENCIA", LocalDate.class));
        metrica.setUsuariosAtivos(resultSet.getLong("QT_USUARIOS_ATIVOS"));
        metrica.setAcoesRealizadas(resultSet.getLong("QT_ACOES_REALIZADAS"));
        metrica.setPontosTotais(resultSet.getLong("NR_PONTOS_TOTAIS"));
        metrica.setCreditosGerados(resultSet.getBigDecimal("NR_CREDITOS_GERADOS"));
        metrica.setCo2Estimado(resultSet.getBigDecimal("NR_CO2_ESTIMADO"));
        metrica.setDataGeracao(resultSet.getObject("DT_GERACAO", LocalDateTime.class));
        return metrica;
    }

    public List<MetricaEsg> listarPorCampanha(CampanhaEsg campanha) throws SQLException {
        try (Connection conexao = connectionFactory.getConnection(); PreparedStatement stmt = conexao.prepareStatement(SELECT_POR_CAMPANHA)) {
            stmt.setInt(1, campanha.getId());
            ResultSet resultSet = stmt.executeQuery();
            List<MetricaEsg> lista = new ArrayList<>();
            while (resultSet.next()) {
                lista.add(getMetrica(resultSet, campanha));
            }
            return lista;
        }
    }
}
