package br.com.soulprime.resource;

import java.sql.SQLException;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.soulprime.bo.AcessoBo;
import br.com.soulprime.bo.RegistroAcaoBo;
import br.com.soulprime.bo.UsuarioBo;
import br.com.soulprime.dto.AtualizarUsuarioRequestDto;
import br.com.soulprime.dto.PainelResponseDto;
import br.com.soulprime.dto.RegistroAcaoResponseDto;
import br.com.soulprime.dto.UsuarioMissaoResponseDto;
import br.com.soulprime.dto.UsuarioResponseDto;
import br.com.soulprime.exception.EntidadeNaoEncontradaException;
import jakarta.validation.Valid;

//Usuarios: consulta, edicao, exclusao logica, desbloqueio, painel, desafios e historico de registros
@RestController
@RequestMapping("/usuarios")
public class UsuarioResource {

    private final UsuarioBo usuarioBo;
    private final RegistroAcaoBo registroAcaoBo;
    private final ModelMapper mapper;

    public UsuarioResource(UsuarioBo usuarioBo, RegistroAcaoBo registroAcaoBo, ModelMapper mapper) {
        this.usuarioBo = usuarioBo;
        this.registroAcaoBo = registroAcaoBo;
        this.mapper = mapper;
    }

    //VALIDADOR: lista com filtros ?perfil=PARTICIPANTE&status=ATIVO
    @GetMapping
    public List<UsuarioResponseDto> listar(@RequestHeader(value = AcessoBo.HEADER, required = false) Integer usuarioLogado, @RequestParam(required = false) String perfil,
                                           @RequestParam(required = false) String status) throws SQLException {
        return usuarioBo.listar(usuarioLogado, perfil, status)
                .stream()
                .map(usuario -> mapper.map(usuario, UsuarioResponseDto.class)).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDto> buscarPorId(@RequestHeader(value = AcessoBo.HEADER, required = false) Integer usuarioLogado, @PathVariable int id) throws SQLException, EntidadeNaoEncontradaException {
        return ResponseEntity.ok(mapper.map(usuarioBo.buscar(usuarioLogado, id), UsuarioResponseDto.class));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDto> atualizar(@RequestHeader(value = AcessoBo.HEADER, required = false) Integer usuarioLogado, @PathVariable int id, @Valid @RequestBody AtualizarUsuarioRequestDto dto)
            throws SQLException, EntidadeNaoEncontradaException {
        return ResponseEntity.ok(mapper.map(usuarioBo.atualizar(usuarioLogado, id, dto), UsuarioResponseDto.class));
    }

    //Exclusao logica (status INATIVO)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@RequestHeader(value = AcessoBo.HEADER, required = false) Integer usuarioLogado, @PathVariable int id) throws SQLException, EntidadeNaoEncontradaException {
        usuarioBo.inativar(usuarioLogado, id);
        return ResponseEntity.noContent().build();
    }

    //VALIDADOR: desbloqueia conta SUSPENSA apos 5 tentativas de login (RN02)
    @PatchMapping("/{id}/desbloqueio")
    public ResponseEntity<UsuarioResponseDto> desbloquear(@RequestHeader(value = AcessoBo.HEADER, required = false) Integer usuarioLogado, @PathVariable int id) throws SQLException, EntidadeNaoEncontradaException {
        return ResponseEntity.ok(mapper.map(usuarioBo.desbloquear(usuarioLogado, id), UsuarioResponseDto.class));
    }

    @GetMapping("/{id}/painel")
    public ResponseEntity<PainelResponseDto> painel(@RequestHeader(value = AcessoBo.HEADER, required = false) Integer usuarioLogado, @PathVariable int id) throws SQLException, EntidadeNaoEncontradaException {
        return ResponseEntity.ok(usuarioBo.painel(usuarioLogado, id));
    }

    @GetMapping("/{id}/desafios")
    public List<UsuarioMissaoResponseDto> desafios(@RequestHeader(value = AcessoBo.HEADER, required = false) Integer usuarioLogado, @PathVariable int id) throws SQLException, EntidadeNaoEncontradaException {
        return usuarioBo.desafios(usuarioLogado, id)
                .stream()
                .map(desafio -> mapper.map(desafio, UsuarioMissaoResponseDto.class)).toList();
    }

    @GetMapping("/{id}/registros")
    public List<RegistroAcaoResponseDto> registros(@RequestHeader(value = AcessoBo.HEADER, required = false) Integer usuarioLogado, @PathVariable int id, @RequestParam(required = false) String status)
            throws SQLException {
        return registroAcaoBo.listarDoUsuario(usuarioLogado, id, status)
                .stream()
                .map(registro -> mapper.map(registro, RegistroAcaoResponseDto.class)).toList();
    }
}
