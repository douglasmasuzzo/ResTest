package com.example.ResTesT.dto;


// DTO utilizado para receber os dados
// enviados na criação de um endpoint mockado
public class CreateEndpointRequest {

    public String payload; // Conteúdo JSON que será retornado pela API mockada

    // Código HTTP da resposta
    // Exemplo: 200, 404, 500...
    public Integer statusCode;

    public Integer delayMs;  // Tempo de atraso da resposta em milissegundos
    public String label; // Nome ou descrição do endpoint mockado
}