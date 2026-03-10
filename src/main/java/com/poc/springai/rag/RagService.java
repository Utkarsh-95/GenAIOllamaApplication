package com.poc.springai.rag;

import jakarta.annotation.PostConstruct;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
public class RagService {

    private final SimpleVectorStore vectorStore;

    private final ChatClient chatClient;

    @Value("classpath:WorkPolicy.txt")
    private Resource policyFile;

    @Value("${app.vectorstore.path}")
    private String vectorstorepath;

    public RagService(SimpleVectorStore vectorStore, ChatClient.Builder builder) {

        this.vectorStore = vectorStore;
        this.chatClient = builder
//                QuestionAnswerAdvisor automates the Retrieval & Augment Steps
                .defaultAdvisors(QuestionAnswerAdvisor.builder(vectorStore).build())
                .build();
    }

    // 1. Ingestion: Load the document into h2
    @PostConstruct
    public void init() {

        File storeFile = new File(vectorstorepath);

        if (storeFile.exists()) {
            vectorStore.load(storeFile);
        }
//        a. Read the file from resources
        TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(policyFile);
        List<Document> docs = tikaDocumentReader.get();

        TextSplitter textSplitter = TokenTextSplitter.builder()
                .withChunkSize(200)
                .withMaxNumChunks(400)
                .build();

        System.out.println("Document size : " + docs.size());
        System.out.println(docs);

//        b. Split text into chunks
//        c. Generate embeddings & add to store
        vectorStore.add(textSplitter.split(docs));
        System.out.println("Successfully added document chunks to the vector store.");

//        d. Save locally (H2 Style persistence)
        vectorStore.save(storeFile);
        System.out.println("Knowledge base successfully loaded with file data.");
    }

    //    2. Retrieval Ask questions based on the stored data
    public String askQuestion(String message) {
        return chatClient
                .prompt()
//                .advisors()
                .user(message)
                .call()
                .content();
    }

    public List<String> semanticSearch(String query) {
        return vectorStore.similaritySearch(SearchRequest.builder()
                        .query(query)
                        .topK(3)
                        .build())
                .stream()
                .map(Document::getText)
                .toList();
    }
}
