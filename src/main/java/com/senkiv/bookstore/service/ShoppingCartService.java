package com.senkiv.bookstore.service;

import com.senkiv.bookstore.dto.CartItemRequestDto;
import com.senkiv.bookstore.dto.CartItemUpdateQuantityDto;
import com.senkiv.bookstore.dto.ShoppingCartResponseDto;
import com.senkiv.bookstore.model.ShoppingCart;
import com.senkiv.bookstore.model.User;

public interface ShoppingCartService {
    ShoppingCartResponseDto getUsersCart(Long userId);

    ShoppingCartResponseDto addBookToCart(Long userId, CartItemRequestDto dto);

    ShoppingCartResponseDto updateQuantity(Long userId, Long cartItemId,
            CartItemUpdateQuantityDto dto);

    void deleteCartItem(Long userId, Long cartItemId);

    void createUsersCart(User user);

    static boolean isCartEmpty(ShoppingCart shoppingCart) {
        return shoppingCart.getCartItems().isEmpty();
    }
}
