package com.example.ResTesT.service;

// Importa a entidade MockEndpoint
import com.example.ResTesT.domain.MockEndpoint;

// Importa DTOs utilizados na entrada e saída de dados
import com.example.ResTesT.dto.CreateEndpointRequest;
import com.example.ResTesT.dto.EndpointResponse;

// Importa exceção personalizada
import com.example.ResTesT.exception.CustomException;

// Importa repository responsável pelo acesso ao banco
import com.example.ResTesT.repository.MockEndpointRepository;

// Importa anotação para registrar a classe como service Spring
import org.springframework.stereotype.Service;

// Importa List e UUID
import java.util.List;
import java.util.UUID;

@Service // Define a classe como camada de serviço responsável pela lógica do Mockendpoint
public class MockEndpointService {

    // Repository responsável pelas operações no banco
    private final MockEndpointRepository repo;

    // Service responsável pela geração de hashes únicos
    private final HashGeneratorService hashService;

    // Service responsável pela validação do payload JSON
    private final PayloadValidatorService validator;

    // Injeção de dependências via construtor
    public MockEndpointService(MockEndpointRepository repo,
                               HashGeneratorService hashService,
                               PayloadValidatorService validator) {
        this.repo = repo;
        this.hashService = hashService;
        this.validator = validator;
    }

    // CRIA UM NOVO ENDPOINT MOCKADO
    public EndpointResponse create(CreateEndpointRequest req) {

        // Valida o payload JSON recebido
        validator.validate(req.payload);

        String hash;

        // Contador de tentativas para gerar hash único
        int attempts = 0;

        // Continua gerando hashes enquanto já existirem no banco
        do {
            hash = hashService.generate();
            attempts++;
        } while (repo.existsByHash(hash) && attempts < 5);

        // Caso não consiga gerar hash único após 5 tentativas
        if (attempts == 5) {
            throw new CustomException("Erro ao gerar hash único");
        }

        // Cria nova entidade MockEndpoint
        MockEndpoint e = new MockEndpoint();

        // Define os dados do endpoint
        e.setHash(hash);
        e.setPayload(req.payload);

        // Caso statusCode seja nulo, usa 200 como padrão
        e.setStatusCode(req.statusCode != null ? req.statusCode : 200);

        // Caso delayMs seja nulo, usa 0 como padrão
        e.setDelayMs(req.delayMs != null ? req.delayMs : 0);

        // Adiciona campo textual para identificação do endpoint
        e.setLabel(req.label);

        // Salva no banco de dados
        repo.save(e);

        // Retorna DTO de resposta
        return toResponse(e);
    }

    // ATUALIZA UM ENDPOINT MOCKADO
    public EndpointResponse update(UUID id, CreateEndpointRequest req) {

        // Busca endpoint pelo ID
        MockEndpoint e = repo.findById(id)

                // Lança exceção caso não encontre
                .orElseThrow(() -> new CustomException("Mock não encontrado"));

        // Valida o payload JSON
        validator.validate(req.payload);

        // Atualiza os dados do endpoint
        e.setPayload(req.payload);

        // Mantém valor antigo caso não seja enviado novo valor
        e.setStatusCode(req.statusCode != null ? req.statusCode : e.getStatusCode());
        e.setDelayMs(req.delayMs != null ? req.delayMs : e.getDelayMs());

        // Atualiza o campo textual para identificação do endpoint
        e.setLabel(req.label);


        // Salva alterações no banco
        repo.save(e);

        // Retorna DTO atualizado
        return toResponse(e);
    }

    // LISTA TODOS OS ENDPOINTS
    public List<EndpointResponse> findAll() {

        // Busca todos os registros e converte para DTO
        return repo.findAll().stream().map(this::toResponse).toList();
    }

    // DELETA UM ENDPOINT
    public void delete(UUID id) {

        // Remove endpoint pelo ID
        repo.deleteById(id);
    }

   // ENCONTRA O ENDPOINT PELO HASH
    public MockEndpoint findByHash(String hash) {

        // Busca endpoint pelo hash
        return repo.findByHash(hash)

                // Lança exceção caso não exista
                .orElseThrow(() -> new CustomException("Endpoint não encontrado"));
    }

    // CONVERTER ENTIDADE PARA DTO
    private EndpointResponse toResponse(MockEndpoint e) {


        // Cria objeto de resposta
        EndpointResponse res = new EndpointResponse();

        // Preenche os dados do DTO
        res.id = e.getId();
        res.hash = e.getHash();

        // Monta URL pública do endpoint
        res.url = "/api/" + e.getHash();

        res.payload = e.getPayload(); // Pega conteúdo do playload JSON
        res.statusCode = e.getStatusCode(); // Define o status code
        res.delayMs = e.getDelayMs(); // Delay em milisegundos para rodar
        res.label = e.getLabel(); // Pega campo textual para identificação do endpoint

        // Retorna DTO preenchido
        return res;
    }
}