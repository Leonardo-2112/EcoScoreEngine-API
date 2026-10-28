package br.com.soulprime.resource;

import java.net.URI;
import java.sql.SQLException;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.soulprime.bo.AcessoBo;
import br.com.soulprime.bo.CampanhaBo;
import br.com.soulprime.dto.AcaoResumoDto;
import br.com.soulprime.dto.CampanhaRequestDto;
import br.com.soulprime.dto.CampanhaResponseDto;
import br.com.soulprime.dto.MetricaResponseDto;
import br.com.soulprime.dto.StatusRequestDto;
import br.com.soulprime.exception.EntidadeNaoEncontradaException;
import br.com.soulprime.model.CampanhaEsg;
import jakarta.validation.Valid;

//Campanhas ESG: criacao pela EMPRESA (PENDENTE), aprovacao pelo VALIDADOR (RN16) e metricas agregadas
@RestController
@RequestMapping("/campanhas")
public class CampanhaResource {

    private final CampanhaBo campanhaBo;
    private final ModelMapper mapper;

    public CampanhaResource(CampanhaBo campanhaBo, ModelMapper mapper) {
        this.campanhaBo = campanhaBo;
        this.mapper = mapper;
    }

    //Converte a campanha com a lista de acoes patrocinadas
    private CampanhaResponseDto toDto(CampanhaEsg campanha) {
        CampanhaResponseDto dto = mapper.map(campanha, CampanhaResponseDto.class);
        dto.setAcoes(campanha.getAcoes().stream().map(campanhaAcao -> mapper.map(campanhaAcao.getAcao(), AcaoResumoDto.class)).toList());
        return dto;
    }

    //Publico: campanhas ATIVAS. EMPRESA: as proprias. VALIDADOR: todas (?status=&empresaId=)
    @GetMapping
    public List<CampanhaResponseDto> listar(@RequestHeader(value = AcessoBo.HEADER, required = false) Integer usuarioLogado, @RequestParam(required = false) String status,
                                            @RequestParam(required = false) Integer empresaId) throws SQLException {
        return campanhaBo.listar(usuarioLogado, status, empresaId).stream().map(this::toDto).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CampanhaResponseDto> buscarPorId(@RequestHeader(value = AcessoBo.HEADER, required = false) Integer usuarioLogado, @PathVariable int id) throws SQLException, EntidadeNaoEncontradaException {
        return ResponseEntity.ok(toDto(campanhaBo.buscar(usuarioLogado, id)));
    }

    @PostMapping
    public ResponseEntity<CampanhaResponseDto> cadastrar(@RequestHeader(value = AcessoBo.HEADER, required = false) Integer usuarioLogado, @Valid @RequestBody CampanhaRequestDto dto, UriComponentsBuilder builder)
            throws SQLException, EntidadeNaoEncontradaException {
        CampanhaEsg campanha = campanhaBo.cadastrar(usuarioLogado, dto);
        URI uri = builder.path("/campanhas/{id}").buildAndExpand(campanha.getId()).toUri();
        return ResponseEntity.created(uri).body(toDto(campanha));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CampanhaResponseDto> atualizar(@RequestHeader(value = AcessoBo.HEADER, required = false) Integer usuarioLogado, @PathVariable int id, @Valid @RequestBody CampanhaRequestDto dto)
            throws SQLException, EntidadeNaoEncontradaException {
        return ResponseEntity.ok(toDto(campanhaBo.atualizar(usuarioLogado, id, dto)));
    }

    //Cancelamento (exclusao logica: status CANCELADA)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@RequestHeader(value = AcessoBo.HEADER, required = false) Integer usuarioLogado, @PathVariable int id) throws SQLException, EntidadeNaoEncontradaException {
        campanhaBo.cancelar(usuarioLogado, id);
        return ResponseEntity.noContent().build();
    }

    //VALIDADOR: PENDENTE -> ATIVA (aprovar) | CANCELADA; ATIVA -> ENCERRADA | CANCELADA
    @PatchMapping("/{id}/status")
    public ResponseEntity<CampanhaResponseDto> alterarStatus(@RequestHeader(value = AcessoBo.HEADER, required = false) Integer usuarioLogado, @PathVariable int id, @Valid @RequestBody StatusRequestDto dto)
            throws SQLException, EntidadeNaoEncontradaException {
        return ResponseEntity.ok(toDto(campanhaBo.alterarStatus(usuarioLogado, id, dto.getStatus())));
    }

    @GetMapping("/{id}/metricas")
    public List<MetricaResponseDto> metricas(@RequestHeader(value = AcessoBo.HEADER, required = false) Integer usuarioLogado, @PathVariable int id) throws SQLException, EntidadeNaoEncontradaException {
        return campanhaBo.metricas(usuarioLogado, id)
                .stream()
                .map(metrica -> mapper.map(metrica, MetricaResponseDto.class)).toList();
    }
}
