package com.example.ResTesT.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Indica que esta classe é uma classe de configuração do Spring
@Configuration
public class CorsConfig {

    // Cria um Bean que configura as regras de CORS da aplicação
    @Bean
    public WebMvcConfigurer corsConfigurer() {

        // Retorna uma implementação personalizada do WebMvcConfigurer
        return new WebMvcConfigurer() {

            @Override // Sobrescreve o metodo responsável pela configuração do CORS
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // aplica CORS em todos os endpoints
                        .allowedOrigins(
                                "http://localhost:5500", // URLs do seu frontend
                                "http://127.0.0.1:5500" // Permite requisições usando o IP local
                                // pode ser adicionada URL do seu frontend em produção
                        )
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // métodos permitidos
                        .allowedHeaders("*") // permite todos os headers
                        .allowCredentials(true); // permite envio de cookies ou autenticação
            }
        };
    }
}