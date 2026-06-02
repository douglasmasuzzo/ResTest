package com.example.ResTesT.config;

// Importa as classes do Bucket4j para implementar o Rate Limiting
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;

// Importa as interfaces de filtro do Jakarta Servlet
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// Importa a anotação que registra esta classe como componente do Spring
import org.springframework.stereotype.Component;

// Importa classes para manipulação de tempo e estrutura de dados thread-safe
import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Filtro de Rate Limiting (limitação de taxa de requisições).
 *
 * O que é Rate Limiting?
 *   É uma técnica que limita o número de requisições que um mesmo cliente
 *   pode fazer em um determinado período de tempo.
 *
 * Por que usar?
 *   - Protege a API contra ataques de força bruta
 *   - Evita que um único usuário sobrecarregue o servidor
 *   - Previne abuso da API (ex: scripts que fazem milhares de chamadas)
 *
 * Como funciona o algoritmo "Token Bucket" (Balde de Tokens)?
 *   Imagine um balde com 100 fichas. Cada requisição consome 1 ficha.
 *   A cada 1 hora, o balde é recarregado com mais 100 fichas.
 *   Se o balde esvaziar antes de 1 hora, as requisições são bloqueadas
 *   até o próximo recarregamento.
 *
 * Cada endereço IP recebe seu próprio balde independente.
 */
@Component // Registra esta classe como um componente gerenciado pelo Spring (bean automático)
public class RateLimitFilter implements Filter {

    /**
     * Mapa que associa cada endereço IP ao seu próprio Bucket (balde de tokens).
     *
     * ConcurrentHashMap é usado em vez de HashMap comum porque a aplicação
     * pode receber várias requisições ao mesmo tempo (múltiplas threads).
     * O ConcurrentHashMap garante que operações simultâneas não causem erros.
     */
    private final ConcurrentHashMap<String, Bucket> bucketsPorIp = new ConcurrentHashMap<>();

    /**
     * Cria um novo Bucket (balde de tokens) com as regras de limite definidas.
     *
     * Configuração atual:
     *   - Capacidade máxima: 100 requisições
     *   - Recarga: 100 tokens a cada 1 hora (greedy = recarrega o mais rápido possível)
     *
     * Por que 100 req/hora? É um valor razoável para uso legítimo da API,
     * mas baixo o suficiente para bloquear scripts abusivos.
     *
     * @return um novo Bucket configurado com as regras de limite
     */
    private Bucket criarBucket() {

        // Define a largura de banda (bandwidth): 100 tokens, recarregando 100 a cada hora
        Bandwidth limite = Bandwidth.classic(100, Refill.greedy(100, Duration.ofHours(1)));

        // Constrói e retorna o bucket com o limite definido
        return Bucket.builder()
                .addLimit(limite)
                .build();
    }

    /**
     * Metodo executado automaticamente para CADA requisição HTTP recebida.
     *
     * O Spring registra este filtro na cadeia de filtros da aplicação.
     * Antes de qualquer requisição chegar ao Controller, passa por aqui.
     *
     * @param servletRequest  a requisição recebida do cliente
     * @param servletResponse a resposta que será enviada ao cliente
     * @param chain           a cadeia de filtros — chamar chain.doFilter() significa "deixar passar"
     */
    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain chain) throws IOException, ServletException {

        // Converte para HTTP para ter acesso ao IP e ao status HTTP
        HttpServletRequest  request  = (HttpServletRequest)  servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // Obtém o endereço IP do cliente que fez a requisição
        // (ex: "192.168.0.10" ou "127.0.0.1" para acesso local)
        String ip = request.getRemoteAddr();

        // Busca o bucket existente para este IP.
        // Se não existir ainda (primeira requisição deste IP), cria um novo bucket.
        // computeIfAbsent é thread-safe — funciona corretamente com múltiplas threads simultâneas.
        Bucket bucket = bucketsPorIp.computeIfAbsent(ip, endereco -> criarBucket());

        // Tenta consumir 1 token do bucket deste IP
        if (bucket.tryConsume(1)) {

            // Ainda há tokens disponíveis — permite que a requisição continue normalmente
            chain.doFilter(servletRequest, servletResponse);

        } else {

            // O balde está vazio — este IP excedeu o limite de requisições
            // Retorna HTTP 429 Too Many Requests (padrão da indústria para rate limiting)
            response.setStatus(429);
            response.setContentType("application/json");
            response.getWriter().write(
                    "{\"erro\": \"Limite de requisições excedido. Tente novamente em 1 hora.\"}"
            );
        }
    }
}