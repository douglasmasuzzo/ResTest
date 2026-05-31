package com.example.ResTesT.service;

// Importa exceção personalizada utilizada nas validações
import com.example.ResTesT.exception.CustomException;

// Importa anotação de teste do JUnit 5
import org.junit.jupiter.api.Test;

// Importa métodos de validação do JUnit
import static org.junit.jupiter.api.Assertions.*;

// Classe de testes do PayloadValidatorService
class PayloadValidatorServiceTest {

    // Instância real do serviço que será testado
    private final PayloadValidatorService service =
            new PayloadValidatorService();

    // ==========================================
    // TESTA JSON VÁLIDO
    // ==========================================

    @Test
    void deveAceitarJsonValido() {

        // Verifica se nenhum erro é lançado
        // ao validar um JSON corretamente formatado
        assertDoesNotThrow(() ->
                service.validate("{\"nome\":\"Douglas\"}")
        );
    }

    // ==========================================
    // TESTA JSON INVÁLIDO
    // ==========================================

    @Test
    void deveLancarErroParaJsonInvalido() {

        // Verifica se uma CustomException é lançada
        // quando o JSON possui formato inválido
        assertThrows(
                CustomException.class,
                () -> service.validate("{nome}")
        );
    }

    // ==========================================
    // TESTA LIMITE MÁXIMO DE TAMANHO
    // ==========================================

    @Test
    void deveLancarErroQuandoJsonExcedeLimite() {

        // Cria uma string muito grande
        // com aproximadamente 110 KB
        String textoGrande = "a".repeat(110000);

        // Verifica se a validação lança exceção
        // ao ultrapassar o limite máximo permitido
        assertThrows(
                CustomException.class,
                () -> service.validate("\"" + textoGrande + "\"")
        );
    }
}