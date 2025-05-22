package com.senkiv.bookstore.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryRequestDto(
        @NotBlank
        String name,
        @NotBlank
        @Size(max = 255)
        String description
) {
}
