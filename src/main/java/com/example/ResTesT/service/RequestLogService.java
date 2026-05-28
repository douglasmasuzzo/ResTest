package com.example.ResTesT.service;


import com.example.ResTesT.domain.MockEndpoint;
import com.example.ResTesT.domain.RequestLog;
import com.example.ResTesT.repository.RequestLogRepository;
import org.springframework.stereotype.Service;

@Service
public class RequestLogService {

    // Repositório responsável por salvar logs no banco de dados
    private final RequestLogRepository repo;

    public RequestLogService(RequestLogRepository repo) {
        this.repo = repo;
    }

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