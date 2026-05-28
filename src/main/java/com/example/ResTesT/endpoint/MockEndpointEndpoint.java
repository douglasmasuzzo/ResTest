package com.example.ResTesT.endpoint;


import com.example.ResTesT.dto.CreateEndpointRequest;
import com.example.ResTesT.dto.EndpointResponse;
import com.example.ResTesT.service.MockEndpointService;
import org.springframework.stereotype.Component;

import java.util.List;

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

}