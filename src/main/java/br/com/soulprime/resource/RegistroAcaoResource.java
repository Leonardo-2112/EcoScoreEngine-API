package br.com.soulprime.resource;

import java.net.URI;
import java.sql.SQLException;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.soulprime.bo.AcessoBo;
import br.com.soulprime.bo.RegistroAcaoBo;
import br.com.soulprime.dto.RegistroAcaoRequestDto;
import br.com.soulprime.dto.RegistroAcaoResponseDto;
import br.com.soulprime.dto.ValidacaoRequestDto;
import br.com.soulprime.exception.EntidadeNaoEncontradaException;
import br.com.soulprime.model.RegistroAcao;
import jakarta.validation.Valid;

//Registro de acoes sustentaveis (PARTICIPANTE) e fila de validacao (VALIDADOR)
@RestController
@RequestMapping("/registros")
public class RegistroAcaoResource {

    private final RegistroAcaoBo registroAcaoBo;
    private final ModelMapper mapper;

    public RegistroAcaoResource(RegistroAcaoBo registroAcaoBo, ModelMapper mapper) {
        this.registroAcaoBo = registroAcaoBo;
        this.mapper = mapper;
    }

    //201 registrado | 400 dados invalidos | 404 acao inexistente | 422 limite diario ou acao inativa
    @PostMapping
    public ResponseEntity<RegistroAcaoResponseDto> registrar(@RequestHeader(value = AcessoBo.HEADER, required = false) Integer usuarioLogado, @Valid @RequestBody RegistroAcaoRequestDto dto, UriComponentsBuilder builder)
            throws SQLException, EntidadeNaoEncontradaException {
        RegistroAcao registro = registroAcaoBo.registrar(usuarioLogado, dto);
        URI uri = builder.path("/registros/{id}").buildAndExpand(registro.getId()).toUri();
        return ResponseEntity.created(uri).body(mapper.map(registro, RegistroAcaoResponseDto.class));
    }

    //VALIDADOR: ?status=PENDENTE (fila de validacao) e ?usuarioId=; PARTICIPANTE: somente os proprios
    @GetMapping
    public List<RegistroAcaoResponseDto> listar(@RequestHeader(value = AcessoBo.HEADER, required = false) Integer usuarioLogado, @RequestParam(required = false) String status,
                                                @RequestParam(required = false) Integer usuarioId) throws SQLException {
        return registroAcaoBo.listar(usuarioLogado, status, usuarioId)
                .stream()
                .map(registro -> mapper.map(registro, RegistroAcaoResponseDto.class)).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegistroAcaoResponseDto> buscarPorId(@RequestHeader(value = AcessoBo.HEADER, required = false) Integer usuarioLogado, @PathVariable int id) throws SQLException, EntidadeNaoEncontradaException {
        return ResponseEntity.ok(mapper.map(registroAcaoBo.buscar(usuarioLogado, id), RegistroAcaoResponseDto.class));
    }

    //VALIDADOR: aprova (credita os pontos) ou nega um registro PENDENTE
    @PatchMapping("/{id}/validacao")
    public ResponseEntity<RegistroAcaoResponseDto> validar(@RequestHeader(value = AcessoBo.HEADER, required = false) Integer usuarioLogado, @PathVariable int id, @Valid @RequestBody ValidacaoRequestDto dto)
            throws SQLException, EntidadeNaoEncontradaException {
        return ResponseEntity.ok(mapper.map(registroAcaoBo.validar(usuarioLogado, id, dto), RegistroAcaoResponseDto.class));
    }
}
