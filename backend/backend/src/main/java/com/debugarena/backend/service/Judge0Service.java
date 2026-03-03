package com.debugarena.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.Map;

@Service
public class Judge0Service {

    private final RestTemplate restTemplate;

    private final String API_URL =
            "https://ce.judge0.com/submissions?base64_encoded=false&wait=true";

    public Judge0Service(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Map<String, String> runCode(String code, String language) {

        int languageId = switch (language.toLowerCase()) {
            case "python" -> 71;
            case "java" -> 62;
            case "c" -> 50;
            default -> 71;
        };

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of(
                "source_code", code,
                "language_id", languageId,
                "stdin", ""
        );

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(body, headers);

        ResponseEntity<Map> response =
                restTemplate.postForEntity(API_URL, request, Map.class);

        Map<String, String> result = new java.util.HashMap<>();

        if (response.getBody() != null) {

            Map<String, Object> responseBody = response.getBody();

            // ✅ If program executed successfully
            if (responseBody.get("stdout") != null) {
                result.put("status", "SUCCESS");
                result.put("message", responseBody.get("stdout").toString());
                return result;
            }

            // ❌ If any compilation or runtime error
            if (responseBody.get("stderr") != null ||
                    responseBody.get("compile_output") != null) {

                result.put("status", "ERROR");
                result.put("message", "Error. Try again.");
                return result;
            }
        }

        result.put("status", "ERROR");
        result.put("message", "Error. Try again.");
        return result;
    }}