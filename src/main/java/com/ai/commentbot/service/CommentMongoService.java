package com.ai.commentbot.service;

import com.ai.commentbot.model.YoutubeComment;
import com.ai.commentbot.model.MongoComments;
import com.ai.commentbot.repository.YoutubeCommentRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class CommentMongoService {

    private final YoutubeCommentRepository commentRepository;

    public CommentMongoService(YoutubeCommentRepository youtubeCommentRepository) {
        this.commentRepository = youtubeCommentRepository;
    }

    public List<YoutubeComment> saveComments(String videoId, List<YoutubeComment> youtubeComments) {
        MongoComments mongoComments = MongoComments.builder()
                .videoId(videoId)
                .youtubeComments(youtubeComments)
                .build();
        commentRepository.save(mongoComments);
        return youtubeComments;
    }

    public List<YoutubeComment> getAllCommentsByVideoId(String videoId) {
        return commentRepository.findAllById(Collections.singleton(videoId))
                .stream()
                .filter(mongoComment -> videoId.equalsIgnoreCase(mongoComment.getVideoId()))
                .findFirst()
                .orElse(new MongoComments(videoId, new ArrayList<>()))
                .getYoutubeComments();
    }

    public List<MongoComments> getAllComments() {
        return commentRepository.findAll();
    }

}
