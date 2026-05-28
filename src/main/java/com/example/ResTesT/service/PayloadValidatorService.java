package com.example.ResTesT.service;

// Importa a classe ObjectMapper do Jackson
// utilizada para manipulação e validação de JSON

import com.example.ResTesT.exception.CustomException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class PayloadValidatorService {

    // Objeto responsável por interpretar e validar JSON
    private final ObjectMapper mapper = new ObjectMapper();

    // Define o tamanho máximo permitido para o JSON
    // 100 KB = 100 * 1024 bytes
    private static final int MAX_SIZE = 100 * 1024;

    public void validate(String json) {
        try {

            // Tenta converter o texto em uma árvore JSON
            // Se o JSON estiver inválido, ocorrerá uma exceção
            mapper.readTree(json);

        } catch (Exception e) {
            throw new CustomException("JSON inválido");
        }

        // Verifica se o tamanho do JSON excede o limite permitido
        if (json.getBytes().length > MAX_SIZE) {
            throw new CustomException("JSON excede 100KB");
        }
    }
}