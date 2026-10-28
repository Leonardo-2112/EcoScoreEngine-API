package br.com.soulprime.bo;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.soulprime.dao.RelatorioDao;
import br.com.soulprime.dto.CampanhaIndicadoresDto;
import br.com.soulprime.dto.DashboardResponseDto;
import br.com.soulprime.dto.RankingItemResponseDto;
import br.com.soulprime.model.Usuario;
import br.com.soulprime.model.enums.Perfil;

//Ranking publico dos participantes e Dashboard ESG agregado das empresas (RN17)
@Service
public class RelatorioBo {

    private static final int LIMITE_PADRAO = 10;
    private static final int LIMITE_MAXIMO = 100;

    private final RelatorioDao relatorioDao;
    private final AcessoBo acessoBo;

    public RelatorioBo(RelatorioDao relatorioDao, AcessoBo acessoBo) {
        this.relatorioDao = relatorioDao;
        this.acessoBo = acessoBo;
    }

    public List<RankingItemResponseDto> ranking(Integer limite) throws SQLException {
        int quantidade = LIMITE_PADRAO;
        if (limite != null) {
            quantidade = Math.max(1, Math.min(limite, LIMITE_MAXIMO));
        }
        return relatorioDao.ranking(quantidade);
    }

    //EMPRESA: somente as proprias campanhas. VALIDADOR: todas
    public DashboardResponseDto dashboard(Integer usuarioLogado) throws SQLException {
        Usuario usuario = acessoBo.exigirPerfil(usuarioLogado, Perfil.EMPRESA, Perfil.VALIDADOR);
        Integer empresaId = usuario.getPerfil() == Perfil.EMPRESA ? Integer.valueOf(usuario.getEmpresa().getId()) : null;
        List<CampanhaIndicadoresDto> campanhas = relatorioDao.indicadoresCampanhas(empresaId);

        long acoes = 0;
        long pontos = 0;
        BigDecimal co2 = BigDecimal.ZERO;
        for (CampanhaIndicadoresDto campanha : campanhas) {
            acoes += campanha.getAcoesCreditadas();
            pontos += campanha.getPontos();
            co2 = co2.add(campanha.getCo2EvitadoKg());
        }
        DashboardResponseDto dashboard = new DashboardResponseDto();
        dashboard.setTotalCampanhas(campanhas.size());
        dashboard.setAcoesCreditadas(acoes);
        dashboard.setPontos(pontos);
        dashboard.setCo2EvitadoKg(co2);
        dashboard.setCampanhas(campanhas);
        return dashboard;
    }
}
