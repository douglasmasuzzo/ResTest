package com.example.ResTesT.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                // Desabilita a proteção CSRF
            .csrf(csrf -> csrf.disable())


                // Configura as permissões das requisições
            .authorizeHttpRequests(auth -> auth

                    // Permite acesso a qualquer endpoint sem autenticação
                    .anyRequest().permitAll()
            );

        // Constrói e retorna a configuração de segurança
        return http.build();
    }
}
