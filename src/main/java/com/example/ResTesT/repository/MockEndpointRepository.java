package com.example.ResTesT.repository;

// Importa a entidade MockEndpoint
import com.example.ResTesT.domain.MockEndpoint;

// Importa o JpaRepository do Spring Data JPA
// responsável pelas operações automáticas no banco
import org.springframework.data.jpa.repository.JpaRepository;

// Importa Optional e UUID
import java.util.Optional;
import java.util.UUID;

// Repository responsável pelo acesso aos dados
// da entidade MockEndpoint
public interface MockEndpointRepository extends JpaRepository<MockEndpoint, UUID> {

    // Busca endpoint pelo código hash

    // Retorna um Optional contendo o endpoint
    // caso o hash exista no banco
    Optional<MockEndpoint> findByHash(String hash);

    // Verifica se o hash já existe
    // Retorna true caso já exista um endpoint
    // com o hash informado
    boolean existsByHash(String hash);
}