package com.example.ResTesT.service;

// Importa a entidade utilizada no teste
import com.example.ResTesT.domain.MockEndpoint;

// Importa a entidade de log para verificação do tipo salvo
import com.example.ResTesT.domain.RequestLog;

// Importa o repository que será simulado
import com.example.ResTesT.repository.RequestLogRepository;

// Importa anotação de teste do JUnit 5
import org.junit.jupiter.api.Test;

// Importa matcher do Mockito para aceitar qualquer objeto
import static org.mockito.ArgumentMatchers.any;

// Importa métodos do Mockito para criação e verificação de mocks
import static org.mockito.Mockito.*;

// Importa métodos de validação do JUnit
import static org.junit.jupiter.api.Assertions.*;

// Classe de testes do RequestLogService
class RequestLogServiceTest {

    // ==========================================
    // DEPENDÊNCIA MOCKADA
    // ==========================================

    // Simula o repository sem acessar banco de dados
    private final RequestLogRepository repo =
            mock(RequestLogRepository.class);

    // Instância do serviço que será testado
    private final RequestLogService service =
            new RequestLogService(repo);

    // ==========================================
    // TESTA SALVAMENTO DE LOG
    // ==========================================

    @Test
    void deveSalvarLog() {

        // Cria endpoint fictício para o teste
        MockEndpoint endpoint = new MockEndpoint();

        // Executa o metodo responsável por registrar o log
        service.log(endpoint, "127.0.0.1");

        // Verifica se o repository recebeu
        // uma chamada para salvar o log
        verify(repo).save(any());
    }

    @Test
    void deveSalvarLogComTipoCorreto() {

        // Cria endpoint fictício para o teste
        MockEndpoint endpoint = new MockEndpoint();

        // Executa o registro do log
        service.log(endpoint, "192.168.1.100");

        // Verifica se o objeto salvo é do tipo RequestLog
        verify(repo).save(any(RequestLog.class));
    }

    @Test
    void deveSalvarLogParaMultiplosAcessos() {

        // Cria endpoint fictício para o teste
        MockEndpoint endpoint = new MockEndpoint();

        // Simula três acessos consecutivos ao mesmo endpoint
        service.log(endpoint, "10.0.0.1");
        service.log(endpoint, "10.0.0.2");
        service.log(endpoint, "10.0.0.3");

        // Verifica se foram feitos três salvamentos separados no banco
        verify(repo, times(3)).save(any());
    }

    @Test
    void naoDeveLancarExcecaoAoRegistrarLog() {

        // Cria endpoint fictício para o teste
        MockEndpoint endpoint = new MockEndpoint();

        // Verifica se o metodo log() não lança exceção em condições normais
        assertDoesNotThrow(() ->
                service.log(endpoint, "127.0.0.1")
        );
    }
}