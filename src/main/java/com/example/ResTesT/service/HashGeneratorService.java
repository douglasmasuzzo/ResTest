package com.example.ResTesT.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class HashGeneratorService {

    // Conjunto de caracteres permitidos no hash
    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    // Define o tamanho do hash gerado
    private static final int LENGTH = 8;

    // Gerador de números aleatórios seguro
    private final SecureRandom random = new SecureRandom();


    // Metodo responsável por gerar o hash aleatório
    public String generate() {

        // Cria um objeto para montar a string do hash
        StringBuilder sb = new StringBuilder();

        // Loop para gerar cada caractere do hash
        for (int i = 0; i < LENGTH; i++) {

            // Escolhe um caractere aleatório da string CHARS
            // e adiciona ao hash
            sb.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        return sb.toString();
    }
}