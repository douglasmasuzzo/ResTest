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

| Camada | Tecnologia |
|---|---|
| Backend | Java 21 + Spring Boot 3.x |
| Frontend | HTML5, CSS3 e JavaScript Vanilla |
| Banco de Dados | PostgreSQL via Supabase |
| Autenticação | Spring Security + Rate Limiting via Bucket4j|
| Build/Deps | Maven | 

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

**Descrição:** O usuário autenticado deve ser capaz de criar um endpoint falso inserindo um payload JSON customizado.

**Critérios de aceitação:**
- Área de texto para inserir o JSON.
- Validação de sintaxe JSON antes de salvar (feedback de erro inline).
- Ao salvar, o sistema gera e exibe a URL pública do endpoint.
- URL copiável com um clique.

**Estimativa:** 6h

---

### RF03 — Retorno do JSON via HTTP ★ Obrigatório

**Descrição:** A URL gerada deve responder a requisições HTTP GET retornando o JSON configurado.

**Critérios de aceitação:**
- Resposta com `Content-Type: application/json`.
- Status code 200 por padrão.
- Headers CORS habilitados (`Access-Control-Allow-Origin: *`).
- Suporte a preflight OPTIONS.

**Estimativa:** 4h

---

### RF04 — Listagem de endpoints do usuário ★ Obrigatório

**Descrição:** O usuário deve visualizar todos os endpoints que criou.

**Critérios de aceitação:**
- Listagem dos endpoints do usuário logado.
- Exibição da URL pública e de um trecho do JSON para identificação.
- Botão de copiar URL em cada item da lista.

**Estimativa:** 4h

---

### RF05 — Exclusão de endpoint ★ Obrigatório

**Descrição:** O usuário deve poder deletar um endpoint que não precisa mais.

**Critérios de aceitação:**
- Confirmação antes de excluir.
- Remoção imediata da listagem após exclusão.
- URL deixa de responder após a exclusão.

**Estimativa:** 2h

---

### RF06 — Configuração de status code ◎ Desejável

**Descrição:** O usuário pode definir o status HTTP que o endpoint irá retornar.

**Critérios de aceitação:**
- Campo de seleção com os principais códigos (200, 201, 400, 404, 500).
- Valor padrão 200.

**Estimativa:** 2h *(implementar na semana 6 se houver tempo)*

---

### RF07 — Edição de endpoint existente ◎ Desejável

**Descrição:** O usuário pode editar o JSON de um endpoint já criado sem alterar sua URL.

**Critérios de aceitação:**
- Formulário pré-preenchido com o JSON atual.
- URL permanece a mesma após edição.

**Estimativa:** 3h *(implementar na semana 6 se houver tempo)*

---

### RF08 — Configuração de delay de resposta ★ Obrigatório

**Descrição:** Atraso configurável na resposta do endpoint (0 a 10.000ms).

**Critérios de aceitação:**
- Campo `delayMs` na criação/edição do endpoint.
- O sistema pausa a resposta pelo tempo configurado usando `Thread.sleep` (Virtual Threads).

**Estimativa:** 2h (Implementado)

---

### RF09 — Histórico de Logs recebidos ★ Obrigatório

**Descrição:** O sistema exibe quantas vezes cada endpoint foi acessado
e de qual origem, permitindo ao usuário monitorar o consumo de sua URL.

**Critérios de aceitação:**
- Exibição do número total de acessos por endpoint na listagem.
- Data e IP da última requisição recebida.
- Histórico zerado ao deletar o endpoint.

**Estimativa:** 3h
**Justificativa:** Backend 100% implementado (RequestLog, RequestLogService,
RequestLogRepository). Custo concentrado apenas na camada de apresentação.

---

## 4. Requisitos Não Funcionais

| ID | Requisito | Prioridade | Solução planejada |
|---|---|---|---|
| RNF01 | URLs públicas disponíveis 24/7 | Alta | Supabase gerenciado + deploy em Railway ou Fly.io |
| RNF02 | Resposta em menos de 500ms | Alta | Índice em `hash` no PostgreSQL, sem cold-start |
| RNF03 | Suporte a CORS | Alta | `CorsConfig` global no Spring com `Access-Control-Allow-Origin: *` |
| RNF04 | Usabilidade — criar endpoint em até 2 minutos | Média | Fluxo de criação em tela única, feedback inline |
| RNF05 | Segurança básica — prevenção de abuso | Média | Tamanho máximo de JSON: 100 KB; autenticação obrigatória para criar endpoints |
| RNF06 | Escalabilidade | Baixa | Design stateless no controller público; índices no banco |
| RNF07 | Manutenibilidade — boas práticas de OO | Média | Separação em camadas (Controller → Service → Repository), SRP por classe |

---

## 5. Modelo de Dados Simplificado

```
mock_endpoints
  id          UUID PK
  usuario_id  UUID FK → usuarios.id
  hash        VARCHAR(8) UNIQUE
  payload     JSONB
  status_code SMALLINT DEFAULT 200
  criado_em   TIMESTAMPTZ
  atualizado_em TIMESTAMPTZ

request_logs
  id          BIGSERIAL PK
  endpoint_id UUID FK → mock_endpoints.id
  metodo      VARCHAR(10)
  called_at   TIMESTAMPTZ
  caller_ip   VARCHAR(45)
```

---

## 6. Fluxo Principal de Uso

```
1. Usuário acessa ResTest → interface carregada em `http:localhost:8080`
2. Preenche o formulário: nome, status HTTP, delay e paylodad em JSON
3. Clica em "Criar Endpoint" → sistema valida o JSON e gera o _hash_
4. A URL pública é exibida: `http:localhost:8080/api/{hash}`
5. Copia a URL e usa no front-end que está testando
6. Pode editar ou excluir o endpoint pela listagem
7. Cada acesso à URL pública é registrado no histórico (RF09)
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
