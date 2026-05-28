package com.example.ResTesT.exception;

// Classe de exceção personalizada da aplicação
// Herda RuntimeException para criar erros específicos do sistema
public class CustomException extends RuntimeException {

    // Construtor que recebe a mensagem de erro
    public CustomException(String message) {

        // Envia a mensagem para a classe pai RuntimeException
        super(message);
    }
}