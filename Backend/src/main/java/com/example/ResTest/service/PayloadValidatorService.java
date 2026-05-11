package com.example.ResTest.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.ResTest.exception.CustomException;
import org.springframework.stereotype.Service;

@Service
public class PayloadValidatorService {

    private final ObjectMapper mapper = new ObjectMapper();
    private static final int MAX_SIZE = 100 * 1024;

    public void validate(String json) {
        try {
            mapper.readTree(json);
        } catch (Exception e) {
            throw new CustomException("JSON inválido");
        }

        if (json.getBytes().length > MAX_SIZE) {
            throw new CustomException("JSON excede 100KB");
        }
    }
}