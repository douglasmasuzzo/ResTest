package com.example.ResTesT.repository;

// Importa a entidade RequestLog
import com.example.ResTesT.domain.RequestLog;

// Importa JpaRepository do Spring Data JPA
// responsável pelas operações automáticas no banco de dados
import org.springframework.data.jpa.repository.JpaRepository;

// Repository responsável pelo acesso aos dados da entidade RequestLog
public interface RequestLogRepository extends JpaRepository<RequestLog, Long> {

    // Atualmente utiliza apenas métodos herdados do JpaRepository
    // Exemplos:
    // save()
    // findAll()
    // findById()
    // deleteById()

}