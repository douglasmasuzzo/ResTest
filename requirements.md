# Documento de Especificação Funcional — ResTest

**Disciplina:** Programação Orientada a Objetos  
**Docente:** Ricardo Pupo   
**Time:** Douglas, Tiago e Otávio  
**Início:** 28 de abril de 2026  
**Entrega:** 16 de junho de 2026  

---

## 1. Descrição do Produto

O **ResTest** é uma aplicação web que permite a desenvolvedores front-end criar endpoints REST falsos para testar suas interfaces. O usuário define um payload JSON e o sistema gera automaticamente uma URL pública que retorna aquele JSON via HTTP, com autenticação própria para que cada usuário gerencie seus próprios endpoints.

---

## 2. Stack Tecnológica

| Camada | Tecnologia |
|---|---|
| Backend | Java 21 + Spring Boot 3.x |
| Frontend | React 18 + Hilla (Vaadin) |
| Banco de Dados | PostgreSQL via Supabase |
| Autenticação | Spring Security (sessão com usuário/senha) |

---

## 3. Requisitos Funcionais — Escopo da Entrega

Os requisitos estão classificados em **obrigatórios** (entram na entrega) e **desejáveis** (implementar somente se sobrar tempo). Essa divisão foi calibrada para o orçamento de ~35 horas com time iniciante.

---

### RF01 — Cadastro e login de usuário ★ Obrigatório

**Descrição:** O sistema deve permitir que o usuário crie uma conta e faça login para acessar seus endpoints.

**Critérios de aceitação:**
- Formulário de cadastro com e-mail e senha.
- Formulário de login com sessão mantida via cookie.
- Acesso às funcionalidades bloqueado para usuários não autenticados.
- Logout disponível na interface.

**Estimativa:** 6h

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

### RF08 — Configuração de delay de resposta ✗ Fora do escopo

**Descrição:** Atraso configurável na resposta do endpoint.

**Justificativa de exclusão:** Baixa prioridade pedagógica e impacto pequeno no usuário. Pode entrar em versão futura.

---

### RF09 — Log de requisições recebidas ✗ Fora do escopo

**Descrição:** Registro e exibição das últimas chamadas feitas a cada endpoint.

**Justificativa de exclusão:** Requer infraestrutura adicional (tabela de logs, atualização assíncrona) que ultrapassa o orçamento disponível.

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
usuarios
  id          UUID PK
  email       VARCHAR UNIQUE
  senha_hash  VARCHAR
  criado_em   TIMESTAMPTZ

mock_endpoints
  id          UUID PK
  usuario_id  UUID FK → usuarios.id
  hash        VARCHAR(8) UNIQUE
  payload     JSONB
  status_code SMALLINT DEFAULT 200
  criado_em   TIMESTAMPTZ
  atualizado_em TIMESTAMPTZ
```

---

## 6. Fluxo Principal de Uso

```
1. Usuário acessa ResTest → tela de login/cadastro
2. Faz login → é redirecionado para sua lista de endpoints
3. Clica em "Novo endpoint" → abre formulário
4. Cola o JSON → sistema valida a sintaxe em tempo real
5. Clica em "Criar" → sistema gera URL pública
6. Copia a URL com 1 clique → usa no front-end que está testando
7. Pode excluir o endpoint quando não precisar mais
```

---

## 7. Distribuição de Responsabilidades Sugerida

| Área | Responsável sugerido |
|---|---|
| Backend (Spring Boot, endpoints, banco) | Douglas + Tiago |
| Frontend (React + Hilla, telas, UX) | Otávio + Douglas |
| Configuração de infraestrutura (Supabase, deploy) | Tiago |

> A distribuição pode ser ajustada conforme afinidade do time. O importante é que cada integrante tenha pelo menos uma entrega de backend e uma de frontend, atendendo ao critério pedagógico da disciplina de POO.

---

## 8. Riscos e Mitigações

| Risco | Probabilidade | Mitigação |
|---|---|---|
| Curva de aprendizado com Hilla/Vaadin | Alta | Reservar a semana 1 inteira para setup e tutoriais; usar os exemplos oficiais do Vaadin |
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
