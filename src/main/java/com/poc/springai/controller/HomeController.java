package com.poc.springai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    private final ChatClient chatClient;

    public HomeController(@Qualifier("chatMemoryChatClient") ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/ask")
    public String chatMemory(Model model, @RequestParam("message") String message) {

        String response = chatClient
                .prompt()
                .user(message)
                .call()
                .content();
        model.addAttribute("response", response);
        model.addAttribute("message", message);
        return "index";
    }
}

