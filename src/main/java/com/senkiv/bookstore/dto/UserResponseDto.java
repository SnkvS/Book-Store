package com.senkiv.bookstore.dto;

public record UserResponseDto(
        Long id,
        String email,
        String password,
        String firstName,
        String lastName,
        String shippingAddress
) {
}
