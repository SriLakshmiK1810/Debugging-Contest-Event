package com.debugarena.backend.model;

public class TestCase {

    private String input;
    private String expectedOutput;

    // Default constructor
    public TestCase() {
    }

    // Getter for input
    public String getInput() {
        return input;
    }

    // Setter for input
    public void setInput(String input) {
        this.input = input;
    }

    // Getter for expectedOutput
    public String getExpectedOutput() {
        return expectedOutput;
    }

    // Setter for expectedOutput
    public void setExpectedOutput(String expectedOutput) {
        this.expectedOutput = expectedOutput;
    }
}