package br.com.soulprime.resource;

import java.net.URI;
import java.sql.SQLException;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.soulprime.bo.AcessoBo;
import br.com.soulprime.bo.MissaoBo;
import br.com.soulprime.dto.AcaoResumoDto;
import br.com.soulprime.dto.MissaoRequestDto;
import br.com.soulprime.dto.MissaoResponseDto;
import br.com.soulprime.exception.EntidadeNaoEncontradaException;
import br.com.soulprime.model.Missao;
import jakarta.validation.Valid;

//Desafios tematicos: leitura publica, escrita pelo VALIDADOR
@RestController
@RequestMapping("/missoes")
public class MissaoResource {

    private final MissaoBo missaoBo;
    private final ModelMapper mapper;

    public MissaoResource(MissaoBo missaoBo, ModelMapper mapper) {
        this.missaoBo = missaoBo;
        this.mapper = mapper;
    }

    //Converte o desafio com a lista de acoes que o compoem
    private MissaoResponseDto toDto(Missao missao) {
        MissaoResponseDto dto = mapper.map(missao, MissaoResponseDto.class);
        dto.setAcoes(missao.getAcoes().stream().map(missaoAcao -> mapper.map(missaoAcao.getAcao(), AcaoResumoDto.class)).toList());
        return dto;
    }

    //?status=ATIVA (padrao) | INATIVA | TODAS
    @GetMapping
    public List<MissaoResponseDto> listar(@RequestParam(required = false) String status) throws SQLException {
        return missaoBo.listar(status).stream().map(this::toDto).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MissaoResponseDto> buscarPorId(@PathVariable int id) throws SQLException, EntidadeNaoEncontradaException {
        return ResponseEntity.ok(toDto(missaoBo.buscar(id)));
    }

    @PostMapping
    public ResponseEntity<MissaoResponseDto> cadastrar(@RequestHeader(value = AcessoBo.HEADER, required = false) Integer usuarioLogado, @Valid @RequestBody MissaoRequestDto dto, UriComponentsBuilder builder)
            throws SQLException, EntidadeNaoEncontradaException {
        Missao missao = missaoBo.cadastrar(usuarioLogado, dto);
        URI uri = builder.path("/missoes/{id}").buildAndExpand(missao.getId()).toUri();
        return ResponseEntity.created(uri).body(toDto(missao));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MissaoResponseDto> atualizar(@RequestHeader(value = AcessoBo.HEADER, required = false) Integer usuarioLogado, @PathVariable int id, @Valid @RequestBody MissaoRequestDto dto)
            throws SQLException, EntidadeNaoEncontradaException {
        return ResponseEntity.ok(toDto(missaoBo.atualizar(usuarioLogado, id, dto)));
    }

    //Exclusao logica (status INATIVA)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@RequestHeader(value = AcessoBo.HEADER, required = false) Integer usuarioLogado, @PathVariable int id) throws SQLException, EntidadeNaoEncontradaException {
        missaoBo.inativar(usuarioLogado, id);
        return ResponseEntity.noContent().build();
    }
}
