package com.example.ResTest.service;


import com.example.ResTest.dto.*;
import com.example.ResTest.domain.User;
import com.example.ResTest.dto.LoginRequest;
import com.example.ResTest.dto.RegisterRequest;
import com.example.ResTest.exception.CustomException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.ResTest.repository.UserRepository;

@Service
public class AuthService {

    private final UserRepository repo;
    private final PasswordEncoder encoder;

    public AuthService(UserRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    public void register(RegisterRequest req) {

        if (repo.findByEmail(req.email).isPresent()) {
            throw new CustomException("Email já existe");
        }

        User user = new User();
        user.setEmail(req.email);
        user.setPasswordHash(encoder.encode(req.password));

        repo.save(user);
    }

    public User login(LoginRequest req) {
        User user = repo.findByEmail(req.email)
                .orElseThrow(() -> new CustomException("Usuário não encontrado"));

        if (!encoder.matches(req.password, user.getPasswordHash())) {
            throw new CustomException("Senha inválida");
        }

        return user;
    }
}