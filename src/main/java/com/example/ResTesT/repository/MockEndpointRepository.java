package com.example.ResTesT.repository;

import com.example.ResTesT.domain.MockEndpoint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MockEndpointRepository extends JpaRepository<MockEndpoint, UUID> {
    Optional<MockEndpoint> findByHash(String hash);
    boolean existsByHash(String hash);
}