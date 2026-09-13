package com.example.api.service;

import com.example.api.client.TvMazeClient;
import com.example.api.dto.TvMazeShowResponse;
import com.example.api.model.ShowCacheDocument;
import com.example.api.repository.ShowCacheRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
public class TvMazeSearchService {

    private final TvMazeClient tvMazeClient;
    private final ShowCacheRepository showCacheRepository;
    private final ObjectMapper objectMapper;

    public TvMazeSearchService(TvMazeClient tvMazeClient,
                               ShowCacheRepository showCacheRepository,
                               ObjectMapper objectMapper) {
        this.tvMazeClient = tvMazeClient;
        this.showCacheRepository = showCacheRepository;
        this.objectMapper = objectMapper;
    }

    public List<TvMazeShowResponse> searchShows(String searchQuery) {
        if (!StringUtils.hasText(searchQuery)) {
            throw new IllegalArgumentException("search_query is required");
        }

        JsonNode payload = tvMazeClient.searchShows(searchQuery.trim());

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

        Optional<ShowCacheDocument> cachedShow = showCacheRepository.findById(showId);
        if (cachedShow.isPresent()) {
            return readJson(cachedShow.get().getPayloadJson());
        }

        JsonNode payload = tvMazeClient.getShowById(showId);
        showCacheRepository.save(new ShowCacheDocument(showId, writeJson(payload), Instant.now()));
        return payload;
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

    private String writeJson(JsonNode node) {
        try {
            return objectMapper.writeValueAsString(node);
        } catch (Exception ex) {
            throw new IllegalStateException("could not serialize show payload", ex);
        }
    }

    private JsonNode readJson(String json) {
        try {
            return objectMapper.readTree(json);
        } catch (Exception ex) {
            throw new IllegalStateException("could not read cached show payload", ex);
        }
    }
}