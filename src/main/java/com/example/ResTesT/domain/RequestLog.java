package com.example.ResTesT.domain;

// Importa anotações JPA para mapear esta classe como tabela no banco de dados
import jakarta.persistence.*;

// Importa anotações do Lombok para geração automática de código
import lombok.*;

// Importa classe de data e hora
import java.time.LocalDateTime;

/**
 * Entidade que representa um Log de Requisição no banco de dados.
 *
 * O que é um "Request Log"?
 *   Sempre que alguém acessa um endpoint mockado (/api/{hash}),
 *   o sistema registra automaticamente quem acessou, quando e como.
 *   Isso permite ao criador do mock ver o histórico de uso do seu endpoint.
 *
 * Informações registradas em cada log:
 *   - Qual endpoint foi acessado
 *   - Qual metodo HTTP foi usado (sempre GET no endpoint público)
 *   - Data e hora do acesso
 *   - Endereço IP de quem fez a requisição
 */
@Entity             // Indica ao JPA que esta classe representa uma tabela no banco
@Table(name = "request_logs") // Nome da tabela no banco de dados
@Getter             // Lombok: gera todos os getters automaticamente
@Setter             // Lombok: gera todos os setters automaticamente
@NoArgsConstructor  // Lombok: gera construtor vazio — obrigatório para o JPA
@AllArgsConstructor // Lombok: gera construtor com todos os campos — útil em testes
@Builder            // Lombok: permite criação de objetos com sintaxe fluente (Builder Pattern)
public class RequestLog {

    /**
     * Identificador único do log no banco de dados.
     *
     * Usamos Long (número inteiro longo) em vez de UUID aqui porque:
     *   - Logs são gerados automaticamente em grande volume
     *   - Long com auto-incremento é mais eficiente para tabelas de histórico
     *   - O usuário nunca precisa referenciar um log pelo ID diretamente
     *
     * GenerationType.IDENTITY instrui o banco a gerar o ID automaticamente
     * usando a estratégia de auto-incremento (1, 2, 3, 4...)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Referência ao endpoint que foi acessado.
     *
     * @ManyToOne: muitos logs podem pertencer a um único endpoint.
     * @JoinColumn: define que a coluna "endpoint_id" na tabela request_logs
     *              armazena o ID do endpoint relacionado (chave estrangeira).
     *
     * fetch = LAZY → o endpoint só é carregado do banco quando necessário,
     *               evitando consultas desnecessárias ao banco.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "endpoint_id", nullable = false)
    private MockEndpoint endpoint;

    /**
     * O metodo HTTP utilizado na requisição.
     *
     * Exemplos: "GET", "POST", "PUT", "DELETE"
     * No endpoint público (/api/{hash}), será sempre "GET".
     *
     * length = 10 → suficiente para qualquer meodo HTTP padrão
     */
    @Column(nullable = false, length = 10)
    private String method;

    /**
     * Data e hora exata em que a requisição foi recebida.
     * Preenchida automaticamente pelo metodo prePersist() — sem intervenção manual.
     */
    @Column(nullable = false)
    private LocalDateTime calledAt;

    /**
     * Endereço IP do cliente que fez a requisição.
     *
     * Exemplo: "192.168.1.100" ou "127.0.0.1" (acesso local)
     *
     * length = 45 → suporta tanto IPv4 (ex: "192.168.1.1") quanto
     *               IPv6 (ex: "2001:0db8:85a3:0000:0000:8a2e:0370:7334"),
     *               que pode ter até 39 caracteres, mais espaço de margem.
     */
    @Column(length = 45)
    private String callerIp;

    /**
     * Executado automaticamente pelo JPA antes de salvar um novo log no banco.
     * Define a data/hora do acesso no momento exato da persistência.
     */
    @PrePersist
    public void prePersist() {
        this.calledAt = LocalDateTime.now();
    }
}