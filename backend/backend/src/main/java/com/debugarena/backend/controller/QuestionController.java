package com.debugarena.backend.controller;
import com.debugarena.backend.SubmissionRequest;
import com.debugarena.backend.model.SubmissionResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.ClassPathResource;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import java.util.Collections;

import org.springframework.web.client.RestTemplate;
import com.debugarena.backend.model.Question;
import org.springframework.web.bind.annotation.CrossOrigin;
import java.io.InputStream;
import java.util.List;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
@CrossOrigin(origins = "*")
@RestController
public class QuestionController {

    private final RestTemplate restTemplate = new RestTemplate();

    private final String JUDGE0_URL =
            "https://ce.judge0.com/submissions?base64_encoded=false&wait=true";

    @GetMapping("/questions")
    public List<Question> getQuestions() throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        InputStream inputStream =
                new ClassPathResource("questions.json").getInputStream();

        List<Question> questions = mapper.readValue(
                inputStream,
                new com.fasterxml.jackson.core.type.TypeReference<List<Question>>() {}
        );

        // ✅ Shuffle questions randomly
        Collections.shuffle(questions);

        return questions;
    }

    @PostMapping("/submit")
    public SubmissionResponse submitCode(@RequestBody SubmissionRequest request) {

        try {
            // 🔹 Load questions.json
            ObjectMapper mapper = new ObjectMapper();
            InputStream inputStream =
                    new ClassPathResource("questions.json").getInputStream();

            List<Question> questions = mapper.readValue(
                    inputStream,
                    new com.fasterxml.jackson.core.type.TypeReference<List<Question>>() {}
            );

            // 🔹 Find correct question by ID
            Question selectedQuestion = questions.stream()
                    .filter(q -> q.getId() == request.getQuestionId())
                    .findFirst()
                    .orElse(null);

            if (selectedQuestion == null) {
                return new SubmissionResponse("ERROR", "Question not found.");
            }

            String inputUsed = selectedQuestion.getInput();  // ✅ dynamic input

            Map<String, Object> judgeRequest = new HashMap<>();
            judgeRequest.put("source_code", request.getCode());
            judgeRequest.put("language_id", request.getLanguageId());
            judgeRequest.put("stdin", inputUsed);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity =
                    new HttpEntity<>(judgeRequest, headers);

            ResponseEntity<Map> response =
                    restTemplate.postForEntity(JUDGE0_URL, entity, Map.class);

            if (response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                Object stdout = body.get("stdout");
                Object stderr = body.get("stderr");
                Object compileOutput = body.get("compile_output");

                if (stderr != null || compileOutput != null) {
                    return new SubmissionResponse(
                            "ERROR",
                            "Input:\n" + inputUsed + "\n\nResult:\nError. Try again."
                    );
                }

                if (stdout != null) {
                    String output = stdout.toString().trim();
                    return new SubmissionResponse(
                            "SUCCESS",
                            "Input:\n" + inputUsed + "\n\nOutput:\n" + output
                    );
                }
            }

            return new SubmissionResponse(
                    "ERROR",
                    "Input:\n" + inputUsed + "\n\nResult:\nError. Try again."
            );

        } catch (Exception e) {
            return new SubmissionResponse("ERROR", "Server error.");
        }
    }}