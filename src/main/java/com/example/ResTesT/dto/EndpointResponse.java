package com.example.ResTesT.dto;


// Importa classe UUID para gerar identificadores
import java.util.UUID;

// DTO utilizado para retornar os dados
// de um endpoint mockado para o cliente
public class EndpointResponse {
    public UUID id; // ID único do endpoint
    public String hash; // Hash utilizado para acessar o endpoint público
    public String url; // URL completa do endpoint mockado
    public String payload; // Payload JSON retornado pelo endpoint
    public int statusCode; // Código HTTP da resposta
    public int delayMs; // Delay configurado em milissegundos
    public String label; // Nome ou descrição do endpoint
}