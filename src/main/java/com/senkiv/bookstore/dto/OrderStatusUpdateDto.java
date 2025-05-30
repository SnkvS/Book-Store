package com.senkiv.bookstore.dto;

import com.senkiv.bookstore.model.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record OrderStatusUpdateDto(
        @NotNull
        OrderStatus orderStatus
) {
}
