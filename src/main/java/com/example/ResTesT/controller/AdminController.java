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
 * Controller intermediário para o painel de administração do front-end.
 *
 * Por que este controller existe?
 *   O front-end roda no navegador, e qualquer código JS é visível ao usuário.
 *   Se o front-end chamasse /mock/** diretamente, precisaria enviar a X-API-Key
 *   no JS — expondo a chave a qualquer pessoa que abrisse o DevTools.
 *
 *   A solução (Opção 3) é este controller intermediário:
 *     - O front-end chama /admin/** SEM nenhuma chave
 *     - Este controller recebe a requisição e delega ao MockEndpointService
 *     - A X-API-Key nunca trafega entre navegador e servidor — fica só no back-end
 *     - A proteção real de /admin/** é feita por localhost-only no SecurityConfig
 *
 * Rotas disponíveis (todas acessíveis apenas a partir de localhost):
 *   GET    /admin/mock         → lista todos os endpoints cadastrados
 *   POST   /admin/mock         → cria um novo endpoint mockado
 *   PUT    /admin/mock/{id}    → atualiza um endpoint existente pelo ID
 *   DELETE /admin/mock/{id}    → remove um endpoint pelo ID
 */
@RestController    // Marca esta classe como Controller REST — retorna JSON automaticamente
@RequestMapping("/admin/mock") // Todas as rotas deste controller começam com /admin/mock
public class AdminController {

    /**
     * Serviço com toda a lógica de negócio dos endpoints mockados.
     * Compartilhado com o MockEndpointController — a lógica não é duplicada.
     */
    private final MockEndpointService service;

    /**
     * Construtor com injeção de dependência do serviço.
     *
     * O Spring injeta automaticamente a instância de MockEndpointService.
     * Usar o construtor (em vez de @Autowired no campo) é a prática
     * recomendada — facilita testes e torna a dependência explícita.
     *
     * @param service o serviço de endpoints mockados injetado pelo Spring
     */
    public AdminController(MockEndpointService service) {
        this.service = service;
    }


    // ─────────────────────────────────────────────────────────────────────────────
    // GET /admin/mock — Listagem de todos os endpoints (para o painel admin)
    // ─────────────────────────────────────────────────────────────────────────────

    /**
     * Retorna a lista de todos os endpoints mockados cadastrados.
     *
     * Chamado pelo front-end ao carregar o painel de administração.
     * Não exige X-API-Key — a segurança é garantida pelo bloqueio de localhost.
     *
     * @return HTTP 200 OK com a lista de endpoints no corpo
     */
    @GetMapping
    public ResponseEntity<?> list() {

        // Delega a listagem ao mesmo serviço usado pelo MockEndpointController
        return ResponseEntity.ok(service.findAll());
    }


    // ─────────────────────────────────────────────────────────────────────────────
    // POST /admin/mock — Criação de novo endpoint (via painel admin)
    // ─────────────────────────────────────────────────────────────────────────────

    /**
     * Cria um novo endpoint mockado com os dados enviados pelo painel admin.
     *
     * O front-end envia o JSON sem X-API-Key; este controller repassa ao service
     * que aplica todas as validações e regras de negócio normalmente.
     *
     * @param request os dados do novo endpoint enviados pelo front-end
     * @return HTTP 201 Created com o DTO do endpoint recém-criado no corpo
     */
    @PostMapping
    public ResponseEntity<EndpointResponse> create(@Valid @RequestBody CreateEndpointRequest request) {

        // Delega ao service — mesma lógica de criação do MockEndpointController
        EndpointResponse response = service.create(request);

        // HTTP 201 Created é o status correto para recursos recém-criados
        return ResponseEntity.status(201).body(response);
    }


    // ─────────────────────────────────────────────────────────────────────────────
    // PUT /admin/mock/{id} — Atualização de endpoint (via painel admin)
    // ─────────────────────────────────────────────────────────────────────────────

    /**
     * Atualiza os dados de um endpoint mockado existente.
     *
     * @param id      o UUID do endpoint a ser atualizado, extraído da URL
     * @param request os novos dados a serem aplicados, recebidos no corpo JSON
     * @return HTTP 200 OK com o DTO do endpoint atualizado no corpo
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id,
                                    @Valid @RequestBody CreateEndpointRequest request) {

        // Delega ao service — lança CustomException se o ID não existir
        return ResponseEntity.ok(service.update(id, request));
    }


    // ─────────────────────────────────────────────────────────────────────────────
    // DELETE /admin/mock/{id} — Remoção de endpoint (via painel admin)
    // ─────────────────────────────────────────────────────────────────────────────

    /**
     * Remove permanentemente um endpoint mockado do sistema.
     *
     * @param id o UUID do endpoint a ser removido, extraído da URL
     * @return HTTP 204 No Content (sem corpo na resposta)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {

        // Delega ao service — lança CustomException se o ID não existir
        service.delete(id);

        // noContent().build() monta a resposta HTTP 204 sem corpo
        return ResponseEntity.noContent().build();
    }
}