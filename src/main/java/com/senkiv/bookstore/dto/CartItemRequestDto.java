package com.senkiv.bookstore.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CartItemRequestDto(
        @Positive
        @NotNull
        Long bookId,
        @Positive
        int quantity
) {
}
