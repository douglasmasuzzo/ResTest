package com.example.ResTesT.config;

// Importa as classes necessárias para criar um filtro de servlet personalizado
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// Importa anotações do Spring para criar beans de configuração
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Importa as classes do Spring Security para configurar a cadeia de segurança
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Classe de configuração de segurança da aplicação.
 *
 * Estratégia de segurança do ResTest:
 *
 *   - Rotas públicas (/api/**):
 *       Qualquer pessoa pode acessar — são os endpoints mockados consumidos por clientes.
 *
 *   - Rotas de gerenciamento via API Key (/mock/**):
 *       Protegidas pelo apiKeyFilter() — exigem o header X-API-Key.
 *       Usadas por ferramentas externas como o Postman.
 *
 *   - Rotas do painel admin (/admin/**):
 *       Acessíveis APENAS a partir de localhost.
 *       O localhostOnlyFilter() trata todos os formatos de IP local:
 *         "127.0.0.1"          → IPv4 padrão
 *         "::1"                → IPv6 padrão
 *         "0:0:0:0:0:0:0:1"   → IPv6 longo (retornado por alguns JVMs/SOs)
 *
 *   - Requisições OPTIONS (preflight CORS):
 *       Sempre liberadas — o navegador as envia antes de POST/PUT/DELETE.
 */
@Configuration
public class SecurityConfig {

    /**
     * Injeta o valor da propriedade "restest.api-key" do arquivo application.properties.
     */
    @Value("${restest.api-key}")
    private String apiKey;

    /**
     * Define a cadeia de filtros de segurança (SecurityFilterChain).
     *
     * Ordem de execução:
     *   1. localhostOnlyFilter → bloqueia /admin/** de IPs externos (retorna 403)
     *   2. apiKeyFilter        → bloqueia /mock/** sem X-API-Key (retorna 401)
     *
     * @param http objeto que permite configurar a segurança HTTP da aplicação
     * @return a cadeia de segurança construída
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                // Desabilita CSRF — API REST stateless não usa sessões
                .csrf(csrf -> csrf.disable())

                // Habilita suporte ao CORS do Spring Security para respeitar o CorsConfig
                .cors(cors -> {})

                // Filtro 1: bloqueia /admin/** para IPs externos
                .addFilterBefore(localhostOnlyFilter(), UsernamePasswordAuthenticationFilter.class)

                // Filtro 2: valida X-API-Key em /mock/**
                .addFilterBefore(apiKeyFilter(), UsernamePasswordAuthenticationFilter.class)

                // Proteção feita pelos filtros acima — Spring Security não interfere nas rotas
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                );

        return http.build();
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // FILTRO 1 — Restrição de /admin/** a localhost
    // ─────────────────────────────────────────────────────────────────────────────

    /**
     * Restringe /admin/** exclusivamente a localhost.
     *
     * Trata todos os formatos possíveis de IP local que o Java/SO
     * pode retornar via getRemoteAddr():
     *   - "127.0.0.1"        → IPv4 padrão
     *   - "::1"              → IPv6 curto
     *   - "0:0:0:0:0:0:0:1" → IPv6 longo (comum em Windows e alguns JVMs)
     *
     * Preflights OPTIONS são sempre liberados para o CORS funcionar.
     *
     * @return Filter que bloqueia acessos externos a /admin/**
     */
    private Filter localhostOnlyFilter() {

        return (ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) -> {

            HttpServletRequest  request  = (HttpServletRequest)  servletRequest;
            HttpServletResponse response = (HttpServletResponse) servletResponse;

            String path   = request.getRequestURI();
            String method = request.getMethod();

            if (path.startsWith("/admin")) {

                // Preflights OPTIONS sempre passam — necessários para o CORS
                boolean isPreflight = "OPTIONS".equalsIgnoreCase(method);

                if (!isPreflight) {

                    String  ip          = request.getRemoteAddr();

                    // Trata todos os formatos de IP local possíveis:
                    // "127.0.0.1"        → IPv4
                    // "::1"              → IPv6 curto
                    // "0:0:0:0:0:0:0:1" → IPv6 longo (Windows/alguns JVMs)
                    boolean isLocalhost = "127.0.0.1".equals(ip)
                            || "::1".equals(ip)
                            || "0:0:0:0:0:0:0:1".equals(ip);

                    if (!isLocalhost) {

                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.setContentType("application/json");
                        response.getWriter().write(
                                "{\"erro\": \"Acesso ao painel admin permitido apenas a partir de localhost.\"}"
                        );
                        return;
                    }
                }
            }

            chain.doFilter(servletRequest, servletResponse);
        };
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // FILTRO 2 — Validação de X-API-Key em /mock/**
    // ─────────────────────────────────────────────────────────────────────────────

    /**
     * Valida o header X-API-Key nas rotas /mock/**.
     * Preflights OPTIONS são liberados para o CORS funcionar corretamente.
     *
     * @return Filter com a lógica de validação de API Key
     */
    private Filter apiKeyFilter() {

        return (ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) -> {

            HttpServletRequest  request  = (HttpServletRequest)  servletRequest;
            HttpServletResponse response = (HttpServletResponse) servletResponse;

            String path   = request.getRequestURI();
            String method = request.getMethod();

            if (path.startsWith("/mock")) {

                // Preflights OPTIONS sempre passam
                boolean isPreflight = "OPTIONS".equalsIgnoreCase(method);

                if (!isPreflight) {

                    String  chaveEnviada  = request.getHeader("X-API-Key");
                    boolean chaveInvalida = chaveEnviada == null || !chaveEnviada.equals(apiKey);

                    if (chaveInvalida) {

                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.setContentType("application/json");
                        response.getWriter().write(
                                "{\"erro\": \"API Key ausente ou inválida. Envie o header X-API-Key.\"}"
                        );
                        return;
                    }
                }
            }

            chain.doFilter(servletRequest, servletResponse);
        };
    }
}