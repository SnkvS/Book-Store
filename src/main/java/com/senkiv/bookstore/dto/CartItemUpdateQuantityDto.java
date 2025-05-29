package com.senkiv.bookstore.dto;

import jakarta.validation.constraints.Positive;

public record CartItemUpdateQuantityDto(
        @Positive
        int quantity
) {
}
