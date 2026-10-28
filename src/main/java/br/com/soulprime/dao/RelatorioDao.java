package br.com.soulprime.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import br.com.soulprime.dto.CampanhaIndicadoresDto;
import br.com.soulprime.dto.RankingItemResponseDto;
import br.com.soulprime.factory.ConnectionFactory;
import br.com.soulprime.model.enums.StatusCampanha;

//Consultas agregadas: ranking (consulta C01 de Banco de Dados) e Dashboard ESG (consulta C05)
@Repository
public class RelatorioDao {

    private static final String RANKING_SQL = "SELECT ROW_NUMBER() OVER (ORDER BY u.NR_PONTOS_TOTAL DESC, u.QT_MISSOES_CONCLUIDAS DESC, u.DT_CADASTRO) AS POSICAO, "
            + "u.ID_USUARIO, u.NM_USUARIO, n.DS_NIVEL, u.QT_MISSOES_CONCLUIDAS, u.NR_PONTOS_TOTAL, COUNT(r.ID_REGISTRO) AS ACOES_CREDITADAS, "
            + "NVL(SUM(a.NR_CO2_POR_UNIDADE), 0) AS CO2_EVITADO_KG "
            + "FROM TB_USUARIO u JOIN TB_NIVEL n ON n.ID_NIVEL = u.ID_NIVEL "
            + "LEFT JOIN TB_REGISTRO_ACAO r ON r.ID_USUARIO = u.ID_USUARIO AND r.DS_VALIDACAO IN ('VALIDADA', 'PARCIAL') "
            + "LEFT JOIN TB_ACAO a ON a.ID_ACAO = r.ID_ACAO "
            + "WHERE u.DS_PERFIL = 'PARTICIPANTE' AND u.DS_STATUS = 'ATIVO' "
            + "GROUP BY u.ID_USUARIO, u.NM_USUARIO, n.DS_NIVEL, u.QT_MISSOES_CONCLUIDAS, u.NR_PONTOS_TOTAL, u.DT_CADASTRO";
    private static final String INDICADORES_SQL = "SELECT c.ID_CAMPANHA, c.NM_CAMPANHA, e.ID_EMPRESA, e.NM_EMPRESA, c.DS_STATUS, c.DT_INICIO, c.DT_FIM, "
            + "COUNT(DISTINCT r.ID_USUARIO) AS PARTICIPANTES, COUNT(r.ID_REGISTRO) AS ACOES, NVL(SUM(r.NR_PONTOS_GERADOS), 0) AS PONTOS, "
            + "NVL(ROUND(AVG(r.NR_PONTOS_GERADOS), 1), 0) AS MEDIA, NVL(SUM(a.NR_CO2_POR_UNIDADE), 0) AS CO2 "
            + "FROM TB_CAMPANHA_ESG c JOIN TB_EMPRESA_PARCEIRA e ON e.ID_EMPRESA = c.ID_EMPRESA "
            + "LEFT JOIN TB_REGISTRO_ACAO r ON r.ID_CAMPANHA = c.ID_CAMPANHA AND r.DS_VALIDACAO IN ('VALIDADA', 'PARCIAL') "
            + "LEFT JOIN TB_ACAO a ON a.ID_ACAO = r.ID_ACAO "
            + "WHERE (? IS NULL OR c.ID_EMPRESA = ?) "
            + "GROUP BY c.ID_CAMPANHA, c.NM_CAMPANHA, e.ID_EMPRESA, e.NM_EMPRESA, c.DS_STATUS, c.DT_INICIO, c.DT_FIM ORDER BY c.DT_INICIO DESC";

    private final ConnectionFactory connectionFactory;

    public RelatorioDao(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public List<RankingItemResponseDto> ranking(int limite) throws SQLException {
        try (Connection conexao = connectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(RANKING_SQL + " ORDER BY POSICAO FETCH FIRST ? ROWS ONLY")) {
            stmt.setInt(1, limite);
            ResultSet resultSet = stmt.executeQuery();
            List<RankingItemResponseDto> lista = new ArrayList<>();
            while (resultSet.next()) {
                RankingItemResponseDto item = new RankingItemResponseDto();
                item.setPosicao(resultSet.getInt("POSICAO"));
                item.setUsuarioId(resultSet.getInt("ID_USUARIO"));
                item.setNome(resultSet.getString("NM_USUARIO"));
                item.setNivel(resultSet.getString("DS_NIVEL"));
                item.setMissoesConcluidas(resultSet.getInt("QT_MISSOES_CONCLUIDAS"));
                item.setPontosAcumulados(resultSet.getLong("NR_PONTOS_TOTAL"));
                item.setAcoesCreditadas(resultSet.getLong("ACOES_CREDITADAS"));
                item.setCo2EvitadoKg(resultSet.getBigDecimal("CO2_EVITADO_KG"));
                lista.add(item);
            }
            return lista;
        }
    }

    //Posicao do participante no ranking (null se nao estiver no ranking)
    public Integer posicaoNoRanking(int usuarioId) throws SQLException {
        try (Connection conexao = connectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement("SELECT POSICAO FROM (" + RANKING_SQL + ") WHERE ID_USUARIO = ?")) {
            stmt.setInt(1, usuarioId);
            ResultSet resultSet = stmt.executeQuery();
            if (!resultSet.next()) {
                return null;
            }
            return resultSet.getInt(1);
        }
    }

    //Indicadores agregados por campanha, sem dados pessoais (RN17). empresaId nulo = todas as campanhas
    public List<CampanhaIndicadoresDto> indicadoresCampanhas(Integer empresaId) throws SQLException {
        try (Connection conexao = connectionFactory.getConnection(); PreparedStatement stmt = conexao.prepareStatement(INDICADORES_SQL)) {
            if (empresaId == null) {
                stmt.setNull(1, Types.NUMERIC);
                stmt.setNull(2, Types.NUMERIC);
            } else {
                stmt.setInt(1, empresaId);
                stmt.setInt(2, empresaId);
            }
            ResultSet resultSet = stmt.executeQuery();
            List<CampanhaIndicadoresDto> lista = new ArrayList<>();
            while (resultSet.next()) {
                CampanhaIndicadoresDto dto = new CampanhaIndicadoresDto();
                dto.setCampanhaId(resultSet.getInt("ID_CAMPANHA"));
                dto.setCampanha(resultSet.getString("NM_CAMPANHA"));
                dto.setEmpresaId(resultSet.getInt("ID_EMPRESA"));
                dto.setEmpresa(resultSet.getString("NM_EMPRESA"));
                dto.setStatus(StatusCampanha.valueOf(resultSet.getString("DS_STATUS")));
                dto.setDataInicio(resultSet.getObject("DT_INICIO", LocalDate.class));
                dto.setDataFim(resultSet.getObject("DT_FIM", LocalDate.class));
                dto.setParticipantes(resultSet.getLong("PARTICIPANTES"));
                dto.setAcoesCreditadas(resultSet.getLong("ACOES"));
                dto.setPontos(resultSet.getLong("PONTOS"));
                dto.setMediaPontos(resultSet.getBigDecimal("MEDIA"));
                dto.setCo2EvitadoKg(resultSet.getBigDecimal("CO2"));
                lista.add(dto);
            }
            return lista;
        }
    }
}
