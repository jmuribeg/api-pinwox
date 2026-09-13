package com.example.api.service;

import com.example.api.dto.TvMazeShowResponse;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

@Service
public class TvMazeSearchService {

    private final RestClient tvMazeRestClient;

    public TvMazeSearchService(RestClient tvMazeRestClient) {
        this.tvMazeRestClient = tvMazeRestClient;
    }

    public List<TvMazeShowResponse> searchShows(String searchQuery) {
        if (!StringUtils.hasText(searchQuery)) {
            throw new IllegalArgumentException("search_query is required");
        }

        JsonNode payload = tvMazeRestClient.get()
                .uri("/search/shows?q={query}", Map.of("query", searchQuery.trim()))
                .retrieve()
                .body(JsonNode.class);

        if (payload == null || !payload.isArray()) {
            return List.of();
        }

        return StreamSupport.stream(payload.spliterator(), false)
                .map(node -> node.path("show"))
                .map(this::toResponse)
                .toList();
    }

    public JsonNode getShowById(Long showId) {
        if (showId == null || showId <= 0) {
            throw new IllegalArgumentException("show_id must be greater than zero");
        }

        return tvMazeRestClient.get()
                .uri("/shows/{showId}", Map.of("showId", showId))
                .retrieve()
                .body(JsonNode.class);
    }

    private TvMazeShowResponse toResponse(JsonNode show) {
        return new TvMazeShowResponse(
                longValue(show.path("id")),
                textValue(show.path("name")),
                resolveChannel(show),
                textValue(show.path("summary")),
                toGenres(show.path("genres"))
        );
    }

    private String resolveChannel(JsonNode show) {
        String networkName = textValue(show.path("network").path("name"));
        if (StringUtils.hasText(networkName)) {
            return networkName;
        }
        return textValue(show.path("webChannel").path("name"));
    }

    private List<String> toGenres(JsonNode genresNode) {
        if (!genresNode.isArray()) {
            return List.of();
        }

        return StreamSupport.stream(genresNode.spliterator(), false)
                .map(this::textValue)
                .filter(StringUtils::hasText)
                .toList();
    }

    private Long longValue(JsonNode node) {
        if (node == null || node.isNull() || !node.canConvertToLong()) {
            return null;
        }
        return node.longValue();
    }

    private String textValue(JsonNode node) {
        if (node == null || node.isNull()) {
            return null;
        }
        String value = node.asText();
        return StringUtils.hasText(value) ? value : null;
    }
}