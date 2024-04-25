package com.ai.commentbot.handler;

import com.ai.commentbot.model.OpenAiChatResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.http.HttpResponse;

import static java.nio.charset.StandardCharsets.UTF_8;

public class OpenAiChatResponseHandler implements HttpResponse.BodyHandler<OpenAiChatResponse> {
    @Override
    public HttpResponse.BodySubscriber<OpenAiChatResponse> apply(HttpResponse.ResponseInfo responseInfo) {
        return HttpResponse.BodySubscribers.mapping(
                HttpResponse.BodySubscribers.ofString(UTF_8),
                responseBody -> {
                    try {
                        return new ObjectMapper().readValue(responseBody, OpenAiChatResponse.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
    }
}
