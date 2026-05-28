package com.example.ResTesT.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "mock_endpoints")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MockEndpoint {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true, length = 8)
    private String hash;

    @Column(columnDefinition = "TEXT")
    private String label;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String payload;

    @Column(nullable = false)
    private int statusCode = 200;

    @Column(nullable = false)
    private int delayMs = 0;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // RELACIONAMENTO COM REQUEST LOGS
    @OneToMany(
            mappedBy = "endpoint",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<RequestLog> requestLogs = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}