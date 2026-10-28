package br.com.soulprime.resource;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.soulprime.dto.HealthResponseDto;
import br.com.soulprime.factory.ConnectionFactory;

//Pagina inicial da API e verificacao de saude (API + banco), usada no deploy
@RestController
public class HealthResource {

    private final ConnectionFactory connectionFactory;

    public HealthResource(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @GetMapping("/")
    public Map<String, String> inicio() {
        Map<String, String> info = new LinkedHashMap<>();
        info.put("api", "EcoScore Engine - API RESTful (GaiaTech)");
        info.put("versao", "1.0.0");
        info.put("saude", "/health");
        info.put("catalogo", "/acoes");
        info.put("ranking", "/ranking");
        return info;
    }

    //200 com o banco UP; 503 se a API estiver no ar mas o banco nao responder
    @GetMapping("/health")
    public ResponseEntity<HealthResponseDto> health() {
        HealthResponseDto dto = new HealthResponseDto();
        dto.setStatus("UP");
        dto.setVersao("1.0.0");
        dto.setHorario(LocalDateTime.now().withNano(0));
        try (Connection conexao = connectionFactory.getConnection()) {
            dto.setBanco(conexao.isValid(5) ? "UP" : "DOWN");
        } catch (SQLException e) {
            dto.setBanco("DOWN");
        }
        if (dto.getBanco().equals("UP")) {
            return ResponseEntity.ok(dto);
        }
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(dto);
    }
}
