package com.example.api.repository;

import com.example.api.model.ShowCommentDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ShowCommentRepository extends MongoRepository<ShowCommentDocument, String> {
}