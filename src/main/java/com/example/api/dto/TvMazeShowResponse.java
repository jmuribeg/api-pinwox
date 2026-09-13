package com.example.api.dto;

import java.util.List;

public record TvMazeShowResponse(
        Long id,
        String name,
        String channel,
        String summary,
        List<String> genres
) {
}