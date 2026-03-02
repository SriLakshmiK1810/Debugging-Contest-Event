package com.debugarena.backend;

public class SubmissionRequest {

    private int questionId;
    private String code;
    private int languageId;   // ✅ ADD THIS

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getLanguageId() {   // ✅ ADD THIS
        return languageId;
    }

    public void setLanguageId(int languageId) {  // ✅ ADD THIS
        this.languageId = languageId;
    }
}