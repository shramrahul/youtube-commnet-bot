package com.ai.commentbot.service;

import com.ai.commentbot.model.YoutubeComment;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Comment;
import com.google.api.services.youtube.model.CommentSnippet;
import com.google.api.services.youtube.model.CommentThread;
import com.google.api.services.youtube.model.CommentThreadListResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class YouTubeDataAPIService {

    private final YouTube youTube;

    public YouTubeDataAPIService(YouTube youTube) {
        this.youTube = youTube;
    }

    public List<YoutubeComment> getVideoComments(String videoId) throws IOException {
        List<CommentThread> commentThreads = getVideoCommentThreads(videoId);
        return commentThreads.stream()
                .map(this::mapToYoutubeComment)
                .collect(Collectors.toList());
    }

    public void replyToVideoComment(String replyText, YoutubeComment youtubeComment) throws IOException {
        Comment reply = buildReplyComment(youtubeComment, replyText);
        YouTube.Comments.Insert request = youTube.comments().insert("snippet", reply);
        Comment response = request.execute();
        System.out.println("Reply successfully posted with ID: " + response.getId());
    }

    private YoutubeComment mapToYoutubeComment(CommentThread commentThread) {
        CommentSnippet snippet = commentThread.getSnippet().getTopLevelComment().getSnippet();
        return YoutubeComment.builder()
                .id(commentThread.getId())
                .text(snippet.getTextOriginal())
                .authorDisplayName(snippet.getAuthorDisplayName())
                .authorChannelUrl(snippet.getAuthorChannelUrl())
                .publishedAt(String.valueOf(snippet.getPublishedAt()))
                .likeCount(Math.toIntExact(snippet.getLikeCount()))
                .videoId(snippet.getVideoId())
                .botReplied(Boolean.FALSE)
                .build();
    }

    private Comment buildReplyComment(YoutubeComment youtubeComment, String replyText) {
        String videoId = youtubeComment.getVideoId();
        String commentId = youtubeComment.getId();
        CommentSnippet snippet = new CommentSnippet();
        snippet.setVideoId(videoId);
        snippet.setParentId(commentId);
        snippet.setTextOriginal(replyText);
        Comment reply = new Comment();
        reply.setSnippet(snippet);
        return reply;
    }

    private List<CommentThread> getVideoCommentThreads(String videoId) throws IOException {
        YouTube.CommentThreads.List request = youTube.commentThreads()
                .list("snippet")
                .setVideoId(videoId);
        CommentThreadListResponse response = request.execute();
        return response.getItems();
    }

}
