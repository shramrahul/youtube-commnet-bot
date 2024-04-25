package com.ai.commentbot.service;

import com.ai.commentbot.model.YoutubeComment;
import com.ai.commentbot.model.OpenAiChatResponse;
import com.ai.commentbot.utils.YoutubeCommentUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class YoutubeService {

    private final YouTubeDataAPIService youTubeDataAPIService;
    private final OpenAiService openAiService;
    private final CommentMongoService commentMongoService;

    public YoutubeService(YouTubeDataAPIService youTubeDataAPIService, OpenAiService openAiService, CommentMongoService commentMongoService) {
        this.youTubeDataAPIService = youTubeDataAPIService;
        this.openAiService = openAiService;
        this.commentMongoService = commentMongoService;
    }

    public List<YoutubeComment> getAndReplyVideoComments(String videoUrl) throws IOException {
        String videoId = YoutubeCommentUtils.extractVideoId(videoUrl);
        List<YoutubeComment> mongoComments = commentMongoService.getAllCommentsByVideoId(videoId);
        Map<String, YoutubeComment> mongoCommentMap = mongoComments
                .stream()
                .collect(Collectors.toMap(YoutubeComment::getId, Function.identity()));

        List<YoutubeComment> youtubeComments = youTubeDataAPIService.getVideoComments(videoId);

        List<YoutubeComment> newComments = youtubeComments.stream()
                .filter(comment -> !mongoCommentMap.containsKey(comment.getId()))
                .peek(this::updateCommentWithSemanticData)
                .toList();

        mongoComments.addAll(newComments);
        List<YoutubeComment> updatedComments = commentMongoService.saveComments(videoId, mongoComments);

        return getAndReplyPositiveComments(videoId, updatedComments);
    }

    private void updateCommentWithSemanticData(YoutubeComment youtubeComment) {
        try {
            OpenAiChatResponse openAiChatResponse = openAiService.getSemanticForAComment(youtubeComment.getText());
            youtubeComment.setSemantic(openAiChatResponse.getChoices().getFirst().getMessage().getContent());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private List<YoutubeComment> getAndReplyPositiveComments(String videoId, List<YoutubeComment> youtubeComments) {
        youtubeComments.stream()
                .filter(this::isNonNegativeAndNotBotReplied)
                .forEach(this::replyToPositiveComments);

        return commentMongoService.saveComments(videoId, youtubeComments);
    }

    private void replyToPositiveComments(YoutubeComment youtubeComment) {
        try {
            String replyText = generateReplyText(youtubeComment.getText());
            youTubeDataAPIService.replyToVideoComment(replyText, youtubeComment);
            youtubeComment.setBotRepliedText(replyText);
            youtubeComment.setBotReplied(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String generateReplyText(String commentText) throws IOException, InterruptedException {
        OpenAiChatResponse openAiChatResponse = openAiService.generateAReplyText(commentText);
        return openAiChatResponse.getChoices().getFirst().getMessage().getContent();
    }

    private boolean isNonNegativeAndNotBotReplied(YoutubeComment youtubeComment) {
        return !youtubeComment.getSemantic().equalsIgnoreCase("negative") && !youtubeComment.isBotReplied();
    }
}
