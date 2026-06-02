package com.example.ResTesT.repository;

// Importa a entidade gerenciada por este repository
import com.example.ResTesT.domain.MockEndpoint;

// Importa o JpaRepository — interface do Spring Data JPA que fornece
// operações de banco de dados prontas sem precisar escrever SQL
import org.springframework.data.jpa.repository.JpaRepository;

// Importa Optional para representar um valor que pode ou não existir
import java.util.Optional;

// Importa UUID para o tipo da chave primária
import java.util.UUID;

/**
 * Repository (repositório) responsável pelo acesso aos dados de MockEndpoint.
 *
 * O que é um Repository?
 *   É uma interface que representa a camada de acesso ao banco de dados.
 *   Seguindo o padrão de arquitetura em camadas:
 *     Controller → Service → Repository → Banco de dados
 *
 * O que é JpaRepository?
 *   É uma interface do Spring Data JPA que já vem com dezenas de operações
 *   prontas para uso, sem precisar escrever nenhuma linha de SQL:
 *
 *   - save(entidade)          → insere ou atualiza um registro
 *   - findById(id)            → busca por ID (retorna Optional)
 *   - findAll()               → retorna todos os registros
 *   - deleteById(id)          → deleta por ID
 *   - existsById(id)          → verifica se um ID existe
 *   - count()                 → conta o total de registros
 *
 *   Parâmetros genéricos: JpaRepository<TipoEntidade, TipoDoId>
 *     MockEndpoint → classe da entidade gerenciada
 *     UUID         → tipo do campo @Id na entidade
 *
 * Por que usar interface em vez de classe?
 *   O Spring cria automaticamente uma implementação concreta desta interface
 *   em tempo de execução — você só precisa declarar os métodos que quer.
 */
public interface MockEndpointRepository extends JpaRepository<MockEndpoint, UUID> {

    /**
     * Busca um endpoint pelo seu hash público.
     *
     * O Spring Data JPA gera automaticamente o SQL a partir do nome do metodo:
     *   findBy + Hash → SELECT * FROM mock_endpoints WHERE hash = ?
     *
     * Retornamos Optional<MockEndpoint> em vez de MockEndpoint diretamente porque
     * o endpoint pode não existir — o Optional força quem chama a tratar esse caso
     * explicitamente, evitando NullPointerException.
     *
     * Uso no serviço:
     *   repo.findByHash("aB3xYz12")
     *       .orElseThrow(() -> new CustomException("Endpoint não encontrado"))
     *
     * @param hash o código hash de 8 caracteres a ser buscado
     * @return Optional contendo o endpoint se encontrado, ou vazio se não existir
     */
    Optional<MockEndpoint> findByHash(String hash);

    /**
     * Verifica se já existe um endpoint com o hash informado.
     *
     * SQL gerado: SELECT COUNT(*) > 0 FROM mock_endpoints WHERE hash = ?
     *
     * Usado pelo HashGeneratorService para garantir que hashes gerados
     * aleatoriamente não colidam com hashes já existentes no banco.
     *
     * @param hash o código hash a ser verificado
     * @return true se já existir um endpoint com este hash, false caso contrário
     */
    boolean existsByHash(String hash);

    /**
     * Busca endpoints cujo label (nome) contenha o texto informado, ignorando maiúsculas/minúsculas.
     *
     * SQL gerado: SELECT * FROM mock_endpoints WHERE LOWER(label) LIKE LOWER('%texto%')
     *
     * Útil para filtrar endpoints por nome na listagem.
     * Exemplo: buscarPorLabel("login") retorna mocks com label "Mock de Login", "ENDPOINT LOGIN", etc.
     *
     * Este metodo segue a convenção de nomes do Spring Data JPA:
     *   findBy + Label + Containing + IgnoreCase
     *
     * @param label texto a ser buscado no campo label
     * @return lista de endpoints cujo label contém o texto informado
     */
    java.util.List<MockEndpoint> findByLabelContainingIgnoreCase(String label);
}