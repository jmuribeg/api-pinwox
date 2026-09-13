package com.example.api.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("show_comments")
public class ShowCommentDocument {

    @Id
    private String id;
    private Long showId;
    private String comment;
    private Integer rating;
    private Instant createdAt;

    public ShowCommentDocument() {
    }

    public ShowCommentDocument(String id, Long showId, String comment, Integer rating, Instant createdAt) {
        this.id = id;
        this.showId = showId;
        this.comment = comment;
        this.rating = rating;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public Long getShowId() {
        return showId;
    }

    public String getComment() {
        return comment;
    }

    public Integer getRating() {
        return rating;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}