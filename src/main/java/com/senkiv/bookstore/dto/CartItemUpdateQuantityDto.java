package com.senkiv.bookstore.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CartItemUpdateQuantityDto(
        @Positive
        @NotNull
        int quantity
) {
}
