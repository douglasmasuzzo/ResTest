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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

// Importa métodos de validação do JUnit
import static org.junit.jupiter.api.Assertions.*;

// Importa recursos do Mockito para mocks e verificações
import static org.mockito.ArgumentMatchers.any;
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

        // Cria objeto de requisição com os dados do novo endpoint
        CreateEndpointRequest req =
                new CreateEndpointRequest();

        req.setPayload("{\"nome\":\"Douglas\"}");
        req.setStatusCode(200);
        req.setDelayMs(0);
        req.setLabel("Teste");

        // Simula geração de hash
        when(hashService.generate())
                .thenReturn("ABC12345");

        // Simula que o hash ainda não existe no banco
        when(repo.existsByHash("ABC12345"))
                .thenReturn(false);

        // Executa método testado
        EndpointResponse response =
                service.create(req);

        // Verifica se o hash retornado é o esperado
        assertEquals("ABC12345", response.getHash());

        // Verifica se a URL foi montada corretamente
        assertEquals("/api/ABC12345", response.getUrl());

        // Verifica se o endpoint foi salvo no repositório
        verify(repo).save(any(MockEndpoint.class));
    }

    @Test
    void deveCriarEndpointComValoresPadrao() {

        // Cria requisição apenas com o payload obrigatório
        // statusCode e delayMs ficam nulos — o service deve defaultar para 200 e 0
        CreateEndpointRequest req =
                new CreateEndpointRequest();

        req.setPayload("{\"padrao\":true}");

        // Simula geração de hash disponível
        when(hashService.generate())
                .thenReturn("DEF67890");

        when(repo.existsByHash("DEF67890"))
                .thenReturn(false);

        // Executa criação
        EndpointResponse response =
                service.create(req);

        // Verifica se os valores padrão foram aplicados corretamente
        assertEquals(200, response.getStatusCode());
        assertEquals(0, response.getDelayMs());
    }

    @Test
    void deveTentarNovoHashQuandoHashJaExiste() {

        // Cria objeto de requisição
        CreateEndpointRequest req =
                new CreateEndpointRequest();

        req.setPayload("{\"colisao\":true}");

        // Simula colisão na primeira tentativa e sucesso na segunda
        when(hashService.generate())
                .thenReturn("COLISAO1")  // 1ª tentativa: já existe
                .thenReturn("UNICO123"); // 2ª tentativa: disponível

        when(repo.existsByHash("COLISAO1")).thenReturn(true);
        when(repo.existsByHash("UNICO123")).thenReturn(false);

        // Executa criação
        EndpointResponse response =
                service.create(req);

        // Verifica que o hash disponível foi utilizado
        assertEquals("UNICO123", response.getHash());

        // Verifica que generate() foi chamado duas vezes devido à colisão
        verify(hashService, times(2)).generate();
    }

    @Test
    void deveLancarExcecaoAposCincoTentativasDeHash() {

        // Cria objeto de requisição
        CreateEndpointRequest req =
                new CreateEndpointRequest();

        req.setPayload("{\"falha\":true}");

        // Simula todas as tentativas retornando hash já existente
        when(hashService.generate()).thenReturn("COLISAO!");
        when(repo.existsByHash("COLISAO!")).thenReturn(true);

        // Verifica se a exceção é lançada após esgotar as tentativas
        assertThrows(
                CustomException.class,
                () -> service.create(req)
        );

        // Verifica que tentou exatamente 5 vezes antes de desistir
        verify(hashService, times(5)).generate();

        // Verifica que nenhum endpoint foi salvo no banco
        verify(repo, never()).save(any());
    }

    // ==========================================
    // TESTA LISTAGEM DE ENDPOINTS
    // ==========================================

    @Test
    void deveRetornarTodosOsEndpoints() {

        // Cria endpoints de teste
        MockEndpoint ep1 = new MockEndpoint();
        ep1.setHash("HASH0001");
        ep1.setPayload("{\"a\":1}");
        ep1.setStatusCode(200);

        MockEndpoint ep2 = new MockEndpoint();
        ep2.setHash("HASH0002");
        ep2.setPayload("{\"b\":2}");
        ep2.setStatusCode(404);

        // Simula retorno da lista pelo repositório
        when(repo.findAll())
                .thenReturn(List.of(ep1, ep2));

        // Executa listagem
        List<EndpointResponse> resultado =
                service.findAll();

        // Verifica se todos os endpoints foram retornados
        assertEquals(2, resultado.size());
        assertEquals("HASH0001", resultado.get(0).getHash());
        assertEquals("HASH0002", resultado.get(1).getHash());
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHaEndpoints() {

        // Simula repositório vazio
        when(repo.findAll())
                .thenReturn(List.of());

        // Executa listagem
        List<EndpointResponse> resultado =
                service.findAll();

        // Verifica se a lista retornada está vazia
        assertTrue(resultado.isEmpty());
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

        // Simula busca bem-sucedida no banco
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

        // Simula busca sem resultado no banco
        when(repo.findByHash("XXXX"))
                .thenReturn(Optional.empty());

        // Verifica se a exceção esperada é lançada
        CustomException ex = assertThrows(
                CustomException.class,
                () -> service.findByHash("XXXX")
        );

        // Verifica se a mensagem de erro contém o hash que causou o problema
        assertTrue(ex.getMessage().contains("XXXX"));
    }

    // ==========================================
    // TESTA ATUALIZAÇÃO DE ENDPOINT
    // ==========================================

    @Test
    void deveAtualizarEndpoint() {

        // Gera ID para o teste
        UUID id = UUID.randomUUID();

        // Cria endpoint existente no banco
        MockEndpoint endpoint =
                new MockEndpoint();

        endpoint.setHash("HASHX");
        endpoint.setPayload("{\"antigo\":true}");
        endpoint.setStatusCode(200);
        endpoint.setDelayMs(0);

        // Simula busca bem-sucedida no banco
        when(repo.findById(id))
                .thenReturn(Optional.of(endpoint));

        // Cria nova requisição de atualização
        CreateEndpointRequest req =
                new CreateEndpointRequest();

        req.setPayload("{\"novo\":true}");
        req.setStatusCode(201);
        req.setLabel("Label atualizado");

        // Executa atualização
        EndpointResponse response =
                service.update(id, req);

        // Verifica se o payload foi atualizado corretamente
        assertEquals(
                "{\"novo\":true}",
                response.getPayload()
        );

        // Verifica se o endpoint foi salvo novamente
        verify(repo).save(endpoint);
    }

    @Test
    void deveLancarExcecaoAoAtualizarIdInexistente() {

        // Gera ID fictício que não existe no banco
        UUID id = UUID.randomUUID();

        // Simula busca sem resultado no banco
        when(repo.findById(id))
                .thenReturn(Optional.empty());

        // Cria requisição de atualização
        CreateEndpointRequest req =
                new CreateEndpointRequest();

        req.setPayload("{\"x\":1}");

        // Verifica se a exceção esperada é lançada
        CustomException ex = assertThrows(
                CustomException.class,
                () -> service.update(id, req)
        );

        // Verifica se a mensagem contém o ID que causou o problema
        assertTrue(ex.getMessage().contains(id.toString()));

        // Verifica que nenhum dado foi salvo no banco
        verify(repo, never()).save(any());
    }

    @Test
    void deveManterStatusCodeAntigoSeNaoInformadoNaAtualizacao() {

        // Gera ID para o teste
        UUID id = UUID.randomUUID();

        // Cria endpoint com status 404 já salvo no banco
        MockEndpoint endpoint =
                new MockEndpoint();

        endpoint.setStatusCode(404);

        // Simula busca bem-sucedida no banco
        when(repo.findById(id))
                .thenReturn(Optional.of(endpoint));

        // Cria requisição sem statusCode — deve manter o valor anterior (404)
        CreateEndpointRequest req =
                new CreateEndpointRequest();

        req.setPayload("{\"manteve\":true}");

        // Executa atualização
        EndpointResponse response =
                service.update(id, req);

        // Verifica se o status original foi mantido
        assertEquals(404, response.getStatusCode());
    }

    // ==========================================
    // TESTA EXCLUSÃO DE ENDPOINT
    // ==========================================

    @Test
    void deveExcluirEndpoint() {

        // Gera ID fictício
        UUID id = UUID.randomUUID();

        // Simula que o endpoint existe no banco
        when(repo.existsById(id))
                .thenReturn(true);

        // Configura o mock para não fazer nada ao deletar (void)
        doNothing().when(repo).deleteById(id);

        // Executa exclusão — não deve lançar exceção
        assertDoesNotThrow(() -> service.delete(id));

        // Verifica se o repository recebeu o comando delete
        verify(repo, times(1)).deleteById(id);
    }

    @Test
    void deveLancarExcecaoAoDeletarIdInexistente() {

        // Gera ID fictício que não existe no banco
        UUID id = UUID.randomUUID();

        // Simula que o endpoint NÃO existe no banco
        when(repo.existsById(id))
                .thenReturn(false);

        // Verifica se a exceção esperada é lançada
        CustomException ex = assertThrows(
                CustomException.class,
                () -> service.delete(id)
        );

        // Verifica se a mensagem contém o ID que causou o problema
        assertTrue(ex.getMessage().contains(id.toString()));

        // Verifica que deleteById nunca foi chamado
        verify(repo, never()).deleteById(any());
    }
}