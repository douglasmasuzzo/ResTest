package com.example.ResTesT.exception;

// Importa a classe para construir respostas HTTP com status e corpo personalizados
import org.springframework.http.ResponseEntity;

// Importa anotações do Spring para interceptar exceções globalmente
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Importa para coletar múltiplos erros de validação
import java.util.HashMap;
import java.util.Map;

/**
 * Tratador global de exceções da aplicação.
 *
 * O que é um "tratador global de exceções"?
 *   Normalmente, quando uma exceção não é capturada dentro de um Controller,
 *   o Spring retorna um erro genérico (geralmente HTTP 500 com HTML feio).
 *
 *   Com @RestControllerAdvice, esta classe intercepta TODAS as exceções
 *   não tratadas de qualquer Controller e retorna respostas JSON padronizadas.
 *
 * Como funciona o fluxo?
 *   Controller → lança exceção → GlobalExceptionHandler captura → retorna JSON de erro
 *
 * Por que centralizar o tratamento de erros?
 *   - Padroniza o formato das respostas de erro em toda a API
 *   - Evita repetição de blocos try/catch em todos os Controllers
 *   - Facilita a manutenção — para mudar o formato do erro, muda só aqui
 */
@RestControllerAdvice // Combina @ControllerAdvice + @ResponseBody: intercepta exceções e retorna JSON
public class GlobalExceptionHandler {

    /**
     * Trata exceções de regra de negócio da aplicação (CustomException).
     *
     * Quando o serviço lança um CustomException (ex: "Mock não encontrado"),
     * este método é chamado automaticamente e retorna HTTP 400 Bad Request
     * com a mensagem de erro no corpo da resposta.
     *
     * Por que 400 (Bad Request)?
     *   Porque o erro é causado por uma requisição inválida do cliente
     *   (ex: ID inexistente, payload malformado) — não é falha do servidor.
     *
     * @param ex a exceção capturada com a mensagem de erro
     * @return resposta HTTP 400 com a mensagem no corpo
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Map<String, String>> tratarCustomException(CustomException ex) {

        // Cria um mapa para retornar a mensagem de erro em formato JSON
        // Resultado: {"erro": "Mock não encontrado"}
        Map<String, String> corpo = new HashMap<>();
        corpo.put("erro", ex.getMessage());

        // Retorna HTTP 400 (Bad Request) com o corpo JSON de erro
        return ResponseEntity.badRequest().body(corpo);
    }

    /**
     * Trata erros de validação de campos do DTO (anotações @NotBlank, @Min, @Max, etc.).
     *
     * Quando o cliente envia um JSON com campos inválidos (ex: payload vazio, statusCode 999),
     * o Spring lança automaticamente MethodArgumentNotValidException antes mesmo de chegar
     * no Controller. Este método captura essa exceção e retorna todos os erros encontrados.
     *
     * Por que retornar todos os erros de uma vez?
     *   Para que o cliente saiba exatamente o que precisa corrigir sem precisar
     *   fazer várias tentativas — melhor experiência de uso da API.
     *
     * Exemplo de resposta:
     * {
     *   "payload": "O payload não pode ser vazio",
     *   "statusCode": "O status code mínimo permitido é 100"
     * }
     *
     * @param ex a exceção contendo todos os erros de validação encontrados
     * @return resposta HTTP 400 com mapa de campo → mensagem de erro
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> tratarErosDeValidacao(MethodArgumentNotValidException ex) {

        // Cria mapa para armazenar os erros no formato: nomeDoCampo → mensagemDeErro
        Map<String, String> erros = new HashMap<>();

        // Percorre todos os erros de validação encontrados
        ex.getBindingResult().getFieldErrors().forEach(erro ->

                // Para cada erro, adiciona no mapa: nome do campo → mensagem configurada na anotação
                erros.put(erro.getField(), erro.getDefaultMessage())
        );

        // Retorna HTTP 400 com todos os erros de validação
        return ResponseEntity.badRequest().body(erros);
    }

    /**
     * Trata qualquer exceção genérica não prevista anteriormente (fallback geral).
     *
     * Este método funciona como uma "rede de segurança" — captura qualquer exceção
     * que não foi tratada pelos outros @ExceptionHandler acima.
     *
     * Por que HTTP 500 (Internal Server Error)?
     *   Exceções não previstas geralmente indicam um bug no servidor,
     *   não um erro causado pelo cliente.
     *
     * A mensagem retornada é genérica de propósito:
     *   Não queremos expor detalhes internos da aplicação (stack trace, nomes de classe)
     *   para o cliente por questões de segurança.
     *   O erro completo é impresso no console do servidor para debugging.
     *
     * @param ex a exceção genérica capturada
     * @return resposta HTTP 500 com mensagem genérica de erro
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> tratarErroGenerico(Exception ex) {

        // Imprime o stack trace completo no console do servidor para facilitar o debugging
        // (visível apenas para o desenvolvedor, nunca para o cliente)
        ex.printStackTrace();

        // Cria resposta genérica — não revela detalhes internos ao cliente
        Map<String, String> corpo = new HashMap<>();
        corpo.put("erro", "Ocorreu um erro interno no servidor. Tente novamente mais tarde.");

        // Retorna HTTP 500 (Internal Server Error)
        return ResponseEntity.internalServerError().body(corpo);
    }
}