package com.senkiv.bookstore.dto;

import jakarta.validation.constraints.Positive;

public record CartItemRequestDto(
        @Positive
        Long bookId,
        @Positive
        Integer quantity
) {
}
