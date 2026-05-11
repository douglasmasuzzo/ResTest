package com.example.ResTest.endpoint;

import com.example.ResTest.dto.*;
import com.example.ResTest.service.AuthService;
import com.example.ResTest.dto.LoginRequest;
import com.example.ResTest.dto.RegisterRequest;
import org.springframework.stereotype.Component;


@Component
public class AuthEndpoint {

    private final AuthService service;

    public AuthEndpoint(AuthService service) {
        this.service = service;
    }

    public void register(RegisterRequest req) {
        service.register(req);
    }

    public void login(LoginRequest req) {
        service.login(req);
    }
}