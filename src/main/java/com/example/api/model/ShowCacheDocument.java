package com.example.api.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("show_cache")
public class ShowCacheDocument {

    @Id
    private Long showId;
    private String payloadJson;
    private Instant cachedAt;

    public ShowCacheDocument() {
    }

    public ShowCacheDocument(Long showId, String payloadJson, Instant cachedAt) {
        this.showId = showId;
        this.payloadJson = payloadJson;
        this.cachedAt = cachedAt;
    }

    public Long getShowId() {
        return showId;
    }

    public String getPayloadJson() {
        return payloadJson;
    }

    public Instant getCachedAt() {
        return cachedAt;
    }
}