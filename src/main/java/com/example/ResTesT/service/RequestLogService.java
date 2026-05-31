package com.example.ResTesT.service;

// Importa a entidade MockEndpoint
import com.example.ResTesT.domain.MockEndpoint;

// Importa a entidade RequestLog
import com.example.ResTesT.domain.RequestLog;

// Importa o repository responsável pelos logs
import com.example.ResTesT.repository.RequestLogRepository;

// Importa anotação para registrar a classe como service Spring
import org.springframework.stereotype.Service;

@Service // Define a classe como camada de serviço responsável pela lógica de logs dos endpoints
public class RequestLogService {

    // Repositório responsável por salvar logs no banco de dados
    private final RequestLogRepository repo;

    // Injeção de dependência via construtor
    public RequestLogService(RequestLogRepository repo) {
        this.repo = repo;
    }

    // Registra novo log de requisição
    public void log(MockEndpoint endpoint, String ip) {

        // Cria novo objeto de log
        RequestLog log = new RequestLog();

        // Define qual endpoint foi acessado
        log.setEndpoint(endpoint);


        // Define o metodo HTTP utilizado
        // Neste caso, GET
        log.setMethod("GET");

        // Define o IP de quem fez a requisição
        log.setCallerIp(ip);

        // Salva o log no banco de dados
        repo.save(log);
    }
}