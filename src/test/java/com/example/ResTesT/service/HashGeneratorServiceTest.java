package com.example.ResTesT.service;

// Importa anotação de teste do JUnit 5
import org.junit.jupiter.api.Test;

// Importa métodos de validação (assertions)
import static org.junit.jupiter.api.Assertions.*;

// Classe de teste do HashGeneratorService
class HashGeneratorServiceTest {

    // Instância real do serviço que será testado
    private final HashGeneratorService service =
            new HashGeneratorService();

    // ==========================================
    // TESTA TAMANHO DO HASH GERADO
    // ==========================================

    @Test
    void deveGerarHashCom8Caracteres() {

        // Gera um novo hash
        String hash = service.generate();

        // Verifica se o hash não é nulo
        assertNotNull(hash);

        // Verifica se o hash possui exatamente 8 caracteres
        assertEquals(8, hash.length());
    }

    // ==========================================
    // TESTA SE OS HASHES SÃO DIFERENTES
    // ==========================================

    @Test
    void deveGerarHashesDiferentes() {

        // Gera dois hashes distintos
        String hash1 = service.generate();
        String hash2 = service.generate();

        // Verifica se os valores gerados são diferentes
        assertNotEquals(hash1, hash2);
    }
}