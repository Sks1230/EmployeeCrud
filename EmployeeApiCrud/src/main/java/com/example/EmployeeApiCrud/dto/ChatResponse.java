package com.example.EmployeeApiCrud.dto;

import java.time.LocalDateTime;

public class ChatResponse {

    private String question;
    private String answer;
    private String model;
    private LocalDateTime respondedAt;

    public ChatResponse() {}

    public ChatResponse(String question, String answer, String model) {
        this.question    = question;
        this.answer      = answer;
        this.model       = model;
        this.respondedAt = LocalDateTime.now();
    }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public LocalDateTime getRespondedAt() { return respondedAt; }
    public void setRespondedAt(LocalDateTime respondedAt) { this.respondedAt = respondedAt; }
}
