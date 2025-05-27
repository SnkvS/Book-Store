package com.senkiv.bookstore.controller;

import com.senkiv.bookstore.dto.CartItemRequestDto;
import com.senkiv.bookstore.dto.CartItemResponseDto;
import com.senkiv.bookstore.dto.CartItemUpdateQuantityDto;
import com.senkiv.bookstore.dto.ShoppingCartResponseDto;
import com.senkiv.bookstore.model.User;
import com.senkiv.bookstore.service.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Shopping cart API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/cart")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @PreAuthorize("hasRole('USER')")
    @Operation(description = "Retrieves user`s shopping cart.")
    @GetMapping
    public ShoppingCartResponseDto getCart(@AuthenticationPrincipal UserDetails userDetails) {
        User authenticatedUser = getUserFromDetails(userDetails);
        return shoppingCartService.getUsersCart(authenticatedUser.getId());
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(description = "Adds new book to shopping cart.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CartItemResponseDto addBook(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid CartItemRequestDto dto) {
        User authenticatedUser = getUserFromDetails(userDetails);
        return shoppingCartService.addBookToCart(authenticatedUser.getId(), dto);
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(description = "Updates item that is already in shoping cart.")
    @PutMapping("/{itemId}")
    public CartItemResponseDto updateItemQuantity(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long itemId,
            @RequestBody @Valid CartItemUpdateQuantityDto dto) {
        User authenticatedUser = getUserFromDetails(userDetails);
        return shoppingCartService.updateQuantity(authenticatedUser.getId(), itemId, dto);
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(description = "Deletes item from shopping cart.")
    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long itemId) {
        User authenticatedUser = getUserFromDetails(userDetails);
        shoppingCartService.deleteCartItem(authenticatedUser.getId(), itemId);
    }

    private User getUserFromDetails(UserDetails userDetails) {
        return (User) userDetails;
    }
}
