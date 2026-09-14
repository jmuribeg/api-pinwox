package com.example.api.service;

import com.example.api.client.TvMazeClient;
import com.example.api.model.ShowCacheDocument;
import com.example.api.model.ShowCommentDocument;
import com.example.api.repository.ShowCacheRepository;
import com.example.api.repository.ShowCommentRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TvMazeSearchServiceTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private TvMazeClient tvMazeClient;

    @Mock
    private ShowCacheRepository showCacheRepository;

    @Mock
    private ShowCommentRepository showCommentRepository;

    private TvMazeSearchService tvMazeSearchService;

    @BeforeEach
    void setUp() {
        tvMazeSearchService = new TvMazeSearchService(
                tvMazeClient,
                showCacheRepository,
                showCommentRepository,
                objectMapper
        );
    }

    @Test
    void shouldReturnCachedShowWithoutCallingApi() throws Exception {
        when(showCacheRepository.findById(139L)).thenReturn(Optional.of(
                new ShowCacheDocument(139L, """
                        {"id":139,"name":"Girls","language":"English"}
                        """, Instant.now())
        ));
        when(showCommentRepository.findByShowId(139L)).thenReturn(List.of(
            new ShowCommentDocument("1", 139L, "Excelente", 5, Instant.now())
        ));

        JsonNode result = tvMazeSearchService.getShowById(139L);

        assertThat(result.get("name").asText()).isEqualTo("Girls");
        assertThat(result.get("comments").isArray()).isTrue();
        assertThat(result.get("comments").get(0).get("comment").asText()).isEqualTo("Excelente");
        assertThat(result.get("comments").get(0).get("rating").asInt()).isEqualTo(5);
        verify(tvMazeClient, never()).getShowById(139L);
    }

    @Test
    void shouldCallApiAndCacheShowWhenMissing() throws Exception {
        JsonNode payload = objectMapper.readTree("""
                {"id":139,"name":"Girls","language":"English"}
                """);
        when(showCacheRepository.findById(139L)).thenReturn(Optional.empty());
        when(tvMazeClient.getShowById(139L)).thenReturn(payload);
        when(showCommentRepository.findByShowId(139L)).thenReturn(List.of(
            new ShowCommentDocument("2", 139L, "Buen show", 4, Instant.now())
        ));

        JsonNode result = tvMazeSearchService.getShowById(139L);

        assertThat(result.get("language").asText()).isEqualTo("English");
        assertThat(result.get("comments").isArray()).isTrue();
        assertThat(result.get("comments").get(0).get("comment").asText()).isEqualTo("Buen show");
        assertThat(result.get("comments").get(0).get("rating").asInt()).isEqualTo(4);
        verify(showCacheRepository).save(org.mockito.ArgumentMatchers.any(ShowCacheDocument.class));
    }
}