package com.example.ResTesT.controller;

// Importa a entidade utilizada no teste
import com.example.ResTesT.domain.MockEndpoint;

// Importa a exceção personalizada para simular erros de negócio
import com.example.ResTesT.exception.CustomException;

// Importa os serviços utilizados pelo controller
import com.example.ResTesT.service.MockEndpointService;
import com.example.ResTesT.service.RequestLogService;

// Importa objeto da requisição HTTP
import jakarta.servlet.http.HttpServletRequest;

// Importa anotações de ciclo de vida e teste do JUnit 5
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// Importa classe para construção de respostas HTTP
import org.springframework.http.ResponseEntity;

// Importa classe para lidar com resultados assíncronos
import java.util.concurrent.CompletableFuture;

// Importa métodos de validação do JUnit
import static org.junit.jupiter.api.Assertions.*;

// Importa métodos do Mockito para criação de mocks e simulação de comportamentos
import static org.mockito.Mockito.*;

// Classe de testes do PublicApiController
// ATENÇÃO: o metodo get() retorna CompletableFuture — usamos .get() para extrair o resultado
class PublicApiControllerTest {

    // ==========================================
    // DEPENDÊNCIAS MOCKADAS
    // ==========================================

    // Simula o service de endpoints sem acessar banco de dados
    private MockEndpointService endpointService;

    // Simula o service de logs sem acessar banco de dados
    private RequestLogService logService;

    // Instância real do controller recebendo os mocks via construtor
    private PublicApiController controller;

    // ==========================================
    // CONFIGURAÇÃO ANTES DE CADA TESTE
    // ==========================================

    @BeforeEach
    void setUp() {

        // Cria versões simuladas dos serviços
        endpointService = mock(MockEndpointService.class);
        logService      = mock(RequestLogService.class);

        // Instancia o controller utilizando os mocks
        controller = new PublicApiController(
                endpointService,
                logService
        );
    }

    // ==========================================
    // TESTA GET /api/{hash} — RETORNO DO PAYLOAD
    // ==========================================

    @Test
    void deveRetornarPayloadComStatus200() throws Exception {

        // Cria um endpoint mockado para o teste
        MockEndpoint endpoint =
                new MockEndpoint();

        // Define o payload que será retornado
        endpoint.setPayload("{\"ok\":true}");

        // Define o status HTTP esperado
        endpoint.setStatusCode(200);

        // Define delay zero para o teste executar rapidamente
        endpoint.setDelayMs(0);

        // Configura o mock para retornar o endpoint ao buscar pelo hash
        when(endpointService.findByHash("ABC"))
                .thenReturn(endpoint);

        // Cria mock da requisição HTTP
        HttpServletRequest request =
                mock(HttpServletRequest.class);

        // Simula o IP do cliente
        when(request.getRemoteAddr())
                .thenReturn("127.0.0.1");

        // Executa o metodo que está sendo testado e resolve o Future
        CompletableFuture<ResponseEntity<?>> future =
                controller.get("ABC", request);

        ResponseEntity<?> result = future.get();

        // Verifica se o status retornado é 200 OK
        assertEquals(200, result.getStatusCode().value());

        // Verifica se o log foi registrado após o acesso
        verify(logService, times(1)).log(endpoint, "127.0.0.1");
    }

    @Test
    void deveRetornarPayloadComStatusCustomizado() throws Exception {

        // Cria um endpoint configurado com status 404
        MockEndpoint endpoint =
                new MockEndpoint();

        endpoint.setPayload("{\"erro\":\"nao encontrado\"}");
        endpoint.setStatusCode(404);
        endpoint.setDelayMs(0);

        // Configura o mock para retornar o endpoint ao buscar pelo hash
        when(endpointService.findByHash("HASH404"))
                .thenReturn(endpoint);

        // Cria mock da requisição HTTP
        HttpServletRequest request =
                mock(HttpServletRequest.class);

        // Simula o IP do cliente
        when(request.getRemoteAddr())
                .thenReturn("10.0.0.1");

        // Executa o metodo e resolve o Future
        CompletableFuture<ResponseEntity<?>> future =
                controller.get("HASH404", request);

        ResponseEntity<?> result = future.get();

        // Verifica se o status configurado pelo usuário é respeitado
        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    void deveRetornarPayloadComStatus500() throws Exception {

        // Cria um endpoint configurado com status 500
        MockEndpoint endpoint =
                new MockEndpoint();

        endpoint.setPayload("{\"erro\":\"interno\"}");
        endpoint.setStatusCode(500);
        endpoint.setDelayMs(0);

        // Configura o mock para retornar o endpoint ao buscar pelo hash
        when(endpointService.findByHash("HASH500"))
                .thenReturn(endpoint);

        // Cria mock da requisição HTTP
        HttpServletRequest request =
                mock(HttpServletRequest.class);

        // Simula o IP do cliente
        when(request.getRemoteAddr())
                .thenReturn("192.168.0.1");

        // Executa o metodo e resolve o Future
        CompletableFuture<ResponseEntity<?>> future =
                controller.get("HASH500", request);

        ResponseEntity<?> result = future.get();

        // Verifica se o status 500 é corretamente propagado
        assertEquals(500, result.getStatusCode().value());
    }

    // ==========================================
    // TESTA EXCEÇÃO PARA HASH INEXISTENTE
    // ==========================================

    @Test
    void devePropagrarExcecaoQuandoHashNaoExiste() {

        // Simula o service lançando CustomException para hash inexistente
        when(endpointService.findByHash("INVALIDO"))
                .thenThrow(new CustomException(
                        "Endpoint não encontrado para o hash: INVALIDO"
                ));

        // Cria mock da requisição HTTP
        HttpServletRequest request =
                mock(HttpServletRequest.class);

        // Simula o IP do cliente
        when(request.getRemoteAddr())
                .thenReturn("127.0.0.1");

        // Verifica se a exceção é lançada antes do Future ser criado
        assertThrows(
                CustomException.class,
                () -> controller.get("INVALIDO", request)
        );

        // Verifica que o log NÃO foi registrado quando o endpoint não existe
        verify(logService, never()).log(any(), any());
    }

    // ==========================================
    // TESTA REGISTRO DE LOG A CADA ACESSO
    // ==========================================

    @Test
    void deveRegistrarLogAoCadaAcesso() throws Exception {

        // Cria endpoint de teste
        MockEndpoint endpoint =
                new MockEndpoint();

        endpoint.setPayload("{\"teste\":1}");
        endpoint.setStatusCode(200);
        endpoint.setDelayMs(0);

        // Configura o mock para retornar o endpoint ao buscar pelo hash
        when(endpointService.findByHash("LOGHASH"))
                .thenReturn(endpoint);

        // Cria mock da requisição HTTP
        HttpServletRequest request =
                mock(HttpServletRequest.class);

        // Simula o IP do cliente
        when(request.getRemoteAddr())
                .thenReturn("172.16.0.1");

        // Executa o acesso ao endpoint
        controller.get("LOGHASH", request).get();

        // Verifica se o log foi registrado com o endpoint e IP corretos
        verify(logService).log(endpoint, "172.16.0.1");
    }
}