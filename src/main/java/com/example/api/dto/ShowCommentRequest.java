package com.example.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ShowCommentRequest(
        @JsonProperty("show_id")
        @NotNull
        @Min(1)
        Long showId,

        @NotBlank
        String comment,

        @NotNull
        @Min(0)
        @Max(5)
        Integer rating
) {
}