package br.com.soulprime.resource;

import java.net.URI;
import java.sql.SQLException;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.soulprime.bo.AcessoBo;
import br.com.soulprime.bo.CarteiraBo;
import br.com.soulprime.dto.CarteiraResponseDto;
import br.com.soulprime.dto.ConversaoResponseDto;
import br.com.soulprime.dto.SaqueRequestDto;
import br.com.soulprime.dto.SaqueResponseDto;
import br.com.soulprime.exception.EntidadeNaoEncontradaException;
import br.com.soulprime.model.HistoricoConversao;
import br.com.soulprime.model.HistoricoSaque;
import jakarta.validation.Valid;

//Carteira do participante: saldo, conversao de pontos em creditos e saques
@RestController
@RequestMapping("/usuarios/{id}")
public class CarteiraResource {

    private final CarteiraBo carteiraBo;
    private final ModelMapper mapper;

    public CarteiraResource(CarteiraBo carteiraBo, ModelMapper mapper) {
        this.carteiraBo = carteiraBo;
        this.mapper = mapper;
    }

    @GetMapping("/carteira")
    public ResponseEntity<CarteiraResponseDto> carteira(@RequestHeader(value = AcessoBo.HEADER, required = false) Integer usuarioLogado, @PathVariable int id) throws SQLException, EntidadeNaoEncontradaException {
        return ResponseEntity.ok(mapper.map(carteiraBo.consultar(usuarioLogado, id), CarteiraResponseDto.class));
    }

    //201 conversao realizada | 422 menos de 100 pontos de saldo
    @PostMapping("/conversoes")
    public ResponseEntity<ConversaoResponseDto> converter(@RequestHeader(value = AcessoBo.HEADER, required = false) Integer usuarioLogado, @PathVariable int id, UriComponentsBuilder builder)
            throws SQLException, EntidadeNaoEncontradaException {
        HistoricoConversao conversao = carteiraBo.converter(usuarioLogado, id);
        URI uri = builder.path("/usuarios/{id}/conversoes").buildAndExpand(id).toUri();
        return ResponseEntity.created(uri).body(mapper.map(conversao, ConversaoResponseDto.class));
    }

    @GetMapping("/conversoes")
    public List<ConversaoResponseDto> conversoes(@RequestHeader(value = AcessoBo.HEADER, required = false) Integer usuarioLogado, @PathVariable int id) throws SQLException, EntidadeNaoEncontradaException {
        return carteiraBo.conversoes(usuarioLogado, id)
                .stream()
                .map(conversao -> mapper.map(conversao, ConversaoResponseDto.class)).toList();
    }

    //201 saque processado | 400 dados invalidos | 422 abaixo do minimo ou saldo insuficiente
    @PostMapping("/saques")
    public ResponseEntity<SaqueResponseDto> sacar(@RequestHeader(value = AcessoBo.HEADER, required = false) Integer usuarioLogado, @PathVariable int id, @Valid @RequestBody SaqueRequestDto dto,
                                                  UriComponentsBuilder builder) throws SQLException, EntidadeNaoEncontradaException {
        HistoricoSaque saque = carteiraBo.sacar(usuarioLogado, id, dto);
        URI uri = builder.path("/usuarios/{id}/saques").buildAndExpand(id).toUri();
        return ResponseEntity.created(uri).body(mapper.map(saque, SaqueResponseDto.class));
    }

    @GetMapping("/saques")
    public List<SaqueResponseDto> saques(@RequestHeader(value = AcessoBo.HEADER, required = false) Integer usuarioLogado, @PathVariable int id) throws SQLException, EntidadeNaoEncontradaException {
        return carteiraBo.saques(usuarioLogado, id)
                .stream()
                .map(saque -> mapper.map(saque, SaqueResponseDto.class)).toList();
    }
}
