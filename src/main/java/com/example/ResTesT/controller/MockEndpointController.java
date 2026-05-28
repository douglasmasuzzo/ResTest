package com.example.ResTesT.controller;


import com.example.ResTesT.dto.CreateEndpointRequest;
import com.example.ResTesT.dto.EndpointResponse;
import com.example.ResTesT.service.MockEndpointService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/mock")
public class MockEndpointController {

    private final MockEndpointService service;

    public MockEndpointController(MockEndpointService service) {
        this.service = service;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<EndpointResponse> create(
            @RequestBody CreateEndpointRequest req) {

        return ResponseEntity.ok(service.create(req));
    }

    // LIST
    @GetMapping
    public ResponseEntity<?> list() {
        return ResponseEntity.ok(service.findAll());
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable UUID id,
            @RequestBody CreateEndpointRequest req
    ) {

        return ResponseEntity.ok(service.update(id, req));
    }
    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarMock(@PathVariable UUID id) {

        service.delete(id);

        return ResponseEntity.noContent().build();
    }

}