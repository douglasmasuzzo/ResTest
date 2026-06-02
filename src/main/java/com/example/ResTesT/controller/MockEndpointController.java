package com.example.ResTesT.controller;

// Importa os DTOs de entrada e saída de dados
import com.example.ResTesT.dto.CreateEndpointRequest;
import com.example.ResTesT.dto.EndpointResponse;

// Importa o serviço com a lógica de negócio dos endpoints mockados
import com.example.ResTesT.service.MockEndpointService;

// Importa as classes do Spring para construir respostas HTTP e receber dados
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Importa @Valid para ativar a validação automática dos campos do DTO
import jakarta.validation.Valid;

// Importa UUID para o tipo do identificador dos endpoints
import java.util.UUID;

/**
 * Controller responsável pelo gerenciamento de endpoints mockados.
 *
 * O que é um Controller?
 *   É a camada que recebe as requisições HTTP, extrai os dados necessários
 *   e delega o processamento para o Service. Após o processamento,
 *   devolve a resposta HTTP adequada para o cliente.
 *
 * Rotas gerenciadas (todas protegidas por API Key via SecurityConfig):
 *   POST   /mock         → cria um novo endpoint mockado
 *   GET    /mock         → lista todos os endpoints cadastrados
 *   PUT    /mock/{id}    → atualiza um endpoint existente pelo ID
 *   DELETE /mock/{id}    → remove um endpoint pelo ID
 *
 * Como autenticar?
 *   Todas as rotas abaixo exigem o header: X-API-Key: restest-chave-secreta-2025
 *   Sem este header, a requisição retorna HTTP 401 (Unauthorized).
 */
@RestController    // Marca esta classe como Controller REST — retorna JSON automaticamente
@RequestMapping("/mock") // Todas as rotas deste controller começam com /mock
public class MockEndpointController {

    /**
     * Serviço com toda a lógica de negócio dos endpoints mockados.
     * final garante que a referência não pode ser trocada após a injeção.
     */
    private final MockEndpointService service;

    /**
     * Construtor com injeção de dependência do serviço.
     *
     * O Spring injeta automaticamente a instância de MockEndpointService
     * quando cria este Controller. Usar o construtor (em vez de @Autowired
     * no campo) é a prática recomendada — facilita testes e torna a
     * dependência explícita e obrigatória.
     *
     * @param service o serviço de endpoints mockados injetado pelo Spring
     */
    public MockEndpointController(MockEndpointService service) {
        this.service = service;
    }


    // ─────────────────────────────────────────────────────────────────────────────
    // POST /mock — Criação de novo endpoint
    // ─────────────────────────────────────────────────────────────────────────────

    /**
     * Cria um novo endpoint mockado com os dados enviados no corpo da requisição.
     *
     * @PostMapping indica que este método responde a requisições HTTP POST em /mock.
     *
     * @Valid ativa a validação das anotações do DTO (ex: @NotBlank, @Min, @Max).
     *   Se alguma validação falhar, o GlobalExceptionHandler intercepta e retorna
     *   HTTP 400 com os detalhes dos erros — sem chegar neste método.
     *
     * @RequestBody instrui o Spring a converter o JSON do corpo da requisição
     *   automaticamente para um objeto CreateEndpointRequest.
     *
     * @param request os dados do novo endpoint enviados pelo cliente
     * @return HTTP 201 Created com o DTO do endpoint recém-criado no corpo
     */
    @PostMapping
    public ResponseEntity<EndpointResponse> create(@Valid @RequestBody CreateEndpointRequest request) {

        // Delega a criação para o serviço — toda a lógica fica no Service
        EndpointResponse response = service.create(request);

        // HTTP 201 Created é o status correto para recursos recém-criados
        // (diferente de 200 OK, que indica apenas que a operação foi bem-sucedida)
        return ResponseEntity.status(201).body(response);
    }


    // ─────────────────────────────────────────────────────────────────────────────
    // GET /mock — Listagem de todos os endpoints
    // ─────────────────────────────────────────────────────────────────────────────

    /**
     * Retorna a lista de todos os endpoints mockados cadastrados no sistema.
     *
     * @GetMapping indica que este método responde a requisições HTTP GET em /mock.
     *
     * @return HTTP 200 OK com a lista de endpoints no corpo (pode ser lista vazia)
     */
    @GetMapping
    public ResponseEntity<?> list() {

        // Retorna todos os endpoints convertidos para DTO
        // ResponseEntity.ok() define automaticamente o status 200 OK
        return ResponseEntity.ok(service.findAll());
    }


    // ─────────────────────────────────────────────────────────────────────────────
    // PUT /mock/{id} — Atualização de endpoint existente
    // ─────────────────────────────────────────────────────────────────────────────

    /**
     * Atualiza os dados de um endpoint mockado existente.
     *
     * @PutMapping indica que este método responde a requisições HTTP PUT em /mock/{id}.
     * PUT é o método HTTP semântico para atualizações completas de um recurso.
     *
     * @PathVariable extrai o {id} da URL e converte automaticamente para UUID.
     *   Exemplo: PUT /mock/550e8400-e29b-41d4-a716-446655440000
     *
     * @param id      o UUID do endpoint a ser atualizado, extraído da URL
     * @param request os novos dados a serem aplicados, recebidos no corpo JSON
     * @return HTTP 200 OK com o DTO do endpoint atualizado no corpo
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id,
                                    @Valid @RequestBody CreateEndpointRequest request) {

        // Delega a atualização para o serviço — lança CustomException se o ID não existir
        return ResponseEntity.ok(service.update(id, request));
    }


    // ─────────────────────────────────────────────────────────────────────────────
    // DELETE /mock/{id} — Remoção de endpoint
    // ─────────────────────────────────────────────────────────────────────────────

    /**
     * Remove permanentemente um endpoint mockado do sistema.
     *
     * @DeleteMapping indica que este método responde a requisições HTTP DELETE em /mock/{id}.
     *
     * Por que retornar 204 No Content em vez de 200 OK?
     *   HTTP 204 significa "operação realizada com sucesso, mas não há conteúdo a retornar".
     *   É o status semântico correto para deleções bem-sucedidas — não faz sentido
     *   retornar dados de algo que acabou de ser removido.
     *
     * @param id o UUID do endpoint a ser removido, extraído da URL
     * @return HTTP 204 No Content (sem corpo na resposta)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {

        // Delega a deleção para o serviço — lança CustomException se o ID não existir
        service.delete(id);

        // noContent().build() monta a resposta HTTP 204 sem corpo
        return ResponseEntity.noContent().build();
    }
}