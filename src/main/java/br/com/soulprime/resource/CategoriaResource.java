package br.com.soulprime.resource;

import java.net.URI;
import java.sql.SQLException;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.soulprime.bo.AcessoBo;
import br.com.soulprime.bo.CategoriaBo;
import br.com.soulprime.dto.CategoriaRequestDto;
import br.com.soulprime.dto.CategoriaResponseDto;
import br.com.soulprime.exception.EntidadeNaoEncontradaException;
import br.com.soulprime.model.CategoriaAcao;
import jakarta.validation.Valid;

//Categorias de acao: leitura publica, escrita pelo VALIDADOR
@RestController
@RequestMapping("/categorias")
public class CategoriaResource {

    private final CategoriaBo categoriaBo;
    private final ModelMapper mapper;

    public CategoriaResource(CategoriaBo categoriaBo, ModelMapper mapper) {
        this.categoriaBo = categoriaBo;
        this.mapper = mapper;
    }

    @GetMapping
    public List<CategoriaResponseDto> listar() throws SQLException {
        return categoriaBo.listar()
                .stream()
                .map(categoria -> mapper.map(categoria, CategoriaResponseDto.class)).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponseDto> buscarPorId(@PathVariable int id) throws SQLException, EntidadeNaoEncontradaException {
        return ResponseEntity.ok(mapper.map(categoriaBo.buscar(id), CategoriaResponseDto.class));
    }

    @PostMapping
    public ResponseEntity<CategoriaResponseDto> cadastrar(@RequestHeader(value = AcessoBo.HEADER, required = false) Integer usuarioLogado, @Valid @RequestBody CategoriaRequestDto dto, UriComponentsBuilder builder)
            throws SQLException {
        CategoriaAcao categoria = categoriaBo.cadastrar(usuarioLogado, mapper.map(dto, CategoriaAcao.class));
        URI uri = builder.path("/categorias/{id}").buildAndExpand(categoria.getId()).toUri();
        return ResponseEntity.created(uri).body(mapper.map(categoria, CategoriaResponseDto.class));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponseDto> atualizar(@RequestHeader(value = AcessoBo.HEADER, required = false) Integer usuarioLogado, @PathVariable int id, @Valid @RequestBody CategoriaRequestDto dto)
            throws SQLException, EntidadeNaoEncontradaException {
        CategoriaAcao categoria = mapper.map(dto, CategoriaAcao.class);
        categoria.setId(id);
        return ResponseEntity.ok(mapper.map(categoriaBo.atualizar(usuarioLogado, categoria), CategoriaResponseDto.class));
    }

    //204 excluida | 409 possui acoes vinculadas
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@RequestHeader(value = AcessoBo.HEADER, required = false) Integer usuarioLogado, @PathVariable int id) throws SQLException, EntidadeNaoEncontradaException {
        categoriaBo.remover(usuarioLogado, id);
        return ResponseEntity.noContent().build();
    }
}
