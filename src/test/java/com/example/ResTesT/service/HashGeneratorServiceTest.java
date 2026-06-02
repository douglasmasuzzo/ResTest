package com.example.ResTesT.service;

// Importa anotações de teste do JUnit 5
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

// Importa classes auxiliares
import java.util.HashSet;
import java.util.Set;

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
        // com 62^8 combinações possíveis, a chance de colisão é desprezível
        assertNotEquals(hash1, hash2);
    }

    // ==========================================
    // TESTA SE APENAS CARACTERES VÁLIDOS SÃO GERADOS
    // ==========================================

    @Test
    void deveConterApenasCaracteresAlfanumericos() {

        // Gera cem hashes e verifica se todos contêm apenas [A-Za-z0-9]
        for (int i = 0; i < 100; i++) {

            // Gera um novo hash
            String hash = service.generate();

            // Verifica se o hash corresponde ao padrão alfanumérico de 8 caracteres
            assertTrue(
                    hash.matches("[A-Za-z0-9]{8}"),
                    "Hash inválido gerado: " + hash
            );
        }
    }

    // ==========================================
    // TESTA UNICIDADE EM MÚLTIPLAS GERAÇÕES
    // ==========================================

    @Test
    void deveGerarHashesUnicosEmMuitasTentativas() {

        // Define a quantidade de hashes a serem gerados
        int quantidade = 1000;

        // Armazena os hashes gerados em um Set para verificar unicidade
        Set<String> hashes = new HashSet<>();

        // Gera os hashes e adiciona ao conjunto
        for (int i = 0; i < quantidade; i++) {
            hashes.add(service.generate());
        }

        // Verifica se nenhuma colisão ocorreu entre os hashes gerados
        // se houve colisão, o Set terá menos elementos que o esperado
        assertEquals(
                quantidade,
                hashes.size(),
                "Foram geradas colisões entre " + quantidade + " hashes"
        );
    }

    // ==========================================
    // TESTA QUE NUNCA RETORNA NULL OU VAZIO
    // ==========================================

    @RepeatedTest(50)
    void nuncaDeveRetornarNullOuVazio() {

        // Gera um novo hash
        String hash = service.generate();

        // Verifica se o hash não é nulo
        assertNotNull(hash);

        // Verifica se o hash não está em branco
        assertFalse(hash.isBlank());
    }
}