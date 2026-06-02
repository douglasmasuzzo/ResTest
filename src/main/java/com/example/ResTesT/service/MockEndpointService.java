package com.example.ResTesT.service;

// Importa a entidade gerenciada por este serviço
import com.example.ResTesT.domain.MockEndpoint;

// Importa os DTOs de entrada (request) e saída (response)
import com.example.ResTesT.dto.CreateEndpointRequest;
import com.example.ResTesT.dto.EndpointResponse;

// Importa a exceção personalizada para erros de negócio
import com.example.ResTesT.exception.CustomException;

// Importa o repository responsável pelo acesso ao banco de dados
import com.example.ResTesT.repository.MockEndpointRepository;

// Importa a anotação que registra esta classe como serviço do Spring
import org.springframework.stereotype.Service;

// Importa tipos de coleções e identificadores
import java.util.List;
import java.util.UUID;

/**
 * Serviço de regras de negócio para os endpoints mockados.
 *
 * O que é a "camada de serviço" (Service)?
 *   Em uma arquitetura em camadas, a responsabilidade de cada camada é:
 *     Controller → recebe a requisição HTTP e valida o formato dos dados
 *     Service    → aplica as regras de negócio (esta camada)
 *     Repository → acessa e persiste dados no banco de dados
 *
 *   O Service é o "cérebro" da aplicação — toda lógica importante fica aqui.
 *   O Controller apenas delega para o Service e devolve a resposta HTTP.
 *
 * Operações disponíveis (CRUD completo):
 *   C → create()    → cria um novo endpoint mockado
 *   R → findAll()   → lista todos os endpoints
 *   R → findByHash()→ busca um endpoint pelo hash público
 *   U → update()    → atualiza um endpoint existente
 *   D → delete()    → remove um endpoint pelo ID
 */
@Service // Registra esta classe como bean de serviço — injetável em Controllers e outros componentes
public class MockEndpointService {

    /**
     * Repository para persistência dos endpoints no banco de dados.
     * Fornece operações de salvar, buscar, atualizar e deletar.
     */
    private final MockEndpointRepository repository;

    /**
     * Serviço responsável por gerar os hashes únicos dos endpoints.
     */
    private final HashGeneratorService hashGeneratorService;

    /**
     * Serviço responsável por validar se o payload enviado é um JSON válido.
     */
    private final PayloadValidatorService payloadValidator;

    /**
     * Construtor com injeção de dependências via construtor.
     *
     * O Spring injeta automaticamente as implementações corretas de cada interface/classe
     * quando cria uma instância de MockEndpointService.
     *
     * Por que injeção pelo construtor?
     *   - As dependências ficam explícitas e obrigatórias (não podem ser nulas)
     *   - Facilita a criação de testes: basta passar mocks no construtor
     *   - É a abordagem recomendada pelo Spring Framework
     */
    public MockEndpointService(MockEndpointRepository repository,
                               HashGeneratorService hashGeneratorService,
                               PayloadValidatorService payloadValidator) {
        this.repository           = repository;
        this.hashGeneratorService = hashGeneratorService;
        this.payloadValidator     = payloadValidator;
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // CREATE — Criação de novo endpoint mockado
    // ─────────────────────────────────────────────────────────────────────────────

    /**
     * Cria um novo endpoint mockado a partir dos dados fornecidos.
     *
     * Fluxo de execução:
     *   1. Valida se o payload é um JSON válido e está dentro do limite de tamanho
     *   2. Gera um hash único (tentando até 5 vezes para evitar colisão)
     *   3. Cria a entidade MockEndpoint com os dados fornecidos
     *   4. Persiste no banco de dados
     *   5. Retorna um DTO com os dados do endpoint criado (incluindo hash e URL)
     *
     * @param request objeto DTO com os dados enviados pelo cliente
     * @return EndpointResponse com os dados do endpoint criado, incluindo hash e URL pública
     * @throws CustomException se o JSON for inválido ou não for possível gerar um hash único
     */
    public EndpointResponse create(CreateEndpointRequest request) {

        // ── Passo 1: Validar o payload JSON ─────────────────────────────────────
        // O validator lança CustomException automaticamente se o JSON for inválido
        payloadValidator.validate(request.getPayload());

        // ── Passo 2: Gerar hash único ────────────────────────────────────────────
        // Com 62^8 (~218 trilhões) combinações possíveis, colisões são extremamente raras.
        // Ainda assim, verificamos o banco a cada tentativa por segurança.
        // Limitamos a 5 tentativas — se todas colidirem, é provável que haja um bug no gerador.
        String hashGerado = null;
        boolean hashDisponivel = false;

        for (int tentativa = 0; tentativa < 5; tentativa++) {

            // Gera um candidato a hash aleatório
            String candidato = hashGeneratorService.generate();

            // Verifica no banco se este hash já está em uso
            if (!repository.existsByHash(candidato)) {

                // Hash único encontrado — sai do loop
                hashGerado = candidato;
                hashDisponivel = true;
                break;
            }

            // Hash já existe no banco — tenta novamente na próxima iteração
        }

        // Se nenhum dos 5 hashes gerados estava disponível, lança exceção
        if (!hashDisponivel) {
            throw new CustomException(
                    "Não foi possível gerar um hash único após 5 tentativas. Tente novamente."
            );
        }

        // ── Passo 3: Criar a entidade e popular os dados ─────────────────────────
        MockEndpoint novoEndpoint = new MockEndpoint();

        // Hash único que identificará este endpoint na URL pública
        novoEndpoint.setHash(hashGerado);

        // Payload JSON que será retornado ao acessar /api/{hash}
        novoEndpoint.setPayload(request.getPayload());

        // Status code HTTP da resposta — usa 200 como padrão se não informado
        novoEndpoint.setStatusCode(
                request.getStatusCode() != null ? request.getStatusCode() : 200
        );

        // Delay em ms antes de responder — usa 0 (sem delay) se não informado
        novoEndpoint.setDelayMs(
                request.getDelayMs() != null ? request.getDelayMs() : 0
        );

        // Rótulo descritivo definido pelo usuário — pode ser nulo
        novoEndpoint.setLabel(request.getLabel());

        // ── Passo 4: Persistir no banco ──────────────────────────────────────────
        // O @PrePersist da entidade define automaticamente createdAt e updatedAt
        repository.save(novoEndpoint);

        // ── Passo 5: Retornar DTO com os dados do endpoint criado ────────────────
        return converterParaDTO(novoEndpoint);
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // READ — Listagem e busca de endpoints
    // ─────────────────────────────────────────────────────────────────────────────

    /**
     * Retorna todos os endpoints mockados cadastrados no sistema.
     *
     * Cada entidade MockEndpoint é convertida para EndpointResponse (DTO)
     * antes de ser retornada, para não expor dados internos desnecessários.
     *
     * @return lista de DTOs com todos os endpoints cadastrados
     */
    public List<EndpointResponse> findAll() {

        // Busca todos os registros do banco e converte cada um para DTO
        // stream() permite processar a lista de forma funcional
        // map() transforma cada MockEndpoint em EndpointResponse
        // toList() coleta os resultados em uma lista imutável
        return repository.findAll()
                .stream()
                .map(this::converterParaDTO)
                .toList();
    }

    /**
     * Busca um endpoint pelo seu hash público.
     *
     * Usado pelo PublicApiController para encontrar o endpoint
     * quando alguém acessa /api/{hash}.
     *
     * @param hash o código hash de 8 caracteres da URL
     * @return a entidade MockEndpoint correspondente ao hash
     * @throws CustomException se não existir endpoint com o hash informado
     */
    public MockEndpoint findByHash(String hash) {

        // orElseThrow lança a exceção automaticamente se o Optional estiver vazio
        // (ou seja, se nenhum endpoint com esse hash existir no banco)
        return repository.findByHash(hash)
                .orElseThrow(() ->
                        new CustomException("Endpoint não encontrado para o hash: " + hash)
                );
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // UPDATE — Atualização de endpoint existente
    // ─────────────────────────────────────────────────────────────────────────────

    /**
     * Atualiza os dados de um endpoint mockado existente.
     *
     * Fluxo de execução:
     *   1. Busca o endpoint pelo ID — lança exceção se não existir
     *   2. Valida o novo payload JSON
     *   3. Atualiza apenas os campos fornecidos (mantém os valores anteriores se nulo)
     *   4. Salva as alterações no banco
     *   5. Retorna o DTO atualizado
     *
     * @param id      UUID do endpoint a ser atualizado
     * @param request DTO com os novos dados a serem aplicados
     * @return EndpointResponse com os dados atualizados do endpoint
     * @throws CustomException se o endpoint não for encontrado ou o JSON for inválido
     */
    public EndpointResponse update(UUID id, CreateEndpointRequest request) {

        // ── Passo 1: Localizar o endpoint no banco ───────────────────────────────
        // Se não existir, orElseThrow lança CustomException automaticamente
        MockEndpoint endpointExistente = repository.findById(id)
                .orElseThrow(() ->
                        new CustomException(
                                "Mock não encontrado para o ID: " + id
                        )
                );

        // ── Passo 2: Validar o novo payload ─────────────────────────────────────
        payloadValidator.validate(request.getPayload());

        // ── Passo 3: Atualizar os campos com os novos valores ────────────────────

        // Sempre atualiza o payload (campo obrigatório na requisição)
        endpointExistente.setPayload(request.getPayload());

        // Atualiza o statusCode apenas se um novo valor foi informado.
        // Caso contrário, mantém o valor que já estava salvo.
        if (request.getStatusCode() != null) {
            endpointExistente.setStatusCode(request.getStatusCode());
        }

        // Mesma lógica para o delay
        if (request.getDelayMs() != null) {
            endpointExistente.setDelayMs(request.getDelayMs());
        }

        // Atualiza o rótulo (pode ser nulo — o usuário pode limpar o label)
        endpointExistente.setLabel(request.getLabel());

        // ── Passo 4: Persistir as alterações ────────────────────────────────────
        // O @PreUpdate da entidade atualiza automaticamente o campo updatedAt
        repository.save(endpointExistente);

        // ── Passo 5: Retornar DTO com os dados atualizados ──────────────────────
        return converterParaDTO(endpointExistente);
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // DELETE — Remoção de endpoint
    // ─────────────────────────────────────────────────────────────────────────────

    /**
     * Remove permanentemente um endpoint mockado do sistema.
     *
     * Por causa do cascade = CascadeType.ALL na entidade MockEndpoint,
     * todos os logs de acesso relacionados a este endpoint
     * também são deletados automaticamente pelo JPA.
     *
     * @param id UUID do endpoint a ser removido
     * @throws CustomException se não existir endpoint com o ID informado
     */
    public void delete(UUID id) {

        // Verifica se o endpoint existe antes de tentar deletar.
        // Sem esta verificação, deleteById() em um ID inexistente
        // não lança erro — o que seria um comportamento silencioso e confuso.
        if (!repository.existsById(id)) {
            throw new CustomException(
                    "Não foi possível deletar: mock não encontrado para o ID: " + id
            );
        }

        // Remove o endpoint e todos os seus logs relacionados (via CascadeType.ALL)
        repository.deleteById(id);
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // MÉTODO AUXILIAR PRIVADO
    // ─────────────────────────────────────────────────────────────────────────────

    /**
     * Converte uma entidade MockEndpoint para o DTO EndpointResponse.
     *
     * Por que ter este método separado?
     *   - Evita duplicação: create(), update() e findAll() usam a mesma conversão
     *   - Se precisar mudar o formato da resposta, muda em um único lugar
     *   - private garante que este método auxiliar não seja chamado de fora da classe
     *
     * @param endpoint a entidade a ser convertida
     * @return EndpointResponse com os dados públicos do endpoint
     */
    private EndpointResponse converterParaDTO(MockEndpoint endpoint) {

        EndpointResponse response = new EndpointResponse();

        // Identificador único interno do banco de dados
        response.setId(endpoint.getId());

        // Hash de 8 caracteres usado na URL pública
        response.setHash(endpoint.getHash());

        // Monta a URL completa para acesso público ao endpoint
        // Exemplo: hash "aB3xYz12" → URL "/api/aB3xYz12"
        response.setUrl("/api/" + endpoint.getHash());

        // Payload JSON que será retornado ao acessar a URL
        response.setPayload(endpoint.getPayload());

        // Código de status HTTP configurado
        response.setStatusCode(endpoint.getStatusCode());

        // Delay em milissegundos antes de responder
        response.setDelayMs(endpoint.getDelayMs());

        // Rótulo descritivo definido pelo usuário
        response.setLabel(endpoint.getLabel());

        return response;
    }
}