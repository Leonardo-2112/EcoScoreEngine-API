package br.com.soulprime.resource;

import java.net.URI;
import java.sql.SQLException;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.soulprime.bo.AcaoBo;
import br.com.soulprime.bo.AcessoBo;
import br.com.soulprime.dto.AcaoRequestDto;
import br.com.soulprime.dto.AcaoResponseDto;
import br.com.soulprime.exception.EntidadeNaoEncontradaException;
import br.com.soulprime.model.Acao;
import jakarta.validation.Valid;

//Catalogo de acoes sustentaveis: leitura publica, escrita pelo VALIDADOR
@RestController
@RequestMapping("/acoes")
public class AcaoResource {

    private final AcaoBo acaoBo;
    private final ModelMapper mapper;

    public AcaoResource(AcaoBo acaoBo, ModelMapper mapper) {
        this.acaoBo = acaoBo;
        this.mapper = mapper;
    }

    //?status=ATIVA (padrao) | INATIVA | TODAS e ?categoriaId=
    @GetMapping
    public List<AcaoResponseDto> listar(@RequestParam(required = false) String status,
                                        @RequestParam(required = false) Integer categoriaId) throws SQLException {
        return acaoBo.listar(status, categoriaId)
                .stream()
                .map(acao -> mapper.map(acao, AcaoResponseDto.class)).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AcaoResponseDto> buscarPorId(@PathVariable int id) throws SQLException, EntidadeNaoEncontradaException {
        return ResponseEntity.ok(mapper.map(acaoBo.buscar(id), AcaoResponseDto.class));
    }

    @PostMapping
    public ResponseEntity<AcaoResponseDto> cadastrar(@RequestHeader(value = AcessoBo.HEADER, required = false) Integer usuarioLogado, @Valid @RequestBody AcaoRequestDto dto, UriComponentsBuilder builder)
            throws SQLException, EntidadeNaoEncontradaException {
        Acao acao = acaoBo.cadastrar(usuarioLogado, dto);
        URI uri = builder.path("/acoes/{id}").buildAndExpand(acao.getId()).toUri();
        return ResponseEntity.created(uri).body(mapper.map(acao, AcaoResponseDto.class));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AcaoResponseDto> atualizar(@RequestHeader(value = AcessoBo.HEADER, required = false) Integer usuarioLogado, @PathVariable int id, @Valid @RequestBody AcaoRequestDto dto)
            throws SQLException, EntidadeNaoEncontradaException {
        return ResponseEntity.ok(mapper.map(acaoBo.atualizar(usuarioLogado, id, dto), AcaoResponseDto.class));
    }

    //Exclusao logica (status INATIVA)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@RequestHeader(value = AcessoBo.HEADER, required = false) Integer usuarioLogado, @PathVariable int id) throws SQLException, EntidadeNaoEncontradaException {
        acaoBo.inativar(usuarioLogado, id);
        return ResponseEntity.noContent().build();
    }
}
