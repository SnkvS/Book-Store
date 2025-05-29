package com.senkiv.bookstore.dto;

import jakarta.validation.constraints.Size;

public record OrderRequestDto(
        @Size(max = 255, message = "Shipping address must not exceed 255 characters")
        String shippingAddress
) {
}
