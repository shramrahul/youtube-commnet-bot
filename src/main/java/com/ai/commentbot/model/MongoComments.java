package com.ai.commentbot.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "comment-bot")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MongoComments {
    @Id
    private String videoId;
    private List<YoutubeComment> youtubeComments;
}
