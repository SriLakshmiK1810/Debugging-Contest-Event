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

        return mapper.readValue(inputStream,
                new com.fasterxml.jackson.core.type.TypeReference<List<Question>>() {});
    }

    @PostMapping("/submit")
    public SubmissionResponse submitCode(@RequestBody SubmissionRequest request) throws Exception {

        ClassPathResource resource = new ClassPathResource("questions.json");
        ObjectMapper mapper = new ObjectMapper();

        List<Map<String, Object>> questions =
                mapper.readValue(resource.getInputStream(), List.class);

        for (Map<String, Object> question : questions) {

            int id = (int) question.get("id");

            if (id == request.getQuestionId()) {

                String expectedOutput = (String) question.get("expectedOutput");
                String input = (String) question.get("input");

                Map<String, Object> judgeRequest = new HashMap<>();
                judgeRequest.put("source_code", request.getCode());
                judgeRequest.put("language_id", request.getLanguageId()); // ✅ ADD THIS
                judgeRequest.put("stdin", input);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<Map<String, Object>> entity =
                        new HttpEntity<>(judgeRequest, headers);

                ResponseEntity<Map> response =
                        restTemplate.postForEntity(JUDGE0_URL, entity, Map.class);

                String actualOutput = null;

                if (response.getBody() != null) {
                    actualOutput = (String) response.getBody().get("stdout");
                }

                if (actualOutput != null &&
                        actualOutput.trim().equals(expectedOutput.trim())) {

                    return new SubmissionResponse("Correct",
                            "All test cases passed");
                } else {
                    return new SubmissionResponse("Wrong",
                            "Output mismatch");
                }
            }
        }

        return new SubmissionResponse("Error", "Question not found");
    }
}