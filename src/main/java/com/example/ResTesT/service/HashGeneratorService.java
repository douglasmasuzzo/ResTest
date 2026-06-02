package com.example.ResTesT.service;

// Importa a anotação que registra esta classe como um serviço gerenciado pelo Spring
import org.springframework.stereotype.Service;

// Importa SecureRandom para geração criptograficamente segura de números aleatórios
import java.security.SecureRandom;

/**
 * Serviço responsável por gerar hashes únicos para os endpoints mockados.
 *
 * O que é um "hash" neste contexto?
 *   É uma string curta de 8 caracteres aleatórios usada como identificador público
 *   do endpoint na URL. Exemplo: "aB3xYz12" → /api/aB3xYz12
 *
 * Por que gerar um hash em vez de usar o UUID do banco?
 *   - UUIDs são longos e difíceis de compartilhar (ex: "550e8400-e29b-41d4-a716-446655440000")
 *   - Hashes curtos são mais fáceis de copiar, compartilhar e lembrar
 *   - Seguem o padrão de ferramentas conhecidas como TinyURL e Bitly
 *
 * Por que usar SecureRandom em vez de Random?
 *   - Random usa um algoritmo determinístico — dado o mesmo "seed" (semente),
 *     sempre gera a mesma sequência. Um atacante que conhece o seed pode prever
 *     os próximos hashes gerados.
 *   - SecureRandom usa fontes de entropia do sistema operacional (ruído de hardware,
 *     movimentos do mouse, etc.), tornando os resultados imprevisíveis e seguros.
 *   - Para um sistema que gera URLs públicas, previsibilidade seria uma vulnerabilidade.
 */
@Service // Registra esta classe como bean de serviço — o Spring a gerencia e injeta automaticamente
public class HashGeneratorService {

    /**
     * Conjunto de caracteres permitidos no hash gerado.
     *
     * Usamos letras maiúsculas + minúsculas + dígitos (base62).
     * Total de combinações possíveis com 8 caracteres:
     *   62^8 = 218.340.105.584.896 combinações (~218 trilhões)
     *
     * Por que excluímos caracteres especiais?
     *   Caracteres como /, ?, #, & têm significado especial em URLs.
     *   Usá-los no hash causaria problemas ao montar a URL /api/{hash}.
     *
     * Por que excluímos 0, O, l, 1 (comumente confundidos)?
     *   Poderíamos, mas optamos por manter todos para maximizar o espaço de combinações.
     */
    private static final String CARACTERES_PERMITIDOS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    /**
     * Tamanho do hash gerado.
     *
     * 8 caracteres oferecem ~218 trilhões de combinações — suficiente para
     * que a probabilidade de colisão seja desprezível mesmo com muitos endpoints.
     * A cada tentativa de criação, a chance de colidir com um hash existente
     * em um banco com 1 milhão de registros é de apenas 0,00000046%.
     */
    private static final int TAMANHO_DO_HASH = 8;

    /**
     * Gerador de números aleatórios criptograficamente seguro.
     *
     * Declarado como campo da classe (não dentro do metodo) para reutilizar
     * a mesma instância em todas as chamadas — criar um novo SecureRandom
     * a cada chamada seria custoso e desnecessário.
     */
    private final SecureRandom random = new SecureRandom();

    /**
     * Gera e retorna um hash aleatório de 8 caracteres.
     *
     * O algoritmo:
     *   1. Cria um StringBuilder vazio
     *   2. Em um loop de 8 iterações, escolhe um caractere aleatório
     *      de CARACTERES_PERMITIDOS usando um índice gerado pelo SecureRandom
     *   3. Adiciona o caractere ao StringBuilder
     *   4. Retorna a string final
     *
     * Exemplo de saída: "aB3xYz12", "Kp9mQr4T", "zZ0aBc7W"
     *
     * @return uma string de 8 caracteres aleatórios e seguros
     */
    public String generate() {

        // StringBuilder é mais eficiente que concatenação de strings em loop
        // porque não cria objetos String intermediários a cada iteração
        StringBuilder hash = new StringBuilder();

        // Gera cada caractere do hash individualmente
        for (int i = 0; i < TAMANHO_DO_HASH; i++) {

            // Sorteia um índice aleatório dentro do range de CARACTERES_PERMITIDOS
            // nextInt(n) retorna um número entre 0 (inclusive) e n (exclusive)
            int indiceAleatorio = random.nextInt(CARACTERES_PERMITIDOS.length());

            // Pega o caractere na posição sorteada e adiciona ao hash
            hash.append(CARACTERES_PERMITIDOS.charAt(indiceAleatorio));
        }

        // Converte o StringBuilder para String e retorna o hash final
        return hash.toString();
    }
}