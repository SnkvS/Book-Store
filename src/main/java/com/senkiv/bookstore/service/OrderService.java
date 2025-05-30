package com.senkiv.bookstore.service;

import com.senkiv.bookstore.dto.OrderItemResponseDto;
import com.senkiv.bookstore.dto.OrderRequestDto;
import com.senkiv.bookstore.dto.OrderResponseDto;
import com.senkiv.bookstore.dto.OrderStatusUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderResponseDto createOrder(Long userId, OrderRequestDto orderRequestDto);

    Page<OrderResponseDto> retrieveHistoryOrders(Pageable pageable, Long id);

    Page<OrderItemResponseDto> getOrderItems(Pageable pageable, Long orderId, Long userId);

    OrderItemResponseDto getSpecificItem(Long itemId, Long orderId, Long userId);

    OrderResponseDto updateStatus(OrderStatusUpdateDto dto, Long orderId);
}
