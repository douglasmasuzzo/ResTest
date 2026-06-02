package com.example.ResTesT.exception;

// Importa anotação de teste do JUnit 5
import org.junit.jupiter.api.Test;

// Importa ResponseEntity utilizado nos retornos dos métodos testados
import org.springframework.http.ResponseEntity;

// Importa classes para simular erros de validação de campos
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

// Importa classes auxiliares
import java.util.List;
import java.util.Map;

// Importa métodos de asserção do JUnit
import static org.junit.jupiter.api.Assertions.*;

// Importa métodos do Mockito para criação de mocks e simulação de comportamentos
import static org.mockito.Mockito.*;

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

        // Executa o metodo responsável por tratar exceções personalizadas
        ResponseEntity<Map<String, String>> response =
                handler.tratarCustomException(
                        new CustomException("Erro de negócio")
                );

        // Verifica se o status retornado é 400 (Bad Request)
        assertEquals(
                400,
                response.getStatusCode().value()
        );

        // Verifica se o corpo contém a chave "erro" com a mensagem correta
        assertNotNull(response.getBody());
        assertEquals("Erro de negócio", response.getBody().get("erro"));
    }

    @Test
    void devePropagrarMensagemDaCustomException() {

        // Define a mensagem de erro que será lançada pelo service
        String mensagem = "Mock não encontrado para o ID: abc-123";

        // Executa o metodo responsável por tratar exceções personalizadas
        ResponseEntity<Map<String, String>> response =
                handler.tratarCustomException(
                        new CustomException(mensagem)
                );

        // Verifica se a mensagem original foi preservada na resposta
        assertEquals(mensagem, response.getBody().get("erro"));
    }

    // ==========================================
    // TESTA TRATAMENTO DE ERROS DE VALIDAÇÃO
    // ==========================================

    @Test
    void deveRetornar400ComTodosOsErrosDeValidacao() {

        // Cria mock do MethodArgumentNotValidException simulando dois erros de campo
        MethodArgumentNotValidException ex =
                mock(MethodArgumentNotValidException.class);

        BindingResult bindingResult =
                mock(BindingResult.class);

        // Define os erros de validação simulados
        FieldError erroCampo1 =
                new FieldError("req", "payload", "O payload não pode ser vazio");

        FieldError erroCampo2 =
                new FieldError("req", "statusCode", "O status code mínimo permitido é 100");

        // Configura o mock para retornar os erros simulados
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(erroCampo1, erroCampo2));

        // Executa o metodo responsável por tratar erros de validação de campos
        ResponseEntity<Map<String, String>> response =
                handler.tratarErosDeValidacao(ex);

        // Verifica se o status retornado é 400 (Bad Request)
        assertEquals(
                400,
                response.getStatusCode().value()
        );

        // Verifica se os dois erros foram incluídos no corpo da resposta
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals("O payload não pode ser vazio", response.getBody().get("payload"));
        assertEquals("O status code mínimo permitido é 100", response.getBody().get("statusCode"));
    }

    @Test
    void deveRetornarMapaVazioSeNaoHouverErrosDeValidacao() {

        // Cria mock sem nenhum erro de campo
        MethodArgumentNotValidException ex =
                mock(MethodArgumentNotValidException.class);

        BindingResult bindingResult =
                mock(BindingResult.class);

        // Configura o mock para retornar lista vazia de erros
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of());

        // Executa o metodo responsável por tratar erros de validação
        ResponseEntity<Map<String, String>> response =
                handler.tratarErosDeValidacao(ex);

        // Verifica se o status retornado é 400 mesmo sem erros de campo
        assertEquals(400, response.getStatusCode().value());

        // Verifica se o corpo está vazio
        assertTrue(response.getBody().isEmpty());
    }

    // ==========================================
    // TESTA TRATAMENTO DE EXCEÇÕES GENÉRICAS
    // ==========================================

    @Test
    void deveRetornar500ParaErroGenerico() {

        // Executa o metodo responsável por tratar exceções genéricas da aplicação
        ResponseEntity<Map<String, String>> response =
                handler.tratarErroGenerico(
                        new RuntimeException("Erro inesperado")
                );

        // Verifica se o status retornado é 500 (Internal Server Error)
        assertEquals(
                500,
                response.getStatusCode().value()
        );

        // Verifica se o corpo contém a chave "erro" com alguma mensagem
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("erro"));
    }

    @Test
    void naoDeveExporDetalhesInternosNoErroGenerico() {

        // Executa o metodo com uma exceção que contém detalhes internos
        ResponseEntity<Map<String, String>> response =
                handler.tratarErroGenerico(
                        new NullPointerException("detalhe interno do servidor")
                );

        // Obtém a mensagem retornada ao cliente
        String mensagemRetornada = response.getBody().get("erro");

        // Verifica que a mensagem interna da exceção não foi exposta ao cliente
        assertFalse(mensagemRetornada.contains("detalhe interno do servidor"));

        // Verifica que o nome da exceção não foi exposto ao cliente
        assertFalse(mensagemRetornada.contains("NullPointerException"));
    }
}