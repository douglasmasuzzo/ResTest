package com.example.ResTesT.controller;

// Importa a entidade utilizada no teste
import com.example.ResTesT.domain.MockEndpoint;

// Importa os serviços utilizados pelo controller
import com.example.ResTesT.service.MockEndpointService;
import com.example.ResTesT.service.RequestLogService;

// Importa objeto da requisição HTTP
import jakarta.servlet.http.HttpServletRequest;

// Importa anotação de teste do JUnit 5
import org.junit.jupiter.api.Test;

// Importa método para validação de resultados
import static org.junit.jupiter.api.Assertions.assertEquals;

// Importa métodos do Mockito para criação de mocks e simulação de comportamentos
import static org.mockito.Mockito.*;

// Classe de teste do PublicApiController
class PublicApiControllerTest {

    // ==========================================
    // TESTA RETORNO DO ENDPOINT PÚBLICO
    // ==========================================

    @Test
    void deveRetornarPayload() throws Exception {

        // Cria mocks dos serviços utilizados pelo controller
        MockEndpointService service =
                mock(MockEndpointService.class);

        RequestLogService logService =
                mock(RequestLogService.class);

        // Cria um endpoint mockado para o teste
        MockEndpoint endpoint =
                new MockEndpoint();

        // Define o payload que será retornado
        endpoint.setPayload("{\"ok\":true}");

        // Define o status HTTP esperado
        endpoint.setStatusCode(200);

        // Configura o comportamento do mock:
        // quando buscar pelo hash "ABC",
        // retorna o endpoint criado acima
        when(service.findByHash("ABC"))
                .thenReturn(endpoint);

        // Instancia o controller com os mocks
        PublicApiController controller =
                new PublicApiController(
                        service,
                        logService
                );

        // Cria mock da requisição HTTP
        HttpServletRequest request =
                mock(HttpServletRequest.class);

        // Simula o IP do cliente
        when(request.getRemoteAddr())
                .thenReturn("127.0.0.1");

        // Executa o metodo que está sendo testado
        var response =
                controller.get("ABC", request);

        // Verifica se o status retornado é 200 OK
        assertEquals(
                200,
                response.getStatusCode().value()
        );
    }
}