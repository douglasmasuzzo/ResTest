package com.example.ResTesT.controller;

// Importa o service que será utilizado no teste
import com.example.ResTesT.service.MockEndpointService;

// Importa a anotação de teste do JUnit 5
import org.junit.jupiter.api.Test;

// Importa o método mock() do Mockito
// utilizado para criar objetos simulados (mocks)
import static org.mockito.Mockito.mock;

// Classe de teste do MockEndpointController
class MockEndpointControllerTest {

    // ==========================================
    // TESTE DE INSTANCIAÇÃO DO CONTROLLER
    // ==========================================

    @Test
    void deveInstanciarController() {

        // Cria uma versão simulada do service
        // sem necessidade de acessar banco ou lógica real
        MockEndpointService service =
                mock(MockEndpointService.class);

        // Instancia o controller utilizando o mock
        MockEndpointController controller =
                new MockEndpointController(service);

        // Verifica se o objeto foi criado corretamente
        assert controller != null;
    }
}