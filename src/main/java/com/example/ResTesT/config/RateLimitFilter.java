package com.example.ResTesT.config;

// Importa as classes da biblioteca Bucket4j para controle de rate limit

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

// indica ao framework que a classe anotada deve ser automaticamente detectada (via classpath scanning)
// instanciada e gerenciada como um bean
@Component
public class RateLimitFilter implements Filter {

    // Armazena um Bucket para cada endereço IP
    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    // Metodo responsável por criar um Bucket
    private Bucket createBucket() {

        // Cria e retorna um Bucket configurado
        return Bucket.builder()
                // Define o limite:
                // 30 requisições disponíveis
                // Recarrega 30 requisições a cada 1 hora
                .addLimit(Bandwidth.classic(100, Refill.greedy(100, Duration.ofHours(1))))
                .build();
    }

    // Metodo executado para cada requisição recebida
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // Converte o ServletRequest para HttpServletRequest
        HttpServletRequest req = (HttpServletRequest) request;

        // Obtém o endereço IP do cliente
        String ip = req.getRemoteAddr();

        // Procura um Bucket já existente para o IP
        // Caso não exista, cria um Bucket
        Bucket bucket = buckets.computeIfAbsent(ip, k -> createBucket());

        // Tenta consumir 1 token do Bucket
        if (bucket.tryConsume(1)) {

            // Se ainda houver tokens disponíveis,
            // permite a continuação da requisição
            chain.doFilter(request, response);
        } else {

            // Caso o limite tenha sido excedido,
            // bloqueia a requisição

            // Converte o response para HttpServletResponse
            HttpServletResponse res = (HttpServletResponse) response;

            // Define o código HTTP 429 (Too Many Requests)
            res.setStatus(429);

            // Retorna mensagem para o cliente
            res.getWriter().write("Too many requests");
        }
    }
}