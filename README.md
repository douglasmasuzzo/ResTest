# ResTest

> Ferramenta para desenvolvedores front-end criarem e gerenciarem endpoints REST falsos para testar suas interfaces de forma ágil e independente.

---

## 🚀 Sobre o projeto

O **ResTest** é uma aplicação fullstack que resolve um problema comum no desenvolvimento web: 
a dependência de APIs que ainda estão em desenvolvimento ou instáveis. Com o ResTest, você define 
a estrutura do dado que precisa e recebe uma URL funcional em segundos.

**Contexto Acadêmico:**
Projeto desenvolvido para a disciplina de **Programação Orientada a Objetos** (Fatec Praia Grande), orientado pelo prof. **Ricardo Pupo**.

**Time:**
- Douglas Masuzzo
- Otávio Augusto 
- Tiago Felipe 

---

## 🛠️ Stack Tecnológica

| Camada | Tecnologia |
|---|---|
| **Backend** | Java 21 + Spring Boot 3.x |
| **Frontend** | HTML5, CSS3 e JavaScript Vanilla |
| **Banco de Dados** | PostgreSQL (Supabase) |
| **Segurança** | Spring Security |
| **Build/Deps** | Maven |

---

## 📖 Documentação Central

Para detalhes profundos, consulte nossos guias técnicos:

*   📄 [**SPECS.md**](./SPECS.md) - _Arquitetura, Modelagem e Endpoints._
*   📄 [**AGENTS.md**](./AGENTS.md) - _Definição de papéis e responsabilidades (API Guardian, UI Crafter, etc)._
*   📄 [**requirements.md**](./requirements.md) - _Escopo funcional e cronograma._
*   📄 [**relatorio_testes.md**](/relatorio_testes.md) - _Relatório de Testes._

---

## 📈 Progresso do Projeto

### ✅ Semana 1 — Planejamento *(Concluído)*
- Elaboração dos documentos de especificação técnica e funcional.
- Definição da arquitetura em camadas e modelo de dados.

### ✅ Semanas 2-3 — Marco 1: Lógica e Backend (Concluído)
Nesta fase, focamos na "espinha dorsal" do sistema. A estrutura lógica foi entregue e está validada.

- [x] **Estrutura Base:** Setup do projeto Spring Boot e organização de pacotes (`com.example.ResTest`).
- [x] **Persistência:** Integração com Supabase e mapeamento das entidades `MockEndpoint` e `RequestLog`.
- [x] **Segurança:** Configuração inicial do Spring Security e `RateLimitFilter` por IP.
- [x] **Serviços de Negócio:** Implementação do `MockEndpointService`, `PayloadValidatorService` e `HashGeneratorService`.
- [x] **Endpoints REST:** Criação de controladores REST para comunicação com o frontend.
- [x] **Histórico de Acessos:** Registro e exposição de `RequestLog` [**RF09 - Histórico de Logs recebidos**](requirements.md).
- [x] **Testes Unitários:** 7 classes de testes cobertas: `controllers`, `exceptionHandler` e `service` (JUnit 5 + Mockito)
- [x] **Frontend Integrada:** Interface HTML/CSS/JS servida como recurso estático do SpringBoot em `./static`.

### ✅ Semanas 4 — Marco 2: Revisão Final (Em Andamento)

- [x] Validar integração frontend ↔ backend em ambiente real (Supabase)
- [x] Testes de integração com Testcontainers
- [x] Deploy em cloud (Railway ou Fly.io)
- [ ] Revisão final da documentação
---

## 🔗 Pull Requests Fechados

| PR | Descrição | Status |
|---|---|---|
| `#1` | **chore: entrega da camada visual** - Implementação da interface frontend para gerenciamento de endpoints mockados. | ✅ Review concluído |
| `#2` | **chore: entrega da camada lógica** - Integração da camada lógica com a camada de persistência, testes unitários e frontend estático. | ✅ Review concluído |

---

## 💻 Como Rodar (Backend)

1.  Certifique-se de ter o **Java 21** e **Maven** instalados.
2.  Configure as variáveis de ambiente necessárias:
    ```bash
    SPRING_DATASOURCE_URL=sua_url_supabase
    SPRING_DATASOURCE_USERNAME=seu_usuario
    SPRING_DATASOURCE_PASSWORD=sua_senha
    ```
3.  Navegue até a pasta `Backend` e execute:
    ```bash
    mvn spring-boot:run
    ```
4. Acesse em: 
    `http://localhost:8080`



---

## 🗺️ Próximos Passos

*   **Imediato:** Solucionar divergência entre as branches `backend` e `main` ( `git merge origin/main` ).
*   **Parcial:** Finalizar revisão geral do projeto