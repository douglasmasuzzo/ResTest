package com.example.ResTest.endpoint;

import com.example.ResTest.dto.*;
import com.example.ResTest.service.MockEndpointService;
import com.example.ResTest.dto.CreateEndpointRequest;
import com.example.ResTest.dto.EndpointResponse;
import org.springframework.stereotype.Component;


import java.util.*;

@Component
public class MockEndpointEndpoint {

    private final MockEndpointService service;

    public MockEndpointEndpoint(MockEndpointService service) {
        this.service = service;
    }

    public EndpointResponse create(CreateEndpointRequest req) {
        return service.create(req);
    }

    public List<EndpointResponse> findAll() {
        return service.findAll();
    }

    public void delete(UUID id) {
        service.delete(id);
    }
}