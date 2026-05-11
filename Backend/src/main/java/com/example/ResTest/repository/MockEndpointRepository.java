package com.example.ResTest.repository;

import com.example.ResTest.domain.MockEndpoint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface MockEndpointRepository extends JpaRepository<MockEndpoint, UUID> {
    Optional<MockEndpoint> findByHash(String hash);
    boolean existsByHash(String hash);
}