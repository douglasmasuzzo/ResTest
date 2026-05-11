package com.example.ResTest.controller;


import com.example.ResTest.service.*;
import com.example.ResTest.domain.MockEndpoint;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import com.example.ResTest.service.MockEndpointService;
import com.example.ResTest.service.RequestLogService;

@RestController
@RequestMapping("/api")
public class PublicApiController {

    private final MockEndpointService service;
    private final RequestLogService logService;

    public PublicApiController(MockEndpointService service,
                               RequestLogService logService) {
        this.service = service;
        this.logService = logService;
    }

    @GetMapping("/{hash}")
    public ResponseEntity<?> get(@PathVariable String hash,
                                HttpServletRequest request) throws Exception {

        MockEndpoint e = service.findByHash(hash);

        if (e.getDelayMs() > 0) {
            Thread.sleep(e.getDelayMs());
        }

        logService.log(e, request.getRemoteAddr());

        return ResponseEntity
                .status(e.getStatusCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(e.getPayload());
    }

}