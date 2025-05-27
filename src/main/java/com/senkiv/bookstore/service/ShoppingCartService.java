package com.senkiv.bookstore.service;

import com.senkiv.bookstore.dto.CartItemRequestDto;
import com.senkiv.bookstore.dto.CartItemResponseDto;
import com.senkiv.bookstore.dto.CartItemUpdateQuantityDto;
import com.senkiv.bookstore.dto.ShoppingCartResponseDto;

public interface ShoppingCartService {
    ShoppingCartResponseDto getUsersCart(Long userId);

    CartItemResponseDto addBookToCart(Long userId, CartItemRequestDto dto);

    CartItemResponseDto updateQuantity(Long userId, Long cartItemId, CartItemUpdateQuantityDto dto);

    void deleteCartItem(Long userId, Long cartItemId);
}
