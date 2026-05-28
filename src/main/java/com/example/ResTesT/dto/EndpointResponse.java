package com.example.ResTesT.dto;

import java.util.UUID;

public class EndpointResponse {
    public UUID id;
    public String hash;
    public String url;
    public String payload;
    public int statusCode;
    public int delayMs;
    public String label;
}