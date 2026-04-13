package com.example.EmployeeApiCrud.controller;

import com.example.EmployeeApiCrud.dto.APIResponse;
import com.example.EmployeeApiCrud.dto.ChatRequest;
import com.example.EmployeeApiCrud.dto.ChatResponse;
import com.example.EmployeeApiCrud.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    /**
     * POST /chat/ask
     * Body: { "question": "Kaun se employees resign kar sakte hain?" }
     *
     * Tumhare employee DB se data lekar Ollama LLM se answer generate karta hai.
     * Koi API key nahi chahiye — sirf Ollama locally chalna chahiye.
     */
    @PostMapping("/ask")
    public ResponseEntity<APIResponse> ask(@RequestBody ChatRequest request) {
        if (request.getQuestion() == null || request.getQuestion().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                .body(new APIResponse(400, "Question cannot be empty"));
        }
        ChatResponse response = chatService.ask(request.getQuestion().trim());
        return ResponseEntity.ok(new APIResponse(200, "Answer generated", response));
    }

    /**
     * GET /chat/ask?q=your+question  (browser-friendly shortcut)
     */
    @GetMapping("/ask")
    public ResponseEntity<APIResponse> askGet(@RequestParam String q) {
        if (q == null || q.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                .body(new APIResponse(400, "Question cannot be empty"));
        }
        ChatResponse response = chatService.ask(q.trim());
        return ResponseEntity.ok(new APIResponse(200, "Answer generated", response));
    }
}
