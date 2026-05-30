package com.example.ResTesT.exception;

// Importa anotação de teste do JUnit 5
import org.junit.jupiter.api.Test;

// Importa ResponseEntity utilizado nos retornos dos métodos testados
import org.springframework.http.ResponseEntity;

// Importa métodos de asserção do JUnit
import static org.junit.jupiter.api.Assertions.*;

// Classe de teste do GlobalExceptionHandler
class GlobalExceptionHandlerTest {

    // Instância do tratador de exceções que será testado
    private final GlobalExceptionHandler handler =
            new GlobalExceptionHandler();

    // ==========================================
    // TESTA TRATAMENTO DE CUSTOMEXCEPTION
    // ==========================================

    @Test
    void deveRetornar400ParaCustomException() {

        // Executa o método responsável por tratar
        // exceções personalizadas
        ResponseEntity<?> response =
                handler.handleCustom(
                        new CustomException("Erro")
                );

        // Verifica se o status retornado é 400 (Bad Request)
        assertEquals(
                400,
                response.getStatusCode().value()
        );
    }

    // ==========================================
    // TESTA TRATAMENTO DE EXCEÇÕES GENÉRICAS
    // ==========================================

    @Test
    void deveRetornar500ParaErroGenerico() {

        // Executa o metodo responsável por tratar
        // exceções genéricas da aplicação
        ResponseEntity<?> response =
                handler.handleGeneric(
                        new RuntimeException()
                );

        // Verifica se o status retornado é
        // 500 (Internal Server Error)
        assertEquals(
                500,
                response.getStatusCode().value()
        );
    }
}