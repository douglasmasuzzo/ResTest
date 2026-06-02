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

    @Test
    void deveAceitarJsonArrayValido() {

        // Verifica se nenhum erro é lançado
        // ao validar um JSON do tipo array
        assertDoesNotThrow(() ->
                service.validate("[{\"id\":1}, {\"id\":2}]")
        );
    }

    @Test
    void deveAceitarJsonVazio() {

        // Verifica se nenhum erro é lançado
        // ao validar um objeto JSON sem campos
        assertDoesNotThrow(() ->
                service.validate("{}")
        );
    }

    @Test
    void deveAceitarJsonComNumeros() {

        // Verifica se nenhum erro é lançado
        // ao validar um JSON com campos numéricos
        assertDoesNotThrow(() ->
                service.validate("{\"preco\":99.99, \"quantidade\":5}")
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

    @Test
    void deveLancarErroParaStringPura() {

        // Verifica se uma CustomException é lançada
        // quando o valor enviado é texto puro sem estrutura JSON
        assertThrows(
                CustomException.class,
                () -> service.validate("isso nao e json")
        );
    }

    @Test
    void deveLancarErroParaJsonMalFormado() {

        // Verifica se uma CustomException é lançada
        // quando o JSON possui chave sem valor
        assertThrows(
                CustomException.class,
                () -> service.validate("{\"chave\":}")
        );
    }

    @Test
    void deveLancarErroParaJsonIncompleto() {

        // Verifica se uma CustomException é lançada
        // quando o JSON não possui fechamento correto
        assertThrows(
                CustomException.class,
                () -> service.validate("{\"nome\":\"Douglas\"")
        );
    }

    // ==========================================
    // TESTA LIMITE MÁXIMO DE TAMANHO
    // ==========================================

    @Test
    void deveLancarErroQuandoJsonExcedeLimite() {

        // Cria uma string muito grande
        // com aproximadamente 110 KB
        String textoGrande = "a".repeat(110_000);

        // Verifica se a validação lança exceção
        // ao ultrapassar o limite máximo permitido
        assertThrows(
                CustomException.class,
                () -> service.validate("\"" + textoGrande + "\"")
        );
    }

    @Test
    void deveAceitarJsonDentroDoLimite() {

        // Cria um payload pequeno bem dentro do limite permitido
        String payloadPequeno =
                "{\"dados\":\"" + "x".repeat(100) + "\"}";

        // Verifica se nenhum erro é lançado para payload dentro do limite
        assertDoesNotThrow(() ->
                service.validate(payloadPequeno)
        );
    }
}