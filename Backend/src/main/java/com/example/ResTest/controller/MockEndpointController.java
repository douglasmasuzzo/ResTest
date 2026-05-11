package com.example.ResTest.controller;

import com.example.ResTest.dto.CreateEndpointRequest;
import com.example.ResTest.dto.EndpointResponse;
import com.example.ResTest.service.MockEndpointService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/connect")
public class MockEndpointController {

    private final MockEndpointService service;

    public MockEndpointController(MockEndpointService service) {
        this.service = service;
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(
            @RequestBody CreateEndpointRequest req) {

        return ResponseEntity.ok(service.create(req));
    }

    @GetMapping("/all")
    public ResponseEntity<List<EndpointResponse>> all() {

        return ResponseEntity.ok(service.findAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {

        service.delete(id);

        return ResponseEntity.ok("Endpoint deletado");
    }
}