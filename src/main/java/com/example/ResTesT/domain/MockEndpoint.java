package com.example.ResTesT.domain;

// Importa anotações JPA para mapear esta classe como uma tabela no banco de dados
import jakarta.persistence.*;

// Importa anotações do Lombok para gerar código automaticamente
import lombok.*;

// Importa classes de data/hora, coleções e UUID
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entidade que representa um Endpoint Mockado no banco de dados.
 *
 * O que é uma "entidade"?
 *   É uma classe Java que o framework JPA/Hibernate usa para criar e gerenciar
 *   uma tabela no banco de dados. Cada instância desta classe = uma linha na tabela.
 *
 * O que é um "Mock Endpoint"?
 *   É um endpoint REST criado pelo usuário com payload, status code e delay personalizados.
 *   Após criado, qualquer pessoa pode acessá-lo pelo hash único gerado.
 *
 * Exemplo: o usuário cria um mock com payload {"status": "ok"} e status 200.
 * O sistema gera o hash "aB3xYz12" e o endpoint fica disponível em /api/aB3xYz12.
 */
@Entity             // Diz ao JPA que esta classe representa uma tabela no banco de dados
@Table(name = "mock_endpoints") // Define o nome da tabela no banco (será criada automaticamente)
@Getter             // Lombok: gera automaticamente todos os métodos getXxx() da classe
@Setter             // Lombok: gera automaticamente todos os métodos setXxx() da classe
@NoArgsConstructor  // Lombok: gera um construtor vazio — exigido pelo JPA para funcionar
@AllArgsConstructor // Lombok: gera um construtor com todos os campos — útil em testes
@Builder            // Lombok: permite criar objetos usando o padrão Builder (fluente e legível)
public class MockEndpoint {

    /**
     * Identificador único do endpoint no banco de dados.
     *
     * @Id marca este campo como chave primária da tabela.
     * UUID é um identificador universal — um número de 128 bits praticamente único no mundo,
     * gerado aleatoriamente. Exemplo: "550e8400-e29b-41d4-a716-446655440000"
     *
     * @GeneratedValue instrui o JPA a gerar o valor automaticamente ao salvar.
     */
    @Id
    @GeneratedValue
    private UUID id;

    /**
     * Hash público que identifica este endpoint na URL.
     *
     * É uma string de 8 caracteres aleatórios usada na URL pública (/api/{hash}).
     * Exemplo: "aB3xYz12"
     *
     * nullable = false → o hash é obrigatório, não pode ser nulo
     * unique = true    → não podem existir dois endpoints com o mesmo hash
     * length = 8       → limita o campo no banco a exatamente 8 caracteres
     */
    @Column(nullable = false, unique = true, length = 8)
    private String hash;

    /**
     * Rótulo/nome descritivo que o usuário define para identificar o endpoint.
     *
     * Exemplo: "Mock de login com sucesso" ou "Retorno de lista de produtos"
     * columnDefinition = "TEXT" → sem limite de tamanho no banco
     */
    @Column(columnDefinition = "TEXT")
    private String label;

    /**
     * O payload JSON que será retornado quando alguém acessar /api/{hash}.
     *
     * Exemplo: {"nome": "Douglas", "status": "ativo"}
     * nullable = false → o payload é obrigatório
     */
    @Column(columnDefinition = "TEXT", nullable = false)
    private String payload;

    /**
     * O código de status HTTP que será retornado junto com o payload.
     *
     * Exemplos comuns:
     *   200 = OK (sucesso)
     *   201 = Created (criado com sucesso)
     *   404 = Not Found (não encontrado)
     *   500 = Internal Server Error (erro do servidor)
     *
     * Valor padrão: 200
     */
    @Column(nullable = false)
    private int statusCode = 200;

    /**
     * Tempo de espera artificial (em milissegundos) antes de retornar a resposta.
     *
     * Útil para simular latência de rede ou serviços lentos durante o desenvolvimento.
     * Exemplo: delayMs = 2000 faz o endpoint esperar 2 segundos antes de responder.
     *
     * Valor padrão: 0 (sem delay)
     */
    @Column(nullable = false)
    private int delayMs = 0;

    /**
     * Data e hora em que este endpoint foi criado no sistema.
     * Preenchida automaticamente pelo metodo prePersist() — não precisa ser informada pelo usuário.
     */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /**
     * Data e hora da última modificação deste endpoint.
     * Atualizada automaticamente pelo metodo preUpdate() a cada vez que o endpoint é editado.
     */
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Lista de todos os logs de acesso registrados para este endpoint.
     *
     * Relacionamento @OneToMany: um endpoint pode ter muitos logs.
     * Cada vez que alguém acessa /api/{hash}, um RequestLog é criado e associado aqui.
     *
     * mappedBy = "endpoint" → indica que o campo "endpoint" na classe RequestLog
     *                          é o dono do relacionamento no banco.
     * cascade = ALL         → operações (salvar, deletar) são propagadas para os logs.
     *                          Deletar um endpoint deleta automaticamente todos os seus logs.
     * orphanRemoval = true  → remove logs que ficaram sem endpoint associado.
     * fetch = LAZY          → os logs só são carregados do banco quando você realmente
     *                          acessar esta lista (evita carregar dados desnecessários).
     */
    @OneToMany(
            mappedBy = "endpoint",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<RequestLog> requestLogs = new ArrayList<>();

    /**
     * Executado automaticamente pelo JPA ANTES de salvar um novo registro no banco.
     *
     * @PrePersist é um "callback" do ciclo de vida JPA — o Hibernate chama este metodo
     * automaticamente na primeira vez que o objeto é salvo.
     * Usamos para definir as datas de criação e atualização inicial.
     */
    @PrePersist
    public void prePersist() {
        LocalDateTime agora = LocalDateTime.now();
        this.createdAt = agora; // Registra quando o endpoint foi criado
        this.updatedAt = agora; // updatedAt começa igual ao createdAt na criação
    }

    /**
     * Executado automaticamente pelo JPA ANTES de atualizar um registro existente no banco.
     *
     * @PreUpdate é chamado pelo Hibernate sempre que o objeto sofre uma modificação
     * e vai ser salvo novamente. Atualiza automaticamente o campo updatedAt.
     */
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now(); // Registra o momento exato da última edição
    }
}