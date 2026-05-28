package com.example.ResTesT.exception;

// Importa a classe ResponseEntity para respostas HTTP
import org.springframework.http.ResponseEntity;

// Importa anotações para tratamento global de exceções
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Define esta classe como um tratador global de exceções da aplicação
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Trata exceções do tipo CustomException
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<?> handleCustom(CustomException ex) {

        // Retorna status 400 (Bad Request)
        // junto com a mensagem da exceção
        return ResponseEntity.badRequest().body(ex.getMessage());
    }


    // Trata qualquer exceção genérica não tratada anteriormente
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneric(Exception ex) {


        // Exibe o erro completo no console da aplicação
        ex.printStackTrace();

        // Retorna status 500 (Internal Server Error)
        // com mensagem genérica
        return ResponseEntity.internalServerError().body("Erro interno");
    }
}