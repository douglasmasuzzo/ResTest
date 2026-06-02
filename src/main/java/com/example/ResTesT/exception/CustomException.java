package com.example.ResTesT.exception;

/**
 * Exceção personalizada da aplicação ResTest.
 *
 * O que é uma exceção personalizada?
 *   O Java possui várias exceções prontas (NullPointerException, IllegalArgumentException, etc.).
 *   Uma exceção personalizada é criada por nós para representar erros específicos
 *   do nosso sistema, com mensagens mais claras e significativas.
 *
 * Por que usar RuntimeException como base?
 *   Existem dois tipos de exceções em Java:
 *
 *   - Checked Exceptions (extends Exception):
 *       O compilador OBRIGA você a tratar ou declarar no metodo (throws).
 *       Exemplo: IOException (erros de arquivo)
 *
 *   - Unchecked Exceptions (extends RuntimeException):
 *       Não precisam ser declaradas ou capturadas obrigatoriamente.
 *       São mais práticas para erros de regra de negócio.
 *
 *   Como os erros de negócio (payload inválido, mock não encontrado) são
 *   detectados em tempo de execução e não precisam de tratamento obrigatório
 *   em todo lugar, usamos RuntimeException.
 *
 * Como funciona no projeto?
 *   1. O serviço lança: throw new CustomException("Mock não encontrado")
 *   2. O GlobalExceptionHandler captura automaticamente
 *   3. Retorna HTTP 400 com a mensagem de erro para o cliente
 */
public class CustomException extends RuntimeException {

    /**
     * Construtor que recebe a mensagem de erro.
     *
     * @param message descrição clara do que deu errado.
     *                Exemplos: "JSON inválido", "Mock não encontrado", "Erro ao gerar hash único"
     *
     * super(message) repassa a mensagem para a classe pai (RuntimeException),
     * que a armazena internamente e a disponibiliza via getMessage().
     */
    public CustomException(String message) {
        super(message);
    }
}