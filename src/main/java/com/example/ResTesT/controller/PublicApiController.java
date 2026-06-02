package com.example.ResTesT.controller;

// Importa a entidade MockEndpoint para representar o endpoint encontrado pelo hash
import com.example.ResTesT.domain.MockEndpoint;

// Importa os serviços necessários para buscar o endpoint e registrar o log
import com.example.ResTesT.service.MockEndpointService;
import com.example.ResTesT.service.RequestLogService;

// Importa para capturar informações da requisição HTTP (como o IP do cliente)
import jakarta.servlet.http.HttpServletRequest;

// Importa classes para construção da resposta HTTP e definição do tipo de conteúdo
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

// Importa anotações REST do Spring para mapear rotas e extrair parâmetros da URL
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Importa para suporte a execução assíncrona (não-bloqueante)
import org.springframework.scheduling.annotation.Async;

// Importa para encapsular o resultado de uma operação assíncrona
import java.util.concurrent.CompletableFuture;

/**
 * Controller público responsável por servir os endpoints mockados aos clientes finais.
 *
 * O que é este Controller?
 *   Enquanto o MockEndpointController permite GERENCIAR os mocks (criar, editar, deletar),
 *   este Controller permite CONSUMIR os mocks — é o que o cliente final usa.
 *
 * Rota pública (sem autenticação — qualquer pessoa pode acessar):
 *   GET /api/{hash} → retorna o payload configurado para o hash informado
 *
 * Exemplo de uso:
 *   O usuário criou um mock com hash "aB3xYz12" e payload {"status": "ok"}.
 *   Qualquer pessoa pode acessar GET /api/aB3xYz12 e receberá {"status": "ok"}.
 *
 * Por que usar @Async (execução assíncrona)?
 *   Endpoints mockados podem ter um delay configurado (ex: 5 segundos).
 *   Sem @Async, a thread que processa a requisição ficaria bloqueada esperando
 *   o delay acabar — impedindo outras requisições de serem atendidas.
 *   Com @Async, o delay acontece em uma thread separada do pool de threads,
 *   liberando a thread principal para atender outras requisições. Isso melhora
 *   significativamente a performance sob carga (múltiplas requisições simultâneas).
 */
@RestController     // Marca como Controller REST — retorna JSON automaticamente
@RequestMapping("/api") // Todas as rotas começam com /api
public class PublicApiController {

    /**
     * Serviço responsável por buscar os endpoints mockados pelo hash.
     */
    private final MockEndpointService endpointService;

    /**
     * Serviço responsável por registrar logs de acesso no banco de dados.
     */
    private final RequestLogService logService;

    /**
     * Construtor com injeção de dependências via construtor.
     * O Spring injeta automaticamente as instâncias corretas dos serviços.
     *
     * @param endpointService serviço dos endpoints mockados
     * @param logService      serviço de registro de logs de acesso
     */
    public PublicApiController(MockEndpointService endpointService, RequestLogService logService) {
        this.endpointService = endpointService;
        this.logService      = logService;
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // GET /api/{hash} — Acesso ao endpoint mockado
    // ─────────────────────────────────────────────────────────────────────────────

    /**
     * Serve o endpoint mockado correspondente ao hash informado na URL.
     *
     * @Async indica que este metodo será executado em uma thread separada do pool
     *   gerenciado pelo Spring (@EnableAsync está ativo na ResTesTApplication).
     *   Isso é importante porque o delay pode pausar a execução por segundos.
     *
     * @GetMapping("/{hash}") mapeia este metodo para GET /api/{hash}
     *   O {hash} é um "path variable" — é extraído dinamicamente da URL.
     *   Exemplo: GET /api/aB3xYz12 → hash = "aB3xYz12"
     *
     * Fluxo de execução:
     *   1. Busca o endpoint pelo hash → retorna 404 se não encontrar
     *   2. Aplica o delay configurado (se houver)
     *   3. Registra o acesso no log (IP, data/hora, endpoint acessado)
     *   4. Retorna o payload com o status code configurado
     *
     * @param hash    o código hash de 8 caracteres extraído da URL
     * @param request objeto da requisição HTTP — usado para capturar o IP do cliente
     * @return CompletableFuture com a resposta HTTP contendo o payload mockado
     */
    @Async
    @GetMapping("/{hash}")
    public CompletableFuture<ResponseEntity<?>> get(@PathVariable String hash,
                                                    HttpServletRequest request) {

        // ── Passo 1: Buscar o endpoint pelo hash ────────────────────────────────
        // Se o hash não existir no banco, o serviço lança CustomException
        // que é capturada pelo GlobalExceptionHandler e retorna HTTP 400
        MockEndpoint endpoint = endpointService.findByHash(hash);

        // ── Passo 2: Aplicar o delay configurado ────────────────────────────────
        if (endpoint.getDelayMs() > 0) {

            try {

                // Thread.sleep() pausa a execução pelo tempo configurado em milissegundos.
                // Como este metodo roda em thread separada (@Async), o servidor
                // continua atendendo outras requisições normalmente durante este sleep.
                Thread.sleep(endpoint.getDelayMs());

            } catch (InterruptedException ex) {

                // InterruptedException ocorre quando outra parte do sistema
                // solicita que esta thread seja interrompida (ex: durante shutdown do servidor).
                // A boa prática obrigatória é chamar interrupt() para restaurar o flag
                // de interrupção na thread atual, permitindo que o sistema se encerre corretamente.
                Thread.currentThread().interrupt();
            }
        }

        // ── Passo 3: Registrar o acesso no log ──────────────────────────────────
        // Salva no banco: qual endpoint foi acessado, quando, e o IP do cliente
        logService.log(endpoint, request.getRemoteAddr());

        // ── Passo 4: Retornar o payload configurado ──────────────────────────────
        // CompletableFuture.completedFuture() encapsula o resultado já disponível
        // em um Future já resolvido — necessário pelo tipo de retorno @Async
        return CompletableFuture.completedFuture(
                ResponseEntity
                        // Usa o status HTTP configurado pelo usuário no endpoint (ex: 200, 404, 201)
                        .status(endpoint.getStatusCode())

                        // Define o tipo do conteúdo da resposta como application/json
                        .contentType(MediaType.APPLICATION_JSON)

                        // Corpo da resposta: o payload JSON configurado no mock
                        .body(endpoint.getPayload())
        );
    }
}