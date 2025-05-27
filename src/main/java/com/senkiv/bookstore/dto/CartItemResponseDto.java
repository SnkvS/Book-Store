package com.senkiv.bookstore.dto;

public record CartItemResponseDto(
        Long id,
        Long bookId,
        Integer quantity
) {
}
