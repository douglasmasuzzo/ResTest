package com.example.ResTesT.repository;

// Importa a entidade gerenciada por este repository
import com.example.ResTesT.domain.RequestLog;

// Importa o JpaRepository — fornece operações de banco prontas automaticamente
import org.springframework.data.jpa.repository.JpaRepository;

// Importa a entidade MockEndpoint para uso no metodo de busca por endpoint
import com.example.ResTesT.domain.MockEndpoint;

// Importa List para o retorno de coleções
import java.util.List;

/**
 * Repository responsável pelo acesso aos dados de RequestLog.
 *
 * O que este repository gerencia?
 *   Registros de acesso (logs) gerados automaticamente cada vez que
 *   alguém consulta um endpoint mockado via /api/{hash}.
 *
 * Herança de JpaRepository<RequestLog, Long>:
 *   RequestLog → entidade gerenciada
 *   Long       → tipo do @Id (identificador auto-incrementado)
 *
 * Métodos herdados prontos para uso:
 *   - save(log)         → salva um novo log no banco
 *   - findAll()         → retorna todos os logs
 *   - findById(id)      → busca log por ID
 *   - count()           → conta o total de logs registrados
 *   - deleteById(id)    → remove um log específico
 */
public interface RequestLogRepository extends JpaRepository<RequestLog, Long> {

    /**
     * Busca todos os logs de acesso de um determinado endpoint.
     *
     * SQL gerado pelo Spring Data JPA a partir do nome do metodo:
     *   findBy + Endpoint → SELECT * FROM request_logs WHERE endpoint_id = ?
     *
     * Útil para exibir o histórico de quem acessou um endpoint específico.
     *
     * @param endpoint o endpoint cujos logs serão buscados
     * @return lista de todos os logs de acesso deste endpoint
     */
    List<RequestLog> findByEndpoint(MockEndpoint endpoint);
}