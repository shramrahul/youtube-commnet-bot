package com.ai.commentbot.service;

import com.ai.commentbot.handler.OpenAiChatResponseHandler;
import com.ai.commentbot.model.OpenAiChatResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class OpenAiService {
    private static final String OPENAI_API_KEY = "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";
    private static final String OPENAI_API_ENDPOINT = "https://api.openai.com/v1/chat/completions";
    private final HttpClient httpClient;

    public OpenAiService(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public OpenAiChatResponse getSemanticForAComment(String prompt) throws IOException, InterruptedException {
        prompt = String.format("tell me if it's a positive, Neutral or negative meaning. message is %s", prompt);
        return getOpenAiResponse(prompt);
    }

    public OpenAiChatResponse generateAReplyText(String prompt) throws IOException, InterruptedException {
        prompt = String.format("Generate a good reply for this comment message. message is %s", prompt);
        return getOpenAiResponse(prompt);
    }

    private OpenAiChatResponse getOpenAiResponse(String prompt) throws IOException, InterruptedException {
        String requestBody = """
                {
                    "model": "gpt-3.5-turbo-0125",
                    "messages": [
                        {
                            "role": "user",
                            "content": "%s"
                        }
                    ],
                    "temperature": 1,
                    "max_tokens": 512,
                    "top_p": 1,
                    "temperature": 0.0,
                    "frequency_penalty": 0,
                    "presence_penalty": 0
                }
                """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(OPENAI_API_ENDPOINT))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + OPENAI_API_KEY)
                .POST(HttpRequest.BodyPublishers.ofString(String.format(requestBody, prompt)))
                .build();

        HttpResponse<OpenAiChatResponse> response = httpClient.send(request, new OpenAiChatResponseHandler());
        return response.body();
    }
}
