# ResTest

> Ferramenta para desenvolvedores front-end criarem e gerenciarem endpoints REST falsos para testar suas interfaces.

---

## Sobre o projeto

O **ResTest** nasceu como projeto acadêmico da disciplina de **Programação Orientada a Objetos** da **Fatec Praia Grande**, sob orientação do docente **Ricardo Pupo**. 
A motivação é simples: desenvolvedores front-end frequentemente precisam testar telas que dependem de dados vindos de uma API — mas essa API ainda não existe, está incompleta ou pertence a outro time. O ResTest resolve isso em segundos: o usuário cola um JSON, recebe uma URL pública e já pode usar essa URL em qualquer `fetch` ou `axios` da sua aplicação.

**Time:**
- Douglas Masuzzo
- Tiago
- Otávio

**Período:** Abril – Junho de 2026

---

## Como funciona

```
1. Usuário cria uma conta e faz login
2. Cola o JSON que quer que a API retorne
3. O sistema gera uma URL pública única
4. O usuário usa essa URL diretamente no front-end que está desenvolvendo
5. A URL responde com o JSON configurado, status code correto e CORS habilitado
```

Exemplo de uso em código:

```javascript
// Antes: URL real da API (que ainda não existe)
// const res = await fetch('https://minha-api.com/usuarios')

// Com ResTest: URL gerada em segundos
const res = await fetch('https://restest.app/api/a3f9bc12')
const data = await res.json()
```

---

## Stack tecnológica

| Camada | Tecnologia |
|---|---|
| Backend | Java 21 + Spring Boot 3.x |
| Frontend | React 18 + Hilla (Vaadin) |
| Banco de Dados | PostgreSQL (Supabase) |
| Autenticação | Spring Security |
| Build | Maven |

A escolha do **Hilla (Vaadin)** como framework de integração elimina a necessidade de escrever uma camada REST manualmente entre o frontend React e o backend Spring Boot — os métodos Java anotados com `@BrowserCallable` são expostos diretamente como funções TypeScript no cliente.

---

## Documentação do projeto

| Arquivo | Conteúdo |
|---|---|
| [`SPECS.md`](./SPECS.md) | Especificação técnica: arquitetura, modelo de dados, pacotes, endpoints e regras de negócio |
| [`AGENTS.md`](./AGENTS.md) | Modelo de agentes: responsabilidades, contratos e interações entre os componentes |
| [`requirements.md`](./requirements.md) | Especificação funcional: requisitos, cronograma, riscos e distribuição de tarefas |

---

## Progresso do projeto

### ✅ Semana 1 — Setup e documentação base *(28/04/2026)*

Marco inicial do projeto. Nesta fase foram produzidos os três documentos fundamentais que guiam o desenvolvimento:

- Definição da arquitetura em camadas (Controller → Service → Repository)
- Modelagem do banco de dados com suporte a `JSONB` no PostgreSQL
- Especificação dos 7 agentes de software e seus contratos de comunicação
- Levantamento e priorização dos requisitos funcionais com base no prazo real de ~35h
- Identificação dos principais riscos: curva de aprendizado com Hilla/Vaadin e integração com Spring Security

**Artefatos entregues nesta semana:**

```
SPECS.md        → especificação técnica completa
AGENTS.md       → modelo de agentes de software
requirements.md → escopo funcional calibrado para o prazo
```

**Próximo passo:** setup do projeto Spring Boot + Hilla + conexão com Supabase.

---

### 🔜 Semanas 2–3 — Marco 1: Autenticação e CRUD *(previsto: 05/05 – 18/05)*

- [ ] Criação do projeto Spring Boot com Hilla
- [ ] Conexão com Supabase (SSL + variáveis de ambiente)
- [ ] Entidades `Usuario` e `MockEndpoint` com JPA
- [ ] Spring Security: cadastro, login e sessão por cookie
- [ ] Hilla Endpoint: `create`, `findAll`, `delete`
- [ ] Tela de login/cadastro no React

---

### 🔜 Semanas 4–5 — Marco 2: URL pública e validação *(previsto: 19/05 – 01/06)*

- [ ] `HashGeneratorService` com tratamento de colisão (Base62, 8 chars)
- [ ] `PublicApiController` — rota `GET /api/{hash}` com CORS
- [ ] `PayloadValidator` com Jackson (limite 100 KB)
- [ ] Tela de criação de endpoint com editor JSON e feedback inline

---

### 🔜 Semana 6 — Marco 3: Listagem e polish *(previsto: 02/06 – 08/06)*

- [ ] Tela de listagem com trecho do JSON e botão de copiar URL
- [ ] RF06 — seleção de status code *(se houver tempo)*
- [ ] RF07 — edição de endpoint existente *(se houver tempo)*

---

### 🔜 Semana 7 — Testes e entrega *(previsto: 09/06 – 16/06)*

- [ ] Testes unitários nos Services (JUnit 5 + Mockito)
- [ ] Testes de integração com Testcontainers
- [ ] Deploy em cloud (Railway ou Fly.io)
- [ ] Revisão final da documentação

---

## Arquitetura resumida

```
Browser (React + Hilla)
        │
        │ Hilla RPC (CRUD interno)     GET /api/{hash} (público)
        ▼                                      ▼
              Spring Boot Application
        │                                      │
        └──────────── Service Layer ───────────┘
                            │
                   Repository (JPA)
                            │
                  Supabase PostgreSQL
```

---

## Como rodar localmente

> Instruções serão adicionadas ao final do Marco 1 (semana 3), após o setup estar estável.
