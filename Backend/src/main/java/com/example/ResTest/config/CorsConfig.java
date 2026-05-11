package com.example.ResTest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // aplica CORS em todos os endpoints
                        .allowedOrigins(
                                "http://localhost:5500",
                                "http://127.0.0.1:5500"
                                 // URL do seu frontend em produção
                        ) // URLs do seu frontend
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // métodos permitidos
                        .allowedHeaders("*") // permite todos os headers
                        .allowCredentials(true); // permite envio de cookies ou autenticação
            }
        };
    }
}