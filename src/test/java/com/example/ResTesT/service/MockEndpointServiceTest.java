package com.example.ResTesT.service;

// Importa entidade, DTOs, exceção e repository utilizados nos testes
import com.example.ResTesT.domain.MockEndpoint;
import com.example.ResTesT.dto.CreateEndpointRequest;
import com.example.ResTesT.dto.EndpointResponse;
import com.example.ResTesT.exception.CustomException;
import com.example.ResTesT.repository.MockEndpointRepository;

// Importa anotação de teste do JUnit 5
import org.junit.jupiter.api.Test;

// Importa classes auxiliares
import java.util.Optional;
import java.util.UUID;

// Importa métodos de validação do JUnit
import static org.junit.jupiter.api.Assertions.*;

// Importa recursos do Mockito para mocks e verificações
import static org.mockito.Mockito.*;

// Classe de testes do MockEndpointService
class MockEndpointServiceTest {

    // ==========================================
    // DEPENDÊNCIAS MOCKADAS
    // ==========================================

    // Simula o repository sem acessar banco de dados
    private final MockEndpointRepository repo =
            mock(MockEndpointRepository.class);

    // Simula o gerador de hashes
    private final HashGeneratorService hashService =
            mock(HashGeneratorService.class);

    // Simula o validador de payload JSON
    private final PayloadValidatorService validator =
            mock(PayloadValidatorService.class);

    // Instância do serviço que será testado
    private final MockEndpointService service =
            new MockEndpointService(
                    repo,
                    hashService,
                    validator
            );

    // ==========================================
    // TESTA CRIAÇÃO DE ENDPOINT
    // ==========================================

    @Test
    void deveCriarEndpoint() {

        // Cria objeto de requisição
        CreateEndpointRequest req =
                new CreateEndpointRequest();

        req.payload = "{\"nome\":\"Douglas\"}";
        req.statusCode = 200;
        req.delayMs = 0;
        req.label = "Teste";

        // Simula geração de hash
        when(hashService.generate())
                .thenReturn("ABC12345");

        // Simula que o hash ainda não existe
        when(repo.existsByHash("ABC12345"))
                .thenReturn(false);

        // Executa método testado
        EndpointResponse response =
                service.create(req);

        // Verifica se o hash retornado é o esperado
        assertEquals("ABC12345", response.hash);

        // Verifica se o endpoint foi salvo
        verify(repo).save(any(MockEndpoint.class));
    }

    // ==========================================
    // TESTA ATUALIZAÇÃO DE ENDPOINT
    // ==========================================

    @Test
    void deveAtualizarEndpoint() {

        // Gera ID para o teste
        UUID id = UUID.randomUUID();

        // Cria endpoint existente
        MockEndpoint endpoint =
                new MockEndpoint();

        endpoint.setId(id);
        endpoint.setPayload("{}");

        // Simula busca bem-sucedida no banco
        when(repo.findById(id))
                .thenReturn(Optional.of(endpoint));

        // Cria nova requisição de atualização
        CreateEndpointRequest req =
                new CreateEndpointRequest();

        req.payload = "{\"novo\":true}";
        req.statusCode = 201;

        // Executa atualização
        EndpointResponse response =
                service.update(id, req);

        // Verifica se o payload foi atualizado
        assertEquals(
                "{\"novo\":true}",
                response.payload
        );

        // Verifica se o endpoint foi salvo novamente
        verify(repo).save(endpoint);
    }

    // ==========================================
    // TESTA EXCLUSÃO DE ENDPOINT
    // ==========================================

    @Test
    void deveExcluirEndpoint() {

        // Gera ID fictício
        UUID id = UUID.randomUUID();

        // Executa exclusão
        service.delete(id);

        // Verifica se o repository recebeu o comando delete
        verify(repo).deleteById(id);
    }

    // ==========================================
    // TESTA BUSCA POR HASH
    // ==========================================

    @Test
    void deveBuscarPorHash() {

        // Cria endpoint de teste
        MockEndpoint endpoint =
                new MockEndpoint();

        endpoint.setHash("ABC12345");

        // Simula busca bem-sucedida
        when(repo.findByHash("ABC12345"))
                .thenReturn(Optional.of(endpoint));

        // Executa busca
        MockEndpoint resultado =
                service.findByHash("ABC12345");

        // Verifica se retornou o hash correto
        assertEquals(
                "ABC12345",
                resultado.getHash()
        );
    }

    // ==========================================
    // TESTA EXCEÇÃO PARA HASH INEXISTENTE
    // ==========================================

    @Test
    void deveLancarErroQuandoHashNaoExiste() {

        // Simula busca sem resultado
        when(repo.findByHash("XXXX"))
                .thenReturn(Optional.empty());

        // Verifica se a exceção esperada é lançada
        assertThrows(
                CustomException.class,
                () -> service.findByHash("XXXX")
        );
    }
}