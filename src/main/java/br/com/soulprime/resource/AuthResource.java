package br.com.soulprime.resource;

import java.net.URI;
import java.sql.SQLException;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.soulprime.bo.AuthBo;
import br.com.soulprime.dto.CadastroUsuarioRequestDto;
import br.com.soulprime.dto.LoginRequestDto;
import br.com.soulprime.dto.UsuarioResponseDto;
import br.com.soulprime.exception.EntidadeNaoEncontradaException;
import br.com.soulprime.model.Usuario;
import jakarta.validation.Valid;

//Autenticacao: login e cadastro publico de participantes
@RestController
@RequestMapping("/auth")
public class AuthResource {

    private final AuthBo authBo;
    private final ModelMapper mapper;

    public AuthResource(AuthBo authBo, ModelMapper mapper) {
        this.authBo = authBo;
        this.mapper = mapper;
    }

    //200 usuario | 400 dados invalidos | 401 e-mail ou senha invalidos | 403 conta inativa | 423 conta bloqueada
    @PostMapping("/login")
    public ResponseEntity<UsuarioResponseDto> login(@Valid @RequestBody LoginRequestDto dto) throws SQLException {
        Usuario usuario = authBo.login(dto.getEmail(), dto.getSenha());
        return ResponseEntity.ok(mapper.map(usuario, UsuarioResponseDto.class));
    }

    //201 participante criado | 400 dados invalidos | 409 e-mail ou CPF ja cadastrado
    @PostMapping("/cadastro")
    public ResponseEntity<UsuarioResponseDto> cadastrar(@Valid @RequestBody CadastroUsuarioRequestDto dto, UriComponentsBuilder builder)
            throws SQLException, EntidadeNaoEncontradaException {
        Usuario usuario = authBo.cadastrarParticipante(mapper.map(dto, Usuario.class));
        URI uri = builder.path("/usuarios/{id}").buildAndExpand(usuario.getId()).toUri();
        return ResponseEntity.created(uri).body(mapper.map(usuario, UsuarioResponseDto.class));
    }
}
