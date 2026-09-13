package com.example.api.controller;

import com.example.api.dto.TvMazeShowResponse;
import com.example.api.service.TvMazeSearchService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/v1")
public class TvMazeSearchController {

    private final TvMazeSearchService tvMazeSearchService;

    public TvMazeSearchController(TvMazeSearchService tvMazeSearchService) {
        this.tvMazeSearchService = tvMazeSearchService;
    }

    @GetMapping("/search")
    public List<TvMazeShowResponse> searchShows(@RequestParam("search_query") @NotBlank String searchQuery) {
        return tvMazeSearchService.searchShows(searchQuery);
    }
}