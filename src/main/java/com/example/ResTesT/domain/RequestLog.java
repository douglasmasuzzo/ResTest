package com.example.ResTesT.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "request_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "endpoint_id", nullable = false)
    private MockEndpoint endpoint;

    @Column(nullable = false, length = 10)
    private String method;

    @Column(nullable = false)
    private LocalDateTime calledAt;

    @Column(length = 45)
    private String callerIp;

    @PrePersist
    public void prePersist() {
        this.calledAt = LocalDateTime.now();
    }
}