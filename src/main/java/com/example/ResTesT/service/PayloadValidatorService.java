package com.example.ResTesT.service;



// Importa exceção personalizada
import com.example.ResTesT.exception.CustomException;

// Importa ObjectMapper da biblioteca Jackson
// utilizado para converter objetos Java em JSON
// e também JSON em objetos Java
import com.fasterxml.jackson.databind.ObjectMapper;

// Importa anotação para registrar a classe como service Spring
import org.springframework.stereotype.Service;

@Service // Define a classe como camada de serviço responsável pela lógica de validação do JSON
public class PayloadValidatorService {

    // Objeto responsável por interpretar e validar JSON
    private final ObjectMapper mapper = new ObjectMapper();

    // Define o tamanho máximo permitido para o JSON
    // 100 KB = 100 * 1024 bytes
    private static final int MAX_SIZE = 100 * 1024;

    // Metodo que faz lógica de validação JSON e de tamanho permitido
    public void validate(String json) {
        try {

            // Tenta converter o texto em uma árvore JSON
            // Se o JSON estiver inválido, ocorrerá uma exceção
            mapper.readTree(json);

        } catch (Exception e) {

            // Lança exceção personalizada caso
            // o JSON seja inválido
            throw new CustomException("JSON inválido");
        }

        // Verifica se o tamanho do JSON excede o limite permitido
        if (json.getBytes().length > MAX_SIZE) {

            // Lança exceção caso exceda 100KB
            throw new CustomException("JSON excede 100KB");
        }
    }
}