package com.example.ResTesT.domain;

// Importa anotações JPA para mapeamento da entidade no banco
import jakarta.persistence.*;

// Importa anotações do Lombok para reduzir código boilerplate
import lombok.*;

// Importa classe de data e hora
import java.time.LocalDateTime;

@Entity // Define a classe como uma entidade
@Table(name = "request_logs") // Define o nome da tabela no banco

// Gera automaticamente métodos GETTERS e SETTERS
@Getter
@Setter

@NoArgsConstructor // Anotação do lombok: gera construtuor vazio
@AllArgsConstructor //Anotação do lombok: gera construtor com todos os atributos

@Builder // Anotação do lombok: gera construtor com todos os atributos
public class RequestLog {

    @Id // Define a chave primária da entidade
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Gera automaticamente o valor do ID
    private Long id;

    // Relacionamento com a entidade MockEndpoint
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "endpoint_id", nullable = false)
    private MockEndpoint endpoint;

    // MÉTODOS HTTP UTILIZADO NA REQUISIÇÃO
    // Exemplo: GET, POST, PUT, DELETE
    @Column(nullable = false, length = 10)
    private String method;

    // DATA E HORA DA REQUISIÇÃO
    @Column(nullable = false)
    private LocalDateTime calledAt;

    // IP DO CLIENTE QUE FEZ A REQUISIÇÃO
    @Column(length = 45)
    private String callerIp;

    // Executado antes de salvar no banco
    @PrePersist
    public void prePersist() {

        // Define automaticamente a data/hora atual
        this.calledAt = LocalDateTime.now();
    }
}