package com.ai.commentbot.repository;

import com.ai.commentbot.model.MongoComments;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface YoutubeCommentRepository extends MongoRepository<MongoComments, String> {
}
