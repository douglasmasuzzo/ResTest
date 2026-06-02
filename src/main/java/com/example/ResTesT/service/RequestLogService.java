package com.example.ResTesT.service;

// Importa a entidade MockEndpoint para referenciar qual endpoint foi acessado
import com.example.ResTesT.domain.MockEndpoint;

// Importa a entidade RequestLog que será salva no banco
import com.example.ResTesT.domain.RequestLog;

// Importa o repository responsável por salvar logs no banco de dados
import com.example.ResTesT.repository.RequestLogRepository;

// Importa a anotação que registra esta classe como serviço do Spring
import org.springframework.stereotype.Service;

/**
 * Serviço responsável por registrar logs de acesso aos endpoints mockados.
 *
 * O que este serviço faz?
 *   Toda vez que alguém acessa um endpoint mockado (/api/{hash}),
 *   este serviço cria e salva automaticamente um registro no banco de dados
 *   com informações sobre o acesso.
 *
 * Para que serve o histórico de logs?
 *   - Permite ao criador do mock saber quantas vezes e quando seu endpoint foi acessado
 *   - Registra os IPs que acessaram o endpoint (útil para auditoria)
 *   - Pode ser usado futuramente para gerar relatórios de uso e analytics
 *
 * Arquitetura:
 *   PublicApiController → RequestLogService → RequestLogRepository → banco de dados
 */
@Service // Registra esta classe como bean de serviço — injetável em qualquer outro componente
public class RequestLogService {

    /**
     * Repository responsável por salvar os logs no banco de dados.
     * Injetado via construtor para facilitar os testes unitários.
     */
    private final RequestLogRepository repository;

    /**
     * Construtor com injeção de dependência.
     *
     * Por que usar injeção pelo construtor em vez de @Autowired no campo?
     *   - Torna as dependências explícitas e obrigatórias
     *   - Facilita a criação de testes unitários: basta passar um mock no construtor
     *   - É a prática recomendada pelo Spring e pela comunidade Java
     *
     * @param repository o repository de logs injetado automaticamente pelo Spring
     */
    public RequestLogService(RequestLogRepository repository) {
        this.repository = repository;
    }

    /**
     * Registra um novo log de acesso para o endpoint informado.
     *
     * Este metodo é chamado pelo PublicApiController toda vez que
     * alguém faz uma requisição GET para /api/{hash}.
     *
     * O que é salvo no log?
     *   - Referência ao endpoint que foi acessado
     *   - Metodo HTTP utilizado (sempre "GET" para o endpoint público)
     *   - Endereço IP do cliente que fez a requisição
     *   - Data e hora do acesso (preenchida automaticamente pelo @PrePersist da entidade)
     *
     * @param endpoint o endpoint mockado que foi acessado
     * @param ip       o endereço IP do cliente que fez a requisição
     */
    public void log(MockEndpoint endpoint, String ip) {

        // Cria um novo objeto de log usando o padrão Builder do Lombok.
        // O Builder permite definir apenas os campos necessários de forma legível.
        RequestLog novoLog = new RequestLog();

        // Define qual endpoint foi acessado (chave estrangeira no banco)
        novoLog.setEndpoint(endpoint);

        // Define o metodo HTTP da requisição.
        // Neste caso sempre "GET" pois o endpoint público só aceita GET.
        novoLog.setMethod("GET");

        // Define o endereço IP de quem fez a requisição.
        // Pode ser IPv4 (ex: "192.168.1.1") ou IPv6 (ex: "::1" para loopback local)
        novoLog.setCallerIp(ip);

        // A data/hora do acesso (calledAt) é definida automaticamente pelo @PrePersist
        // na entidade RequestLog — não precisamos setar manualmente aqui.

        // Persiste o log no banco de dados
        repository.save(novoLog);
    }
}