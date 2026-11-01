package com.mycompany.frontend_fastfood.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Crea el RestTemplate que usan los controladores para llamar
 * a la API del backend.
 *
 * El NavegacionController del ejemplo de la clase 11 recibe un
 * RestTemplate en el constructor, pero Spring Boot no lo crea solo:
 * hay que declararlo como @Bean.
 */
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}