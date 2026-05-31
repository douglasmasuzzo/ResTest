# Relatório de Testes Unitários – Projeto ResTesT

## Objetivo

Garantir o correto funcionamento das principais regras de negócio e componentes da aplicação ResTesT 
através de testes automatizados utilizando JUnit 5 e Mockito.

---

## Estrutura dos Testes

Os testes foram organizados na seguinte estrutura:

```
src
└── test
    └── java
        └── com.example.ResTesT
            ├── service
            │   ├── HashGeneratorServiceTest
            │   ├── PayloadValidatorServiceTest
            │   └── MockEndpointServiceTest
            │
            ├── exception
            │   └── GlobalExceptionHandlerTest
            │
            └── ResTesTApplicationTests
```


---


# 1. HashGeneratorServiceTest

## Objetivo

- Validar a geração dos hashes utilizados pelos endpoints mockados.

## Cenários Testados

### **`deveGerarHashCom8Caracteres()`**

- Verifica se o hash gerado possui exatamente 8 caracteres.

### **`deveGerarHashesDiferentes()`**

- Verifica se duas gerações consecutivas produzem valores distintos.

## Resultado

✅ Testes aprovados.


---


# 2. PayloadValidatorServiceTest

## Objetivo

- Garantir a validação correta dos payloads JSON.

## Cenários Testados

### **`deveAceitarJsonValido()`**

- Verifica se um JSON válido é aceito sem exceções.

### **`deveLancarExcecaoParaJsonInvalido()`**

- Verifica se um JSON mal formatado gera uma CustomException.

### **`deveLancarExcecaoParaJsonMaiorQue100KB()`**

- Verifica se payloads acima do limite permitido são rejeitados.

## Resultado

✅ Testes aprovados.


---


# 3. MockEndpointServiceTest

## Objetivo

- Validar as regras de negócio relacionadas aos endpoints mockados.

## Cenários Testados

### **`deveCriarEndpoint()`**

- Verifica a criação de um endpoint mockado.

### **`deveAtualizarEndpoint()`**

- Verifica a atualização dos dados de um endpoint existente.

### **`deveBuscarPorHash()`**

- Verifica a busca de endpoint utilizando o hash.

### **`deveLancarExcecaoQuandoHashNaoExiste()`**

- Verifica o tratamento quando um hash inexistente é informado.

## Resultado

✅ Testes aprovados.


---


# 4. GlobalExceptionHandlerTest

## Objetivo

- Validar o tratamento centralizado de exceções da aplicação.

## Cenários Testados

### **`deveRetornar400ParaCustomException()`**

- Verifica se exceções de negócio retornam HTTP 400.

### **`deveRetornar500ParaErroGenerico()`**

- Verifica se exceções genéricas retornam HTTP 500.

## Observação

Durante a execução do teste de erro genérico, o stack trace é exibido no console porque o método:

```java
ex.printStackTrace();
```

faz parte da implementação do GlobalExceptionHandler.

Esse comportamento é esperado e não representa falha no teste.

## Resultado

✅ Testes aprovados.


---


# 5. ResTesTApplicationTests

## Objetivo

- Garantir que o contexto Spring Boot seja carregado corretamente.

## Cenário Testado

### contextLoads()

Verifica se toda a aplicação consegue iniciar corretamente, carregando:

* Controllers
* Services
* Repositories
* Configurações Spring
* Conexão JPA
* EntityManager
* Contexto da aplicação

## Evidência

Resultado obtido:

```
1 test passed
Process finished with exit code 0
```

## Resultado

✅ Aplicação inicializada com sucesso.


---


# Resumo Geral

| Classe de Teste             | Status     |
| --------------------------- | ---------- |
| HashGeneratorServiceTest    | ✅ Aprovado |
| PayloadValidatorServiceTest | ✅ Aprovado |
| MockEndpointServiceTest     | ✅ Aprovado |
| GlobalExceptionHandlerTest  | ✅ Aprovado |
| ResTesTApplicationTests     | ✅ Aprovado |


---


# Conclusão

Todos os testes executados foram concluídos com sucesso.

Os resultados demonstram que:

* A geração de hashes funciona corretamente.
* A validação de payloads JSON está protegida contra dados inválidos.
* As operações de CRUD dos endpoints mockados estão funcionando.
* O tratamento global de exceções responde adequadamente.
* O contexto Spring Boot inicia sem erros.
* A aplicação encontra-se estável para continuidade do desenvolvimento e integração.
