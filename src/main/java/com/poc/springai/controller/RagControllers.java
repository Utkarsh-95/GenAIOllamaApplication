package com.poc.springai.controller;

import com.poc.springai.rag.RagService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/rag")
public class RagControllers {

    private final RagService ragService;

    public RagControllers(RagService ragService) {
        this.ragService = ragService;
    }

    @GetMapping("/ask")
    public String ask(@RequestParam("query") String query) {
        return ragService.askQuestion(query);
    }

        @GetMapping("/search")
    public ResponseEntity<List<String>> semanticSearch(@RequestParam("query") String query) {
        return ResponseEntity.ok(ragService.semanticSearch(query));
    }
}
