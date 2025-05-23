package com.senkiv.bookstore.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoryRequestDto(
        @NotBlank
        String name,
        @NotBlank
        String description
) {
}
