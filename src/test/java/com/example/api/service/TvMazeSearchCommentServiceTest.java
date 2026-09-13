package com.example.api.service;

import com.example.api.client.TvMazeClient;
import com.example.api.dto.ShowCommentRequest;
import com.example.api.dto.StatusResponse;
import com.example.api.model.ShowCacheDocument;
import com.example.api.model.ShowCommentDocument;
import com.example.api.repository.ShowCacheRepository;
import com.example.api.repository.ShowCommentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TvMazeSearchCommentServiceTest {

    @Mock
    private TvMazeClient tvMazeClient;

    @Mock
    private ShowCacheRepository showCacheRepository;

    @Mock
    private ShowCommentRepository showCommentRepository;

    @Test
    void shouldSaveCommentAndReturnStatus() {
        TvMazeSearchService service = new TvMazeSearchService(
                tvMazeClient,
                showCacheRepository,
                showCommentRepository,
                new ObjectMapper()
        );

        ShowCommentRequest request = new ShowCommentRequest(139L, "Muy buena", 5);

        StatusResponse response = service.saveComment(request);

        assertThat(response.status()).isEqualTo("saved");

        ArgumentCaptor<ShowCommentDocument> captor = ArgumentCaptor.forClass(ShowCommentDocument.class);
        verify(showCommentRepository).save(captor.capture());
        assertThat(captor.getValue().getShowId()).isEqualTo(139L);
        assertThat(captor.getValue().getComment()).isEqualTo("Muy buena");
        assertThat(captor.getValue().getRating()).isEqualTo(5);
    }
}