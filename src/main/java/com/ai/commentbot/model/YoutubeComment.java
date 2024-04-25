package com.ai.commentbot.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.api.client.util.DateTime;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class YoutubeComment {
    private String id;
    private String text;
    private String authorDisplayName;
    private String authorChannelUrl;

/*    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private DateTime publishedAt;*/
    private String publishedAt;

    private int likeCount;
    private String videoId;
    private String semantic;
    private String botRepliedText;
    private boolean botReplied;
    @JsonProperty("id")
    public String getId() {
        return id;
    }

}
