package com.example.EmployeeApiCrud.service;

import com.example.EmployeeApiCrud.dto.ChatResponse;

public interface ChatService {

    /** Answer any HR question using DB data + Ollama LLM */
    ChatResponse ask(String question);
}
