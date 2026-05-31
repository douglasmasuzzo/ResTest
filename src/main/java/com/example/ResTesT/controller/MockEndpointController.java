package com.example.ResTesT.controller;

// Importando classes do repository
import com.example.ResTesT.dto.CreateEndpointRequest;
import com.example.ResTesT.dto.EndpointResponse;

// Importando service com a lógica do mockendpoint
import com.example.ResTesT.service.MockEndpointService;

// Importando dependências do pom.xml
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Importa classe UUID para gerar identificadores
import java.util.UUID;


@RestController // Define que esta classe é um controller REST
@RequestMapping("/mock") // Define o caminho base para todos os endpoints deste controller
public class MockEndpointController {

    private final MockEndpointService service; // Service responsável pelas regras de negócio

    public MockEndpointController(MockEndpointService service) { // Injeção de dependência do service pelo construtor
        this.service = service;
    }

    // CREATE - Cria novo mock
    @PostMapping
    public ResponseEntity<EndpointResponse> create(

            // Recebe os dados enviados no corpo da requisição
            @RequestBody CreateEndpointRequest req) {

        // Chama o service para criar o mock
        // e retorna resposta HTTP 200 OK
        return ResponseEntity.ok(service.create(req));
    }

    // LIST -- Lista todos os mocks
    @GetMapping
    public ResponseEntity<?> list() {

        // Retorna todos os endpoints cadastrados
        return ResponseEntity.ok(service.findAll());
    }

    // UPDATE -- Atualiza um mock
    @PutMapping("/{id}")
    public ResponseEntity<?> update(

            // Recebe o ID do endpoint pela URL
            @PathVariable UUID id,

            // Recebe os novos dados no corpo da requisição
            @RequestBody CreateEndpointRequest req
    ) {

        // Atualiza o endpoint com base no ID informado
        return ResponseEntity.ok(service.update(id, req));
    }
    // DELETE -- Deleta um mock
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarMock(

            // Recebe o ID do endpoint pela URL
            @PathVariable UUID id) {

        // Chama o service para deletar o endpoint
        service.delete(id);

        // Retorna HTTP 204 No Content
        // indicando que a exclusão foi realizada com sucesso
        return ResponseEntity.noContent().build();
    }

}