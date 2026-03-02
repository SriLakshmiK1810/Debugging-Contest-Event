package com.debugarena.backend.model;

import java.util.Map;

public class Question {

    private int id;
    private String title;
    private String description;
    private String input;
    private String expectedOutput;
    private Map<String, String> languages;

    public Question() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getInput() { return input; }          // 👈 ADD
    public void setInput(String input) { this.input = input; }  // 👈 ADD

    public String getExpectedOutput() { return expectedOutput; }
    public void setExpectedOutput(String expectedOutput) { this.expectedOutput = expectedOutput; }

    public Map<String, String> getLanguages() { return languages; }
    public void setLanguages(Map<String, String> languages) { this.languages = languages; }
}