package com.example.ResTesT.dto;

// Importa as anotações de validação do Jakarta Bean Validation
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

// Importa anotações do Lombok para geração automática de código
import lombok.Getter;
import lombok.Setter;

/**
 * DTO (Data Transfer Object) de criação de endpoint.
 *
 * O que é um DTO?
 *   É um objeto simples usado para transportar dados entre camadas da aplicação.
 *   Neste caso, representa o corpo (body) da requisição HTTP que o cliente envia
 *   para criar ou atualizar um mock endpoint.
 *
 * Por que usar DTO em vez de usar a entidade diretamente?
 *   - Separa o que o cliente pode enviar do que existe no banco de dados
 *   - Permite validar os campos antes de chegar na lógica de negócio
 *   - Evita que o cliente envie campos que não deveria (ex: id, createdAt)
 *
 * Exemplo de corpo JSON que o cliente envia:
 * {
 *   "payload":    "{\"nome\": \"Douglas\", \"status\": \"ativo\"}",
 *   "statusCode": 200,
 *   "delayMs":    500,
 *   "label":      "Mock de usuário ativo"
 * }
 */
@Getter // Lombok: gera automaticamente os métodos getPayload(), getStatusCode(), etc.
@Setter // Lombok: gera automaticamente os métodos setPayload(), setStatusCode(), etc.
public class CreateEndpointRequest {

    /**
     * O conteúdo JSON que será retornado pelo endpoint mockado.
     *
     * @NotBlank garante que:
     *   - O campo foi enviado (não é nulo)
     *   - O campo não está vazio ("")
     *   - O campo não contém apenas espaços em branco ("   ")
     *
     * Sem essa validação, uma requisição com payload nulo causaria
     * um NullPointerException no serviço, em vez de uma mensagem de erro clara.
     *
     * Exemplo válido:   "{\"produto\": \"notebook\", \"preco\": 2500}"
     * Exemplos inválidos: null, "", "   "
     */
    @NotBlank(message = "O payload não pode ser vazio")
    private String payload;

    /**
     * O código de status HTTP que o endpoint retornará.
     *
     * @Min(100) → status code mínimo válido (informational responses)
     * @Max(599) → status code máximo válido (server error responses)
     *
     * O campo é Integer (com I maiúsculo) em vez de int (primitivo)
     * porque pode ser nulo — se não informado, o serviço usa 200 como padrão.
     *
     * Exemplos válidos: 200, 201, 400, 404, 500
     * Exemplos inválidos: 0, 99, 600, 999
     */
    @Min(value = 100, message = "O status code mínimo permitido é 100")
    @Max(value = 599, message = "O status code máximo permitido é 599")
    private Integer statusCode;

    /**
     * Tempo de delay artificial em milissegundos antes de retornar a resposta.
     *
     * @Min(0)     → não faz sentido ter delay negativo
     * @Max(30000) → limitamos a 30 segundos para evitar que o servidor
     *               fique com threads presas por tempo indefinido
     *
     * Exemplos de uso: simular uma API lenta (delayMs=3000 = 3 segundos)
     */
    @Min(value = 0, message = "O delay não pode ser negativo")
    @Max(value = 30000, message = "O delay máximo permitido é 30.000ms (30 segundos)")
    private Integer delayMs;

    /**
     * Nome ou descrição do endpoint mockado — definido pelo usuário.
     *
     * Serve para identificar facilmente o propósito do mock.
     * Exemplo: "Retorno de login com sucesso" ou "Erro 404 de produto não encontrado"
     *
     * Campo opcional — pode ser nulo ou vazio.
     */
    private String label;
}