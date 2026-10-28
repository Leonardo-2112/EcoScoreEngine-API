package br.com.soulprime;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Ponto de entrada da API RESTful do EcoScore Engine (GaiaTech).
 * Camadas: resource (REST) -> bo (regras de negocio) -> dao (JDBC) -> factory (conexao).
 */
@SpringBootApplication
public class EcoScoreEngineApiApplication {

    public static void main(String[] args) {
        // Datas gravadas no Oracle (tipo DATE) seguem o horario de Brasilia.
        TimeZone.setDefault(TimeZone.getTimeZone("America/Sao_Paulo"));
        SpringApplication.run(EcoScoreEngineApiApplication.class, args);
    }
}
