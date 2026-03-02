package com.debugarena.backend.model;

public class SubmissionResponse {

    private String status;
    private String message;

    public SubmissionResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}