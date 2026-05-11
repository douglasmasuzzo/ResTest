package com.example.ResTest.service;

import com.example.ResTest.domain.*;
import com.example.ResTest.domain.MockEndpoint;
import com.example.ResTest.domain.RequestLog;
import org.springframework.stereotype.Service;
import com.example.ResTest.repository.RequestLogRepository;

@Service
public class RequestLogService {

    private final RequestLogRepository repo;

    public RequestLogService(RequestLogRepository repo) {
        this.repo = repo;
    }

    public void log(MockEndpoint endpoint, String ip) {
        RequestLog log = new RequestLog();
        log.setEndpoint(endpoint);
        log.setMethod("GET");
        log.setCallerIp(ip);
        repo.save(log);
    }
}