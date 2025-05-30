package com.senkiv.bookstore.dto;

import com.senkiv.bookstore.model.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

public record OrderResponseDto(
        Long id,
        OrderStatus status,
        BigDecimal total,
        LocalDateTime orderDate,
        String shippingAddress,
        Set<OrderItemResponseDto> orderItems) {
}
