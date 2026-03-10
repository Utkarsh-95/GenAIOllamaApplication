package com.poc.springai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ChatController {

    private final ChatClient chatClient;

    private final ChatModel chatModel;

    public ChatController(ChatClient.Builder chatClientBuilder, ChatModel chatModel) {
        this.chatClient = chatClientBuilder.build();
        this.chatModel = chatModel;
    }

    @GetMapping("/healthCheck")
    public String healthCheck() {
        return "OK";
    }

    @GetMapping("/chat")
    public String chat(@RequestParam("prompt") String prompt) {
        return chatClient.prompt(prompt).call().content();
    }

    @GetMapping("/ask")
    public String askModel(@RequestParam("prompt") String prompt) {
        return chatModel.call(prompt);
    }

    @GetMapping("/chatSystemPrompt")
    public String chatSystemPrompt(@RequestParam("message") String message) {
        return chatClient
                .prompt()
                //.advisors(new TokenUsageAuditAdvisor())
                .system("""
                        You are an internal IT helpdesk assistant. Your role is to assist 
                        employees with IT-related issues such as resetting passwords, 
                        unlocking accounts, and answering questions related to IT policies.
                        If a user requests help with anything outside of these 
                        responsibilities, respond politely and inform them that you are 
                        only able to assist with IT support tasks within your defined scope.
                        """)
                .user(message)
                .call().content();
    }
}