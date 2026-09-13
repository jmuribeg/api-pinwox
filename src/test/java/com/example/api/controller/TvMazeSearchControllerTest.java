package com.example.api.controller;

import com.example.api.dto.TvMazeShowResponse;
import com.example.api.dto.ShowCommentRequest;
import com.example.api.dto.StatusResponse;
import com.example.api.service.TvMazeSearchService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TvMazeSearchController.class)
class TvMazeSearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

    @MockBean
    private TvMazeSearchService tvMazeSearchService;

    @Test
    void shouldReturnShowsFromSearchQuery() throws Exception {
        when(tvMazeSearchService.searchShows("girls")).thenReturn(List.of(
                new TvMazeShowResponse(
                        139L,
                        "Girls",
                        "HBO",
                        "Summary",
                        List.of("Drama", "Comedy")
                )
        ));

        mockMvc.perform(get("/api/v1/search")
                        .param("search_query", "girls")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(139))
                .andExpect(jsonPath("$[0].name").value("Girls"))
                .andExpect(jsonPath("$[0].channel").value("HBO"))
                .andExpect(jsonPath("$[0].summary").value("Summary"))
                .andExpect(jsonPath("$[0].genres[0]").value("Drama"));

        verify(tvMazeSearchService).searchShows("girls");
    }

        @Test
        void shouldReturnFullShowById() throws Exception {
                JsonNode payload = objectMapper.readTree("""
                                {
                                  "id": 139,
                                  "name": "Girls",
                                  "language": "English"
                                }
                                """);

                when(tvMazeSearchService.getShowById(139L)).thenReturn(payload);

                mockMvc.perform(get("/api/v1/show/139")
                                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(139))
                                .andExpect(jsonPath("$.name").value("Girls"))
                                .andExpect(jsonPath("$.language").value("English"));

                verify(tvMazeSearchService).getShowById(139L);
        }

        @Test
        void shouldSaveCommentAndReturnStatus() throws Exception {
                ShowCommentRequest request = new ShowCommentRequest(139L, "Muy buena", 5);
                when(tvMazeSearchService.saveComment(request)).thenReturn(new StatusResponse("saved"));

                mockMvc.perform(post("/api/v1/comments")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value("saved"));

                verify(tvMazeSearchService).saveComment(request);
        }
}