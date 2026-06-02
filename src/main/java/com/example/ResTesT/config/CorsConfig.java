package com.example.ResTesT.config;

// Importa anotações do Spring para criar beans de configuração
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Importa as classes necessárias para configurar o CORS
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Classe de configuração de CORS (Cross-Origin Resource Sharing).
 *
 * O que é CORS?
 *   Quando um frontend tenta fazer uma requisição para um backend em um
 *   domínio/porta diferente, o navegador bloqueia por segurança.
 *   O CORS permite que o backend declare quais origens têm permissão.
 *
 * Origens permitidas nesta configuração:
 *
 *   - http://localhost:8080 e http://127.0.0.1:8080:
 *       O front-end é servido pelo próprio Spring Boot (pasta static/).
 *       Tecnicamente é mesma origem, mas declarar explicitamente evita
 *       qualquer ambiguidade do navegador.
 *
 *   - http://localhost:5500 e http://127.0.0.1:5500:
 *       Live Server do VS Code — útil durante desenvolvimento separado
 *       do front-end fora do Spring Boot.
 *
 */
@Configuration // Indica ao Spring que esta classe contém beans de configuração
public class CorsConfig {

    /**
     * Registra as regras de CORS globais para toda a aplicação.
     *
     * @return um WebMvcConfigurer com as regras de CORS definidas
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {

        return new WebMvcConfigurer() {

            @Override
            public void addCorsMappings(CorsRegistry registry) {

                registry
                        // Aplica as regras de CORS para todos os endpoints da API
                        .addMapping("/**")

                        // Permite o front-end servido pelo Spring Boot (porta 8080)
                        // e também o Live Server do VS Code (porta 5500) para desenvolvimento
                        .allowedOrigins(
                                "http://localhost:8080",    // Spring Boot servindo o front-end
                                "http://127.0.0.1:8080",   // Mesma coisa com IP explícito
                                "http://localhost:5500",    // Live Server do VS Code
                                "http://127.0.0.1:5500"    // Mesma coisa com IP explícito
                        )

                        // Métodos HTTP permitidos nas requisições cross-origin
                        // OPTIONS é necessário para o preflight request do CORS
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")

                        // Permite qualquer header nas requisições
                        .allowedHeaders("*")

                        // Permite envio de credenciais nas requisições cross-origin
                        .allowCredentials(true);
            }
        };
    }
}