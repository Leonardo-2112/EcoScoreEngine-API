package br.com.soulprime.resource;

import java.sql.SQLException;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.com.soulprime.bo.AcessoBo;
import br.com.soulprime.bo.NivelBo;
import br.com.soulprime.bo.RelatorioBo;
import br.com.soulprime.dto.DashboardResponseDto;
import br.com.soulprime.dto.NivelResponseDto;
import br.com.soulprime.dto.RankingItemResponseDto;

//Consultas de apoio: niveis, ranking publico e Dashboard ESG (dados agregados - RN17)
@RestController
public class RelatorioResource {

    private final RelatorioBo relatorioBo;
    private final NivelBo nivelBo;
    private final ModelMapper mapper;

    public RelatorioResource(RelatorioBo relatorioBo, NivelBo nivelBo, ModelMapper mapper) {
        this.relatorioBo = relatorioBo;
        this.nivelBo = nivelBo;
        this.mapper = mapper;
    }

    @GetMapping("/niveis")
    public List<NivelResponseDto> niveis() throws SQLException {
        return nivelBo.listar()
                .stream()
                .map(nivel -> mapper.map(nivel, NivelResponseDto.class)).toList();
    }

    //Ranking dos participantes ativos pelos pontos acumulados. ?limite=10 (maximo 100)
    @GetMapping("/ranking")
    public List<RankingItemResponseDto> ranking(@RequestParam(required = false) Integer limite) throws SQLException {
        return relatorioBo.ranking(limite);
    }

    //EMPRESA: indicadores das proprias campanhas. VALIDADOR: de todas
    @GetMapping("/dashboard/esg")
    public ResponseEntity<DashboardResponseDto> dashboard(@RequestHeader(value = AcessoBo.HEADER, required = false) Integer usuarioLogado) throws SQLException {
        return ResponseEntity.ok(relatorioBo.dashboard(usuarioLogado));
    }
}
