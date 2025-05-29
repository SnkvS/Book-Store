package com.senkiv.bookstore.dto;

import com.senkiv.bookstore.model.OrderStatus;
import jakarta.validation.constraints.NotBlank;

public record OrderStatusUpdateDto(
        @NotBlank
        OrderStatus orderStatus
) {
}
