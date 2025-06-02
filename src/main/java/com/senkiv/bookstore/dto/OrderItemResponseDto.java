package com.senkiv.bookstore.dto;

import java.math.BigDecimal;

public record OrderItemResponseDto(
        Long id,
        Long bookId,
        int quantity,
        BigDecimal price
) {
}
