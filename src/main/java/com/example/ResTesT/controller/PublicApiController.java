package com.example.ResTesT.controller;


// Importa classe da entidade MockEndpoint
import com.example.ResTesT.domain.MockEndpoint;

// Importa os services com as lógicas das entidades MockEndpoint e RequestLog
import com.example.ResTesT.service.MockEndpointService;
import com.example.ResTesT.service.RequestLogService;

// Importa objeto da requisição HTTP para capturar informações do cliente
import jakarta.servlet.http.HttpServletRequest;

// Importa das dependências do pom.xml para manipulação de respostas HTTP
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

// Importa anotações REST das dependências do pom.xml
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Define esta classe como um controller REST
@RestController

// Define o caminho base dos endpoints
@RequestMapping("/api")
public class PublicApiController {

    private final MockEndpointService service; // Service responsável pelos endpoints mockados
    private final RequestLogService logService; // Service responsável pelos logs de requisição

    // Injeção de dependências do service via construtor
    public PublicApiController(MockEndpointService service, RequestLogService logService) {
        this.service = service;
        this.logService = logService;
    }

    // Endpoint GET dinâmico baseado no hash
    @GetMapping("/{hash}")
    public ResponseEntity<?> get(@PathVariable String hash,
                                HttpServletRequest request) throws Exception {

        // Busca o endpoint mockado pelo hash informado na URL
        MockEndpoint e = service.findByHash(hash);

        // Verifica se existe delay configurado
        if (e.getDelayMs() > 0) {

            // Pausa a resposta pelo tempo configurado
            Thread.sleep(e.getDelayMs());
        }

        // Registra a requisição no sistema de logs
        // incluindo o IP do cliente
        logService.log(e, request.getRemoteAddr());

        // Retorna a resposta configurada no endpoint mockado
        return ResponseEntity
                .status(e.getStatusCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(e.getPayload());
    }

}