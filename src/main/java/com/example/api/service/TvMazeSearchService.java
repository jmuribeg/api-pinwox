package com.example.api.service;

import com.example.api.client.TvMazeClient;
import com.example.api.dto.ShowCommentRequest;
import com.example.api.dto.ShowCommentResponse;
import com.example.api.dto.StatusResponse;
import com.example.api.dto.TvMazeShowResponse;
import com.example.api.model.ShowCacheDocument;
import com.example.api.model.ShowCommentDocument;
import com.example.api.repository.ShowCacheRepository;
import com.example.api.repository.ShowCommentRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class TvMazeSearchService {

    private final TvMazeClient tvMazeClient;
    private final ShowCacheRepository showCacheRepository;
    private final ShowCommentRepository showCommentRepository;
    private final ObjectMapper objectMapper;

    public TvMazeSearchService(TvMazeClient tvMazeClient,
                               ShowCacheRepository showCacheRepository,
                               ShowCommentRepository showCommentRepository,
                               ObjectMapper objectMapper) {
        this.tvMazeClient = tvMazeClient;
        this.showCacheRepository = showCacheRepository;
        this.showCommentRepository = showCommentRepository;
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

        // Convert payload to list of shows
        List<TvMazeShowResponse> shows = StreamSupport.stream(payload.spliterator(), false)
                .map(node -> node.path("show"))
                .map(this::toResponse)
                .toList();

        // Extract show IDs
        Collection<Long> showIds = shows.stream()
                .map(TvMazeShowResponse::id)
                .filter(id -> id != null && id > 0)
                .toList();

        if (showIds.isEmpty()) {
            return shows;
        }

        // Fetch all comments for these show IDs
        List<ShowCommentDocument> allComments = showCommentRepository.findByShowIdIn(showIds);

        // Group comments by showId
        Map<Long, List<ShowCommentResponse>> commentsByShowId = allComments.stream()
                .collect(Collectors.groupingBy(
                        ShowCommentDocument::getShowId,
                        Collectors.mapping(
                                doc -> new ShowCommentResponse(doc.getComment(), doc.getRating()),
                                Collectors.toList()
                        )
                ));

        // Augment shows with comments
        return shows.stream()
                .map(show -> new TvMazeShowResponse(
                        show.id(),
                        show.name(),
                        show.channel(),
                        show.summary(),
                        show.genres(),
                        commentsByShowId.getOrDefault(show.id(), List.of())
                ))
                .toList();
    }

    public JsonNode getShowById(Long showId) {
        if (showId == null || showId <= 0) {
            throw new IllegalArgumentException("show_id must be greater than zero");
        }

        Optional<ShowCacheDocument> cachedShow = showCacheRepository.findById(showId);
        if (cachedShow.isPresent()) {
            return appendComments(readJson(cachedShow.get().getPayloadJson()), showId);
        }

        JsonNode payload = tvMazeClient.getShowById(showId);
        showCacheRepository.save(new ShowCacheDocument(showId, writeJson(payload), Instant.now()));
        return appendComments(payload, showId);
    }

    public StatusResponse saveComment(ShowCommentRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request is required");
        }
        if (request.showId() == null || request.showId() <= 0) {
            throw new IllegalArgumentException("show_id must be greater than zero");
        }

        showCommentRepository.save(new ShowCommentDocument(
                null,
                request.showId(),
                request.comment().trim(),
                request.rating(),
                Instant.now()
        ));

        return new StatusResponse("saved");
    }

    private TvMazeShowResponse toResponse(JsonNode show) {
        return new TvMazeShowResponse(
                longValue(show.path("id")),
                textValue(show.path("name")),
                resolveChannel(show),
                textValue(show.path("summary")),
                toGenres(show.path("genres")),
                List.of()  // Comments will be added during search enrichment
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

    private JsonNode appendComments(JsonNode showPayload, Long showId) {
        ObjectNode response = (showPayload != null && showPayload.isObject())
                ? ((ObjectNode) showPayload).deepCopy()
                : objectMapper.createObjectNode();

        if (!response.has("id") && showId != null) {
            response.put("id", showId);
        }

        List<ShowCommentDocument> comments = showCommentRepository.findByShowId(showId);
        ArrayNode commentsArray = objectMapper.createArrayNode();

        if (comments != null) {
            for (ShowCommentDocument comment : comments) {
                ObjectNode commentNode = objectMapper.createObjectNode();
                commentNode.put("comment", comment.getComment());
                commentNode.put("rating", comment.getRating());
                commentsArray.add(commentNode);
            }
        }

        response.set("comments", commentsArray);
        return response;
    }
}