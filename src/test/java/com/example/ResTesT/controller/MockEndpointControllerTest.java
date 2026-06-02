package com.example.ResTesT.controller;

// Importa os DTOs de entrada e saída utilizados nos testes
import com.example.ResTesT.dto.CreateEndpointRequest;
import com.example.ResTesT.dto.EndpointResponse;

// Importa a exceção personalizada para simular erros de negócio
import com.example.ResTesT.exception.CustomException;

// Importa o service que será mockado nos testes
import com.example.ResTesT.service.MockEndpointService;

// Importa anotações de ciclo de vida e teste do JUnit 5
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// Importa classe para construção de respostas HTTP
import org.springframework.http.ResponseEntity;

// Importa classes auxiliares
import java.util.List;
import java.util.UUID;

// Importa métodos de validação do JUnit
import static org.junit.jupiter.api.Assertions.*;

// Importa métodos do Mockito para criação de mocks e verificações
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

// Classe de testes do MockEndpointController
class MockEndpointControllerTest {

    // ==========================================
    // DEPENDÊNCIAS MOCKADAS
    // ==========================================

    // Simula o service sem executar lógica real
    private MockEndpointService service;

    // Instância real do controller recebendo o mock via construtor
    private MockEndpointController controller;

    // ==========================================
    // CONFIGURAÇÃO ANTES DE CADA TESTE
    // ==========================================

    @BeforeEach
    void setUp() {

        // Cria uma versão simulada do service
        service = mock(MockEndpointService.class);

        // Instancia o controller utilizando o mock
        controller = new MockEndpointController(service);
    }

    // ==========================================
    // TESTA INSTANCIAÇÃO DO CONTROLLER
    // ==========================================

    @Test
    void deveInstanciarControllerCorretamente() {

        // Verifica se o objeto foi criado corretamente
        assertNotNull(controller);
    }

    // ==========================================
    // TESTA POST /mock — CRIAÇÃO DE ENDPOINT
    // ==========================================

    @Test
    void deveRetornar201AoCriarEndpoint() {

        // Cria objeto de requisição com os dados do novo endpoint
        CreateEndpointRequest req =
                new CreateEndpointRequest();

        req.setPayload("{\"nome\":\"Douglas\"}");
        req.setStatusCode(200);
        req.setDelayMs(0);
        req.setLabel("Teste de criação");

        // Cria resposta simulada que o service retornará
        EndpointResponse resposta =
                new EndpointResponse();

        resposta.setHash("ABC12345");
        resposta.setUrl("/api/ABC12345");
        resposta.setPayload("{\"nome\":\"Douglas\"}");
        resposta.setStatusCode(200);
        resposta.setDelayMs(0);

        // Configura o mock para retornar a resposta simulada ao criar
        when(service.create(any(CreateEndpointRequest.class)))
                .thenReturn(resposta);

        // Executa o método que está sendo testado
        ResponseEntity<EndpointResponse> result =
                controller.create(req);

        // Verifica se o status retornado é 201 Created
        assertEquals(201, result.getStatusCode().value());

        // Verifica se o hash retornado no corpo é o esperado
        assertNotNull(result.getBody());
        assertEquals("ABC12345", result.getBody().getHash());

        // Verifica se o service foi chamado exatamente uma vez
        verify(service, times(1)).create(any(CreateEndpointRequest.class));
    }

    @Test
    void deveCriarEndpointComValoresPadrao() {

        // Cria requisição apenas com o payload obrigatório
        // statusCode e delayMs ficam nulos — o service deve defaultar para 200 e 0
        CreateEndpointRequest req =
                new CreateEndpointRequest();

        req.setPayload("{\"padrao\":true}");

        // Cria resposta simulada com os valores padrão aplicados
        EndpointResponse resposta =
                new EndpointResponse();

        resposta.setHash("DEF67890");
        resposta.setStatusCode(200);
        resposta.setDelayMs(0);

        // Configura o mock
        when(service.create(any(CreateEndpointRequest.class)))
                .thenReturn(resposta);

        // Executa criação
        ResponseEntity<EndpointResponse> result =
                controller.create(req);

        // Verifica se os valores padrão foram aplicados corretamente
        assertEquals(200, result.getBody().getStatusCode());
        assertEquals(0, result.getBody().getDelayMs());
    }

    // ==========================================
    // TESTA GET /mock — LISTAGEM DE ENDPOINTS
    // ==========================================

    @Test
    void deveRetornar200ComListaDeEndpoints() {

        // Monta lista simulada com dois endpoints
        EndpointResponse ep1 = new EndpointResponse();
        ep1.setHash("HASH0001");

        EndpointResponse ep2 = new EndpointResponse();
        ep2.setHash("HASH0002");

        // Configura o mock para retornar a lista simulada
        when(service.findAll())
                .thenReturn(List.of(ep1, ep2));

        // Executa a listagem
        ResponseEntity<?> result = controller.list();

        // Verifica se o status retornado é 200 OK
        assertEquals(200, result.getStatusCode().value());

        // Verifica se o service foi chamado
        verify(service, times(1)).findAll();
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHaEndpoints() {

        // Configura o mock para retornar lista vazia
        when(service.findAll())
                .thenReturn(List.of());

        // Executa a listagem
        ResponseEntity<?> result = controller.list();

        // Verifica se o status retornado é 200 OK mesmo sem resultados
        assertEquals(200, result.getStatusCode().value());

        // Verifica se o service foi chamado
        verify(service, times(1)).findAll();
    }

    // ==========================================
    // TESTA PUT /mock/{id} — ATUALIZAÇÃO
    // ==========================================

    @Test
    void deveRetornar200AoAtualizarEndpoint() {

        // Gera ID para o teste
        UUID id = UUID.randomUUID();

        // Cria nova requisição de atualização
        CreateEndpointRequest req =
                new CreateEndpointRequest();

        req.setPayload("{\"atualizado\":true}");
        req.setStatusCode(201);

        // Cria resposta simulada com os dados atualizados
        EndpointResponse resposta =
                new EndpointResponse();

        resposta.setPayload("{\"atualizado\":true}");
        resposta.setStatusCode(201);

        // Configura o mock para retornar a resposta ao atualizar
        when(service.update(eq(id), any(CreateEndpointRequest.class)))
                .thenReturn(resposta);

        // Executa a atualização
        ResponseEntity<?> result = controller.update(id, req);

        // Verifica se o status retornado é 200 OK
        assertEquals(200, result.getStatusCode().value());

        // Verifica se o service foi chamado com os parâmetros corretos
        verify(service, times(1)).update(eq(id), any(CreateEndpointRequest.class));
    }

    @Test
    void devePropagrarExcecaoQuandoIdNaoExisteNaAtualizacao() {

        // Gera ID fictício que não existe no banco
        UUID id = UUID.randomUUID();

        // Cria requisição de atualização
        CreateEndpointRequest req =
                new CreateEndpointRequest();

        req.setPayload("{\"x\":1}");

        // Simula o service lançando CustomException para ID inexistente
        when(service.update(eq(id), any()))
                .thenThrow(new CustomException("Mock não encontrado"));

        // Verifica se a exceção é propagada pelo controller
        assertThrows(
                CustomException.class,
                () -> controller.update(id, req)
        );
    }

    // ==========================================
    // TESTA DELETE /mock/{id} — REMOÇÃO
    // ==========================================

    @Test
    void deveRetornar204AoDeletarEndpoint() {

        // Gera ID para o teste
        UUID id = UUID.randomUUID();

        // Configura o mock para não fazer nada ao deletar (void)
        doNothing().when(service).delete(id);

        // Executa a remoção
        ResponseEntity<?> result = controller.delete(id);

        // Verifica se o status retornado é 204 No Content
        assertEquals(204, result.getStatusCode().value());

        // Verifica se o service foi chamado com o ID correto
        verify(service, times(1)).delete(id);
    }

    @Test
    void devePropagrarExcecaoQuandoIdNaoExisteNaDelecao() {

        // Gera ID fictício que não existe no banco
        UUID id = UUID.randomUUID();

        // Simula o service lançando CustomException ao tentar deletar ID inexistente
        doThrow(new CustomException("Não foi possível deletar"))
                .when(service).delete(id);

        // Verifica se a exceção é propagada pelo controller
        assertThrows(
                CustomException.class,
                () -> controller.delete(id)
        );
    }
}