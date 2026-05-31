# Especificação Técnica — ResTest

> Ferramenta para criação de endpoints REST falsos voltada a desenvolvedores front-end.

---

## 1. Visão Geral do Sistema

O **ResTest** é uma aplicação fullstack que permite ao usuário definir um payload JSON e obter instantaneamente uma URL pública que serve esse JSON via HTTP. O sistema é projetado para ser simples, rápido e completamente serverless do ponto de vista do usuário final.

---

## 2. Stack Tecnológica

| Camada | Tecnologia | Justificativa |
|---|---|---|
| Backend | Java 21 + Spring Boot 3.x | Ecossistema maduro, suporte a Virtual Threads (Loom), fácil exposição de REST APIs |
| Frontend | HTML5, CSS3 e JavaScript Vanilla | Simplicidade e rapidez no desenvolvimento; comunicação via Fetch API com o backend |
| Banco de Dados | PostgreSQL (Supabase) | Banco gerenciado, suporte a JSONB nativo, escalabilidade horizontal |
| ORM | Spring Data JPA + Hibernate | Mapeamento objeto-relacional padrão do ecossistema Spring |
| Validação | Jackson + Bean Validation | Validação de JSON e campos de formulário |
| Build | Maven | Padrão de mercado para projetos Spring Boot |

---

## 3. Arquitetura Geral

```
┌─────────────────────────────────────────────────────┐
│                  Cliente (Browser)                  │
│                Vanilla JS (Fetch API)               │
└────────────────────┬────────────────────────────────┘
                     │ REST API
┌────────────────────▼────────────────────────────────┐
│              Spring Boot Application                │
│  ┌─────────────────┐   ┌──────────────────────────┐ │
│  │  Internal REST  │   │  Public REST Controller  │ │
│  │  (CRUD interno) │   │  GET /api/{hash}         │ │
│  └────────┬────────┘   └────────────┬─────────────┘ │
│           │                         │               │
│  ┌────────▼─────────────────────────▼─────────────┐ │
│  │              Service Layer                      │ │
│  │  MockEndpointService  |  RequestLogService      │ │
│  └────────────────────────────┬────────────────────┘ │
│                               │                     │
│  ┌────────────────────────────▼────────────────────┐ │
│  │              Repository Layer (JPA)             │ │
│  └────────────────────────────┬────────────────────┘ │
└───────────────────────────────┼─────────────────────┘
                                │ JDBC / SSL
┌───────────────────────────────▼─────────────────────┐
│             Supabase PostgreSQL                     │
│   mock_endpoints     |  request_logs                │
└─────────────────────────────────────────────────────┘
```

O sistema possui **duas fronteiras de entrada distintas**:

1. **Interface REST Interna** — comunicação entre o frontend e o backend Spring Boot, usada para operações de CRUD dos endpoints (criar, listar, editar, excluir).
2. **REST Controller Público** — rota aberta `/api/{hash}` que serve o JSON configurado pelo usuário, acessível por qualquer cliente HTTP.

---

## 4. Modelo de Dados

### 4.1 Tabela `mock_endpoints`

```sql
CREATE TABLE mock_endpoints (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    hash        VARCHAR(8) UNIQUE NOT NULL,
    label       TEXT,
    payload     TEXT NOT NULL,
    status_code INTEGER NOT NULL DEFAULT 200,
    delay_ms    INTEGER NOT NULL DEFAULT 0,
    created_at  TIMESTAMP NOT NULL DEFAULT now(),
    updated_at  TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_mock_endpoints_hash ON mock_endpoints (hash);
```

### 4.2 Tabela `request_logs`

```sql
CREATE TABLE request_logs (
    id          BIGSERIAL PRIMARY KEY,
    endpoint_id UUID NOT NULL REFERENCES mock_endpoints(id) ON DELETE CASCADE,
    method      VARCHAR(10) NOT NULL,
    called_at   TIMESTAMP NOT NULL DEFAULT now(),
    caller_ip   VARCHAR(45)
);

CREATE INDEX idx_request_logs_endpoint_id ON request_logs (endpoint_id);
```

**Observações de design:**
- O campo `payload` é armazenado como `TEXT` para suportar JSON serializado de até 100KB.
- O campo `hash` é gerado no backend (alfanumérico de 8 caracteres) para controle da lógica de unicidade.
- Logs são deletados em cascata ao remover o endpoint pai.

---

## 5. Estrutura de Pacotes (Backend)

```
com.example.ResTesT
├── config/
│   ├── CorsConfig.java             # Configuração global de CORS
│   └── RateLimitFilter.java        # Filtro de rate limiting por IP
├── domain/
│   ├── MockEndpoint.java           # Entidade JPA
│   └── RequestLog.java             # Entidade JPA
├── repository/
│   ├── MockEndpointRepository.java # Spring Data JPA
│   └── RequestLogRepository.java
├── service/
│   ├── MockEndpointService.java    # Regras de negócio de endpoints
│   ├── HashGeneratorService.java   # Geração de hash único
│   ├── PayloadValidatorService.java # Validação de JSON
│   └── RequestLogService.java      # Registro de chamadas recebidas
├── controller/                     # Controladores REST
│   ├── MockEndpointController.java # CRUD interno (/mock)
│   └── PublicApiController.java    # GET /api/{hash}
└── dto/
    ├── CreateEndpointRequest.java
    └── EndpointResponse.java
```

---

## 6. Endpoints da API

### 6.1 API Pública (servida ao mundo)

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/api/{hash}` | Retorna o JSON configurado com o status code e delay definidos |

**Resposta de exemplo:**
```http
HTTP/1.1 200 OK
Content-Type: application/json
Access-Control-Allow-Origin: *
Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS

{ "id": 1, "name": "ResTest User" }
```

### 6.2 Interface REST Interna (CRUD)

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/mock` | Cria novo endpoint |
| `GET` | `/mock` | Lista todos os endpoints |
| `PUT` | `/mock/{id}` | Atualiza payload ou configurações |
| `DELETE` | `/mock/{id}` | Remove endpoint |

---

## 7. Regras de Negócio

### 7.1 Geração de Hash
- O `HashGeneratorService` gera strings alfanuméricas de 8 caracteres (Base62).
- Colisões são tratadas com retry automático (máximo 5 tentativas).
- O hash é imutável após a criação — a URL não muda ao editar o endpoint.

### 7.2 Validação de Payload JSON
- O JSON submetido é validado com `ObjectMapper` do Jackson antes de persistir.
- Tamanho máximo: **100 KB**.
- Tipos aceitos na raiz: objeto `{}`, array `[]` e valores primitivos.
- Erros de sintaxe retornam `400 Bad Request` com mensagem descritiva.

### 7.3 Rate Limiting
- Limite por IP para criação: **30 requisições / hora**.
- Implementado via `Bucket4j` integrado ao `OncePerRequestFilter` do Spring.
- Exceder o limite retorna `429 Too Many Requests`.

### 7.4 Delay de Resposta
- Aplicado via `Thread.sleep()` na Virtual Thread do endpoint público.
- Mínimo: 0 ms. Máximo: **10.000 ms**.
- Não afeta as operações de CRUD interno.

### 7.5 Logs de Requisição
- Cada chamada à rota `/api/{hash}` grava um registro em `request_logs`.
- Apenas os **últimos 50 logs** por endpoint são exibidos na interface.
- O histórico é zerado automaticamente ao editar o payload ou as configurações do endpoint.

---

## 8. Configuração de CORS

Todos os endpoints públicos respondem com os seguintes headers:

```
Access-Control-Allow-Origin: *
Access-Control-Allow-Methods: GET, OPTIONS
Access-Control-Allow-Headers: Content-Type, Authorization
Access-Control-Max-Age: 86400
```

Configuração centralizada em `CorsConfig.java` via `WebMvcConfigurer`.

---

## 9. Configuração do Ambiente

### 9.1 Variáveis de Ambiente

```env
# Banco de dados (Supabase)
SPRING_DATASOURCE_URL=jdbc:postgresql://<host>:5432/<db>
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=<senha>

# Aplicação
APP_BASE_URL=https://restestapi.example.com
APP_MAX_JSON_SIZE_KB=100
APP_RATE_LIMIT_PER_HOUR=30
```

### 9.2 Perfis Spring

| Perfil | Finalidade |
|---|---|
| `dev` | Banco local, logs verbosos, rate limit desativado |
| `prod` | Supabase, rate limit ativo, logs estruturados (JSON) |

---

## 10. Requisitos Não Funcionais Atendidos

| RNF | Solução Técnica |
|---|---|
| Disponibilidade 24/7 (RNF01) | Supabase gerenciado + deploy em cloud (Railway / Fly.io) |
| Latência < 500ms p95 (RNF02) | Virtual Threads (Project Loom), índice em `hash`, sem cold-start |
| CORS permissivo (RNF03) | `CorsConfig.java` with `Access-Control-Allow-Origin: *` |
| Usabilidade (RNF04) | Fluxo de criação em 1 tela, feedback inline de validação JSON |
| Segurança básica (RNF05) | Rate limiting via Bucket4j, limite de tamanho de payload, sanitização via Jackson |
| Escalabilidade (RNF06) | Design stateless no controller público, JSONB indexado, cache via Spring Cache |
| Manutenibilidade (RNF07) | SRP por camada, cobertura de testes unitários nos Services e HashGenerator |

---

## 11. Testes

| Tipo | Escopo | Ferramenta |
|---|---|---|
| Unitário | `MockEndpointService`, `HashGeneratorService`, validação de JSON | JUnit 5 + Mockito |
| Integração | Repositórios JPA, controller público | Spring Boot Test + Testcontainers (PostgreSQL) |
| Contrato | Endpoints REST | Postman / RestAssured |
| E2E | Fluxo de criação e consumo de endpoint | Cypress / Playwright (opcional) |

Cobertura mínima esperada para camadas de serviço: **80%**.
