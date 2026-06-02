package com.example.ResTesT.dto;

// Importa anotações do Lombok para geração automática de código
import lombok.Getter;
import lombok.Setter;

// Importa UUID para representar o identificador único do endpoint
import java.util.UUID;

/**
 * DTO (Data Transfer Object) de resposta do endpoint.
 *
 * O que é este objeto?
 *   Representa o JSON que a API retorna ao cliente após uma operação
 *   de criação, atualização ou listagem de endpoints mockados.
 *
 * Por que não retornar a entidade MockEndpoint diretamente?
 *   - A entidade tem campos internos (como requestLogs, createdAt, updatedAt)
 *     que não precisam ser expostos ao cliente
 *   - Retornar a entidade diretamente pode causar problemas com
 *     o carregamento LAZY dos relacionamentos (LazyInitializationException)
 *   - O DTO nos dá controle total sobre o que o cliente enxerga
 *
 * Exemplo de JSON retornado:
 * {
 *   "id":         "550e8400-e29b-41d4-a716-446655440000",
 *   "hash":       "aB3xYz12",
 *   "url":        "/api/aB3xYz12",
 *   "payload":    "{\"status\": \"ok\"}",
 *   "statusCode": 200,
 *   "delayMs":    0,
 *   "label":      "Mock de login com sucesso"
 * }
 */
@Getter // Lombok: gera automaticamente todos os métodos get (ex: getId(), getHash())
@Setter // Lombok: gera automaticamente todos os métodos set (ex: setId(), setHash())
public class EndpointResponse {

    /**
     * Identificador único do endpoint no banco de dados.
     * Gerado automaticamente pelo sistema — o cliente não define este valor.
     */
    private UUID id;

    /**
     * Código hash de 8 caracteres que identifica o endpoint na URL pública.
     * Gerado automaticamente pelo HashGeneratorService.
     * Exemplo: "aB3xYz12"
     */
    private String hash;

    /**
     * URL completa onde o endpoint mockado pode ser acessado.
     * Montada automaticamente pelo serviço a partir do hash.
     * Exemplo: "/api/aB3xYz12"
     */
    private String url;

    /**
     * O payload JSON que será retornado quando alguém acessar o endpoint.
     * Exatamente o mesmo valor enviado na criação.
     */
    private String payload;

    /**
     * O código de status HTTP configurado para este endpoint.
     * Padrão: 200 (OK)
     */
    private int statusCode;

    /**
     * O tempo de delay configurado em milissegundos.
     * Padrão: 0 (sem delay)
     */
    private int delayMs;

    /**
     * O nome/descrição do endpoint definido pelo usuário.
     * Pode ser nulo caso não tenha sido informado na criação.
     */
    private String label;
}