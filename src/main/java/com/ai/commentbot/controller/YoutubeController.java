package com.ai.commentbot.controller;

import com.ai.commentbot.model.YoutubeComment;
import com.ai.commentbot.model.YoutubeCommentRequest;
import com.ai.commentbot.service.YoutubeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
public class YoutubeController {

    private final YoutubeService youtubeService;

    public YoutubeController(YoutubeService youtubeService) {
        this.youtubeService = youtubeService;
    }

    @PostMapping("/video/comments")
    public List<YoutubeComment> getAndReplyVideoComments(@RequestBody YoutubeCommentRequest request) throws IOException {
        String videoUrl = request.getVideoUrl();
        return youtubeService.getAndReplyVideoComments(videoUrl);
    }

    @GetMapping("/home")
    public String getVideoComments() {
        return "Application registered.";
    }
}
