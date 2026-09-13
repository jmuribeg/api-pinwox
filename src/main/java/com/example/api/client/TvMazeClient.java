package com.example.api.client;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class TvMazeClient {

    private final RestClient tvMazeRestClient;

    public TvMazeClient(RestClient tvMazeRestClient) {
        this.tvMazeRestClient = tvMazeRestClient;
    }

    public JsonNode getShowById(Long showId) {
        return tvMazeRestClient.get()
                .uri("/shows/{showId}", Map.of("showId", showId))
                .retrieve()
                .body(JsonNode.class);
    }

    public JsonNode searchShows(String searchQuery) {
        return tvMazeRestClient.get()
                .uri("/search/shows?q={query}", Map.of("query", searchQuery))
                .retrieve()
                .body(JsonNode.class);
    }
}