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
- Tiago Felipe 
- Otávio Augusto 

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

*   📄 [**SPECS.md**](./SPECS.md) - Arquitetura, Modelagem e Endpoints.
*   📄 [**AGENTS.md**](./AGENTS.md) - Definição de papéis e responsabilidades (API Guardian, UI Crafter, etc).
*   📄 [**requirements.md**](./requirements.md) - Escopo funcional e cronograma.

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
- [ ] **Frontend Initial:** Dashboard de endpoints em HTML/JS.

---

## 🔗 Pull Requests Ativos

| PR | Descrição | Status |
|---|---|---|
| `#2` | **chore: entrega da estrutura lógica** - Implementação completa do Backend, Services e Security. | 🕒 Aguardando Review |

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

---

## 🗺️ Próximos Passos

*   **Marco 1:** Validação completa da integração frontend ↔ backend e tela de listagem de endpoints.
*   **Marco 2:** Refinamento de UX, testes unitários nos Services e deploy em cloud (Railway ou Fly.io).