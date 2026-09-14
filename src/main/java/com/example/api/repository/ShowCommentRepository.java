package com.example.api.repository;

import com.example.api.model.ShowCommentDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;
import java.util.List;

public interface ShowCommentRepository extends MongoRepository<ShowCommentDocument, String> {
    List<ShowCommentDocument> findByShowId(Long showId);
    List<ShowCommentDocument> findByShowIdIn(Collection<Long> showIds);
}