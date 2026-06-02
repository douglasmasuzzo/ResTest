# Documento de Especificação Funcional — ResTest

**Disciplina:** Programação Orientada a Objetos  
**Docente:** Ricardo Pupo   
**Time:** Douglas Masuzzo, Tiago Felipe e Otávio Augusto   
**Início:** 28 de abril de 2026  
**Entrega:** 02 de junho de 2026

---

## 1. Descrição do Produto

O **ResTest** é uma aplicação web que permite a desenvolvedores front-end criar endpoints REST falsos para testar suas interfaces. O usuário define um payload JSON e o sistema gera automaticamente uma URL pública que retorna aquele JSON via HTTP, com autenticação própria para que cada usuário gerencie seus próprios endpoints.

---

## 2. Stack Tecnológica

| Camada | Tecnologia | Justificativa |
|---|---|---|
| Backend | Java 21 + Spring Boot 3.x | Suporte a Virtual Threads (Loom), ecossistema maduro |
| Frontend | HTML5, CSS3 e JS Vanilla | Simplicidade; servido via recursos estáticos do Spring |
| Banco de Dados | PostgreSQL (Supabase) | Suporte a JSONB nativo, escalabilidade |
| Segurança | Spring Security + Rate Limit | Proteção de endpoints e controle de abuso por IP |
| **Utilitários** | Lombok + Bucket4j | Redução de boilerplate e rate limiting robusto |
| **Build/Deps** | Maven | Gerenciamento de dependências e automação de build |

---

## 3. Requisitos Funcionais — Escopo da Entrega

Os requisitos estão classificados em **obrigatórios** (entram na entrega) e **desejáveis** (implementar somente se sobrar tempo). Essa divisão foi calibrada para o orçamento de ~35 horas com time iniciante.

---

### RF01 — Cadastro e login de usuário ✗ Fora do escopo

**Justificativa de remoção:** A proposta central do ResTest é a simplicidade
de uso — colar um JSON e obter uma URL em segundos, sem atrito de cadastro.
O sistema opera de forma aberta e sem autenticação por sessão.
A infraestrutura de segurança (BCrypt, Spring Security, RateLimitFilter)
permanece ativa para proteção contra abuso via rate limiting.

---

### RF02 — Criação de endpoint com payload JSON ★ Obrigatório

**Descrição:** O usuário deve ser capaz de criar um endpoint falso inserindo um payload JSON customizado.

**Critérios de aceitação:**
- Área de texto para inserir o JSON.
- Validação de sintaxe JSON antes de salvar (feedback de erro inline).
- Ao salvar, o sistema gera e exibe a URL pública do endpoint.
- URL copiável com um clique.

---

### RF04 — Listagem de endpoints ★ Obrigatório

**Descrição:** O sistema deve exibir todos os endpoints criados para gerenciamento.

---

### RF06 — Configuração de status code ★ Obrigatório

**Descrição:** O usuário pode definir o status HTTP que o endpoint irá retornar (200, 201, 400, 404, 500).

**Status:** Implementado (Backend & Model)

---

### RF07 — Edição de endpoint existente ★ Obrigatório

**Descrição:** O usuário pode editar o JSON, delay ou status de um endpoint já criado sem alterar sua URL.

**Status:** Implementado (MockEndpointService)

---

### RF09 — Histórico de Logs recebidos ★ Obrigatório

**Descrição:** O sistema exibe quantas vezes cada endpoint foi acessado
e de qual origem (IP e Data).

**Status:** Implementado (Backend)

---

## 4. Requisitos Não Funcionais

| ID | Requisito | Prioridade | Solução planejada |
|---|---|---|---|
| RNF01 | URLs públicas disponíveis 24/7 | Alta | Supabase gerenciado + deploy em Railway ou Fly.io |
| RNF02 | Resposta em menos de 500ms | Alta | Índice em `hash` no PostgreSQL, sem cold-start |
| RNF03 | Suporte a CORS | Alta | `CorsConfig` global no Spring com `Access-Control-Allow-Origin: *` |
| RNF04 | Usabilidade — criar endpoint em até 2 minutos | Média | Fluxo de criação em tela única, feedback inline |
| RNF05 | Segurança básica — prevenção de abuso | Média | Tamanho máximo de JSON: 100 KB; rate limiting via Bucket4j por IP |
| RNF06 | Escalabilidade | Baixa | Design stateless no controller público; índices no banco |
| RNF07 | Manutenibilidade — boas práticas de OO | Média | Separação em camadas (Controller → Service → Repository), SRP por classe |

---

## 5. Modelo de Dados Simplificado

```
mock_endpoints
  id          UUID PK
  hash        VARCHAR(8) UNIQUE
  payload     TEXT
  status_code INTEGER DEFAULT 200
  delay_ms    INTEGER DEFAULT 0
  criado_em   TIMESTAMP
  atualizado_em TIMESTAMP

request_logs
  id          BIGSERIAL PK
  endpoint_id UUID FK → mock_endpoints.id
  method      VARCHAR(10)
  chamado_em  TIMESTAMP
  caller_ip   VARCHAR(45)
```

---

## 6. Fluxo Principal de Uso

```
1. Usuário acessa ResTest → tela inicial de criação/listagem
2. Cola o payload JSON → sistema valida a sintaxe em tempo real
3. Define Status Code e Delay (opcional)
4. Clica em "Criar" → sistema gera URL pública instantaneamente
5. Copia a URL com 1 clique → usa no front-end que está testando
6. Gerencia seus endpoints na lista (Editar, Excluir, Ver Logs)
```

---

## 7. Distribuição de Responsabilidades Sugerida

| Área | Responsável sugerido |
|---|---|
| Backend (Spring Boot, endpoints, banco) | Douglas + Tiago |
| Frontend (HTML, CSS, JS, telas, UX) | Otávio + Douglas |
| Configuração de infraestrutura (Supabase, deploy) | Tiago |

> A distribuição pode ser ajustada conforme afinidade do time. O importante é que cada integrante tenha pelo menos uma entrega de backend e uma de frontend, atendendo ao critério pedagógico da disciplina de POO.

---

## 8. Riscos e Mitigações

| Risco | Probabilidade | Mitigação |
|---|---|---|
| Integração entre Frontend e Backend via Fetch API | Média | Utilizar a Fetch API nativa do JS para consumir os endpoints REST do Spring Boot |
| Integração Spring Security mais complexa que o esperado | Média | Usar autenticação básica com formulário padrão do Spring Security antes de customizar |
| Falta de tempo para RF06 e RF07 | Média | São marcados como desejáveis; a entrega é válida sem eles |
| Supabase com configuração de SSL/conexão | Baixa | Seguir documentação oficial do Supabase para Spring Boot com `spring.datasource` |

---

## 9. Legenda de Prioridade

| Símbolo | Significado |
|---|---|
| ★ Obrigatório | Deve estar funcionando na entrega final |
| ◎ Desejável | Implementar se sobrar tempo na semana 6 |
| ✗ Fora do escopo | Não entra nesta entrega |
