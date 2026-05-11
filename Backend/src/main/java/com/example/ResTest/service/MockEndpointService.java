package com.example.ResTest.service;

import com.example.ResTest.dto.*;
import com.example.ResTest.domain.MockEndpoint;
import com.example.ResTest.dto.CreateEndpointRequest;
import com.example.ResTest.dto.EndpointResponse;
import com.example.ResTest.exception.CustomException;
import com.example.ResTest.repository.MockEndpointRepository;
import org.springframework.stereotype.Service;


import java.util.*;

@Service
public class MockEndpointService {

    private final MockEndpointRepository repo;
    private final HashGeneratorService hashService;
    private final PayloadValidatorService validator;

    public MockEndpointService(MockEndpointRepository repo,
                               HashGeneratorService hashService,
                               PayloadValidatorService validator) {
        this.repo = repo;
        this.hashService = hashService;
        this.validator = validator;
    }

    public EndpointResponse create(CreateEndpointRequest req) {

        validator.validate(req.payload);

        String hash;
        int attempts = 0;

        do {
            hash = hashService.generate();
            attempts++;
        } while (repo.existsByHash(hash) && attempts < 5);

        if (attempts == 5) {
            throw new CustomException("Erro ao gerar hash único");
        }

        MockEndpoint e = new MockEndpoint();
        e.setHash(hash);
        e.setPayload(req.payload);
        e.setStatusCode(req.statusCode != null ? req.statusCode : 200);
        e.setDelayMs(req.delayMs != null ? req.delayMs : 0);
        e.setLabel(req.label);

        repo.save(e);

        return toResponse(e);
    }

    public List<EndpointResponse> findAll() {
        return repo.findAll().stream().map(this::toResponse).toList();
    }

    public void delete(UUID id) {
        repo.deleteById(id);
    }

    public MockEndpoint findByHash(String hash) {
        return repo.findByHash(hash)
                .orElseThrow(() -> new CustomException("Endpoint não encontrado"));
    }

    private EndpointResponse toResponse(MockEndpoint e) {
        EndpointResponse res = new EndpointResponse();
        res.id = e.getId();
        res.hash = e.getHash();
        res.url = "/api/" + e.getHash();
        res.payload = e.getPayload();
        res.statusCode = e.getStatusCode();
        res.delayMs = e.getDelayMs();
        res.label = e.getLabel();
        return res;
    }
}