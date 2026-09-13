package com.example.api.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateShowRequest(
        @NotBlank String name,
        @NotBlank String genre
) {
}
