package com.example.ResTesT.endpoint;

// Importa os DTOs utilizados para entrada e saída de dados
import com.example.ResTesT.dto.CreateEndpointRequest;
import com.example.ResTesT.dto.EndpointResponse;

// Importa o service responsável pelas lógica do mockendpoint
import com.example.ResTesT.service.MockEndpointService;

// Importa anotação para registrar a classe como componente Spring
import org.springframework.stereotype.Component;

// Importa a interface List
import java.util.List;

@Component // Define essa classe como um componente gerenciado pelo spring automaticamente
public class MockEndpointEndpoint {

    // Service responsável pelas operações dos endpoints mockados
    private final MockEndpointService service;

    // Injeção de dependência via construtor
    public MockEndpointEndpoint(MockEndpointService service) {
        this.service = service;
    }

    // Cria um novo endpoint mockado
    public EndpointResponse create(CreateEndpointRequest req) {

        // Chama o service para criar o endpoint
        return service.create(req);
    }

    // Lista todos os endpoints mockados
    public List<EndpointResponse> findAll() {

        // Retorna todos os endpoints cadastrados
        return service.findAll();
    }

}