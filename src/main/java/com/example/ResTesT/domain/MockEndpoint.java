package com.example.ResTesT.domain;

// Importa anotações JPA para mapeamento da entidade no banco
import jakarta.persistence.*;

// Importa anotações do Lombok para reduzir código boilerplate
import lombok.*;

// Importa classes de data/hora, listas e UUID para geração de identificadores
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity // Define que esta classe é uma entidade
@Table(name = "mock_endpoints") // Define o nome da tabela no banco

// Gera automaticamente métodos GETTERS e SETTERS
@Getter
@Setter

@NoArgsConstructor // Anotação do lombok: gera construtuor vazio
@AllArgsConstructor // Anotação do lombok: gera construtor com todos os atributos

@Builder // Anotação do lombok: permite criar objetos usando Builder Pattern
public class MockEndpoint {

    @Id // Define a chave primária da entidade
    @GeneratedValue // Gera automaticamente o valor do ID
    private UUID id;

    // Campo obrigatório, único e limitado a 8 caracteres ( código hash )
    @Column(nullable = false, unique = true, length = 8)
    private String hash;

    // Campo textual para identificação do endpoint
    @Column(columnDefinition = "TEXT")
    private String label;

    // Payload JSON retornado pelo endpoint mockado
    @Column(columnDefinition = "TEXT", nullable = false)
    private String payload;

    // Código HTTP da resposta
    @Column(nullable = false)
    private int statusCode = 200;

    // Tempo de delay da resposta em milissegundos
    @Column(nullable = false)
    private int delayMs = 0;

    // Data de criação do registro
    @Column(nullable = false)
    private LocalDateTime createdAt;

    // Data da última atualização do registro
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Relacionmento com a etnidade RequestLog
    @OneToMany(

            // Campo da outra entidade responsável pelo relacionamento
            mappedBy = "endpoint",

            // Operações em cascata
            // Exemplo: deletar endpoint deleta os logs relacionados
            cascade = CascadeType.ALL,

            // Remove registros órfãos automaticamente
            orphanRemoval = true
    )

    // Lista de logs associados ao endpoint
    private List<RequestLog> requestLogs = new ArrayList<>();


    // Executado antes de salvar no banco
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now(); // Define data de criação
        this.updatedAt = LocalDateTime.now(); // Define data de atualização incial
    }

    // Executado antes de atualizar no banco
    @PreUpdate
    public void preUpdate() {

        // Atualiza automaticamente a data de modificação
        this.updatedAt = LocalDateTime.now();
    }
}