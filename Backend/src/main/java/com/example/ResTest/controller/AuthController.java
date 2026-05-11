package com.example.ResTest.controller;

import com.example.ResTest.domain.User;
import com.example.ResTest.dto.LoginRequest;
import com.example.ResTest.dto.RegisterRequest;
import com.example.ResTest.service.AuthService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {

        service.register(req);

        return ResponseEntity.ok("Usuário criado com sucesso");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {

        User user = service.login(req);

        return ResponseEntity.ok(user);
    }
}