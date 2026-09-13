package com.example.api.controller;

import com.example.api.dto.TvMazeShowResponse;
import com.example.api.service.TvMazeSearchService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TvMazeSearchController.class)
class TvMazeSearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

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
}