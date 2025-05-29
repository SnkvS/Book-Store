package com.senkiv.bookstore.controller;

import com.senkiv.bookstore.dto.OrderItemResponseDto;
import com.senkiv.bookstore.dto.OrderRequestDto;
import com.senkiv.bookstore.dto.OrderResponseDto;
import com.senkiv.bookstore.dto.OrderStatusUpdateDto;
import com.senkiv.bookstore.model.User;
import com.senkiv.bookstore.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Order API")
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @Operation(description = "Retrieves history of user`s orders.")
    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public Page<OrderResponseDto> retrieveOrders(
            @AuthenticationPrincipal UserDetails userDetails,
            Pageable pageable) {
        return orderService.retrieveHistoryOrders(pageable, getUserFromDetails(userDetails));
    }

    @Operation(description = "Places an order with items from user`s shopping cart.")
    @PreAuthorize("hasRole('USER')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponseDto createOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid OrderRequestDto dto) {
        return orderService.createOrder(getUserFromDetails(userDetails), dto);
    }

    @Operation(description = "Retrieves all items from one of user`s orders.")
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{orderId}/items")
    public Page<OrderItemResponseDto> retrieveItemsForSpecificOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long orderId,
            Pageable pageable) {
        return orderService.getOrderItems(pageable, orderId, getUserFromDetails(userDetails));
    }

    @Operation(description = "Retrieves specific item from one of user`s orders")
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{orderId}/items/{itemId}")
    public OrderItemResponseDto retrieveSpecificItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long orderId,
            @PathVariable Long itemId
    ) {
        return orderService.getSpecificItem(itemId, orderId, getUserFromDetails(userDetails));

    }

    @Operation(description = "Updates status of specific order.")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{orderId}")
    public OrderResponseDto updateStatus(
            @PathVariable Long orderId,
            @RequestBody OrderStatusUpdateDto dto
    ) {
        return orderService.updateStatus(dto, orderId);
    }

    private Long getUserFromDetails(UserDetails userDetails) {
        User authenticatedUser = (User) userDetails;
        return authenticatedUser.getId();
    }
}
