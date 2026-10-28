package br.com.soulprime.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//Disponibiliza o ModelMapper para converter DTO <-> Model
//(ex.: CarteiraResponseDto.usuarioId recebe Carteira.usuario.id automaticamente)
@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper getModelMapper() {
        return new ModelMapper();
    }
}
