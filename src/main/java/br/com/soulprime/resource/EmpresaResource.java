package br.com.soulprime.resource;

import java.net.URI;
import java.sql.SQLException;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.soulprime.bo.AcessoBo;
import br.com.soulprime.bo.EmpresaBo;
import br.com.soulprime.dto.CadastroEmpresaRequestDto;
import br.com.soulprime.dto.EmpresaRequestDto;
import br.com.soulprime.dto.EmpresaResponseDto;
import br.com.soulprime.dto.StatusRequestDto;
import br.com.soulprime.exception.EntidadeNaoEncontradaException;
import br.com.soulprime.model.EmpresaParceira;
import br.com.soulprime.model.Usuario;
import jakarta.validation.Valid;

//Empresas parceiras (B2B): cadastro publico (PENDENTE), consulta, edicao e ativacao pelo VALIDADOR
@RestController
@RequestMapping("/empresas")
public class EmpresaResource {

    private final EmpresaBo empresaBo;
    private final ModelMapper mapper;

    public EmpresaResource(EmpresaBo empresaBo, ModelMapper mapper) {
        this.empresaBo = empresaBo;
        this.mapper = mapper;
    }

    //201 empresa PENDENTE + representante | 400 CNPJ ou dados invalidos | 409 CNPJ, e-mail ou CPF ja cadastrado
    @PostMapping
    public ResponseEntity<EmpresaResponseDto> cadastrar(@Valid @RequestBody CadastroEmpresaRequestDto dto, UriComponentsBuilder builder)
            throws SQLException, EntidadeNaoEncontradaException {
        Usuario representante = mapper.map(dto.getRepresentante(), Usuario.class);
        EmpresaParceira empresa = empresaBo.cadastrar(dto.getEmpresa(), representante);
        URI uri = builder.path("/empresas/{id}").buildAndExpand(empresa.getId()).toUri();
        return ResponseEntity.created(uri).body(mapper.map(empresa, EmpresaResponseDto.class));
    }

    //VALIDADOR: ?status=PENDENTE lista as empresas aguardando a validacao do CNPJ
    @GetMapping
    public List<EmpresaResponseDto> listar(@RequestHeader(value = AcessoBo.HEADER, required = false) Integer usuarioLogado, @RequestParam(required = false) String status) throws SQLException {
        return empresaBo.listar(usuarioLogado, status)
                .stream()
                .map(empresa -> mapper.map(empresa, EmpresaResponseDto.class)).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmpresaResponseDto> buscarPorId(@RequestHeader(value = AcessoBo.HEADER, required = false) Integer usuarioLogado, @PathVariable int id) throws SQLException, EntidadeNaoEncontradaException {
        return ResponseEntity.ok(mapper.map(empresaBo.buscar(usuarioLogado, id), EmpresaResponseDto.class));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmpresaResponseDto> atualizar(@RequestHeader(value = AcessoBo.HEADER, required = false) Integer usuarioLogado, @PathVariable int id, @Valid @RequestBody EmpresaRequestDto dto)
            throws SQLException, EntidadeNaoEncontradaException {
        return ResponseEntity.ok(mapper.map(empresaBo.atualizar(usuarioLogado, id, dto), EmpresaResponseDto.class));
    }

    //VALIDADOR: PENDENTE -> ATIVA (RN03), ATIVA -> SUSPENSA/INATIVA etc.
    @PatchMapping("/{id}/status")
    public ResponseEntity<EmpresaResponseDto> alterarStatus(@RequestHeader(value = AcessoBo.HEADER, required = false) Integer usuarioLogado, @PathVariable int id, @Valid @RequestBody StatusRequestDto dto)
            throws SQLException, EntidadeNaoEncontradaException {
        return ResponseEntity.ok(mapper.map(empresaBo.alterarStatus(usuarioLogado, id, dto.getStatus()), EmpresaResponseDto.class));
    }
}
