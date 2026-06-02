package com.example.ResTesT.service;

// Importa a exceção personalizada da aplicação
import com.example.ResTesT.exception.CustomException;

// Importa o ObjectMapper do Jackson — biblioteca para manipulação de JSON em Java
import com.fasterxml.jackson.databind.ObjectMapper;

// Importa a anotação que registra esta classe como serviço do Spring
import org.springframework.stereotype.Service;

/**
 * Serviço responsável por validar o payload JSON enviado pelo usuário.
 *
 * O que este serviço valida?
 *   1. Se o conteúdo enviado é um JSON válido (sintaxe correta)
 *   2. Se o tamanho do JSON não ultrapassa o limite permitido (100 KB)
 *
 * Por que validar antes de salvar?
 *   - Garante que o endpoint mockado sempre retornará um JSON válido para quem o acessar
 *   - Evita que dados inválidos sejam armazenados no banco
 *   - Previne ataques de payload gigante que consumiriam muito espaço em disco (DoS via body)
 *
 * Exemplos de payloads:
 *   VÁLIDO:   {"nome": "Douglas", "ativo": true}
 *   VÁLIDO:   [{"id": 1}, {"id": 2}]
 *   INVÁLIDO: {nome: Douglas}       → falta aspas nas chaves
 *   INVÁLIDO: {"nome": "Douglas"    → falta fechar a chave
 */
@Service // Registra esta classe como bean de serviço gerenciado pelo Spring
public class PayloadValidatorService {

    /**
     * ObjectMapper da biblioteca Jackson — responsável por interpretar e converter JSON.
     *
     * Declarado como campo da classe para reutilizar a mesma instância em todas as chamadas.
     * Criar um ObjectMapper novo a cada validação seria desnecessariamente custoso.
     */
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Tamanho máximo permitido para o payload em bytes.
     *
     * 100 KB = 100 * 1024 = 102.400 bytes
     *
     * Por que limitar em 100 KB?
     *   - Payloads reais de APIs raramente ultrapassam alguns kilobytes
     *   - Sem limite, um usuário poderia enviar um JSON de 100 MB, que:
     *       a) consumiria muito espaço no banco de dados
     *       b) tornaria cada resposta do endpoint lenta (100 MB por requisição)
     *       c) poderia derrubar o servidor (ataque DoS)
     *   - 100 KB é generoso o suficiente para qualquer payload legítimo
     */
    private static final int TAMANHO_MAXIMO_BYTES = 100 * 1024;

    /**
     * Valida se o JSON informado é sintaticamente correto e está dentro do limite de tamanho.
     *
     * Fluxo de validação:
     *   1. Tenta interpretar o JSON usando o ObjectMapper
     *      → Se falhar, o JSON tem sintaxe inválida → lança CustomException
     *   2. Verifica o tamanho em bytes da string
     *      → Se ultrapassar 100 KB → lança CustomException
     *   3. Se passar nas duas verificações, o método retorna normalmente (sem exceção)
     *
     * Por que lançar CustomException em vez de retornar boolean?
     *   Lançar exceção permite que o GlobalExceptionHandler trate o erro automaticamente
     *   e retorne uma resposta HTTP 400 com a mensagem de erro para o cliente,
     *   sem precisar de código extra no Controller ou Service.
     *
     * @param json a string JSON a ser validada
     * @throws CustomException se o JSON for inválido ou ultrapassar o limite de tamanho
     */
    public void validate(String json) {

        // ─── PASSO 1: Verificar se o JSON tem sintaxe válida ───────────────────────
        try {

            // readTree() tenta parsear o JSON e constrói uma árvore de nós.
            // Se o JSON for inválido (chave sem aspas, vírgula extra, etc.),
            // lança JsonProcessingException que capturamos abaixo.
            mapper.readTree(json);

        } catch (Exception e) {

            // O JSON não pôde ser interpretado — sua sintaxe está incorreta.
            // Lançamos CustomException que será capturada pelo GlobalExceptionHandler.
            throw new CustomException(
                    "Payload inválido: o conteúdo enviado não é um JSON válido. " +
                            "Verifique a sintaxe (aspas, vírgulas, chaves)."
            );
        }

        // ─── PASSO 2: Verificar o tamanho do JSON ──────────────────────────────────

        // getBytes() retorna os bytes da string na codificação padrão do sistema (UTF-8).
        // Usamos .length na array de bytes (não na String) porque caracteres especiais
        // como acentos e emojis podem ocupar mais de 1 byte cada.
        int tamanhoEmBytes = json.getBytes().length;

        if (tamanhoEmBytes > TAMANHO_MAXIMO_BYTES) {

            // O payload ultrapassa o limite permitido.
            // Informamos ao cliente o tamanho atual e o máximo permitido.
            throw new CustomException(
                    "Payload muito grande: " + tamanhoEmBytes + " bytes. " +
                            "O limite máximo é " + TAMANHO_MAXIMO_BYTES + " bytes (100 KB)."
            );
        }

        // Se chegou até aqui sem exceção, o JSON é válido e está dentro do limite.
    }
}