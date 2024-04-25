package com.ai.commentbot.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenAiChatChoice {
    private Integer index;
    private OpenAiMessage message;
//    private final Delta delta;
@JsonProperty("finish_reason")
private String finishReason;
}
