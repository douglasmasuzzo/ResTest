package com.example.ResTesT.service;

// Importa a entidade utilizada no teste
import com.example.ResTesT.domain.MockEndpoint;

// Importa o repository que será simulado
import com.example.ResTesT.repository.RequestLogRepository;

// Importa anotação de teste do JUnit 5
import org.junit.jupiter.api.Test;

// Importa matcher do Mockito para aceitar qualquer objeto
import static org.mockito.ArgumentMatchers.any;

// Importa métodos do Mockito para criação e verificação de mocks
import static org.mockito.Mockito.*;

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

        // Executa o método responsável por registrar o log
        service.log(endpoint, "127.0.0.1");

        // Verifica se o repository recebeu
        // uma chamada para salvar o log
        verify(repo).save(any());
    }
}