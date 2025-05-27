package com.senkiv.bookstore.service.impl;

import com.senkiv.bookstore.dto.CartItemRequestDto;
import com.senkiv.bookstore.dto.CartItemResponseDto;
import com.senkiv.bookstore.dto.CartItemUpdateQuantityDto;
import com.senkiv.bookstore.dto.ShoppingCartResponseDto;
import com.senkiv.bookstore.exception.ShoppingCartException;
import com.senkiv.bookstore.mapper.CartItemMapper;
import com.senkiv.bookstore.mapper.ShoppingCartMapper;
import com.senkiv.bookstore.model.CartItem;
import com.senkiv.bookstore.model.ShoppingCart;
import com.senkiv.bookstore.repository.BookRepository;
import com.senkiv.bookstore.repository.CartItemRepository;
import com.senkiv.bookstore.repository.ShoppingCartRepository;
import com.senkiv.bookstore.service.ShoppingCartService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    public static final String SHOPPING_CART_INIT_ERROR_MSG =
            "Something went wrong during initialization of shopping cart for user with id -> %s";
    public static final String CANNOT_ADD_INVALID_BOOK_ID_MESSAGE =
            "Cannot add to cart book with invalid id -> %s.";
    public static final String CART_ITEM_NOT_FOUND_MESSAGE =
            "There is no item in cart with id -> %s";
    public static final String ITEM_ALREADY_IN_CART_MESSAGE =
            "The item with id %s is already in your cart.";
    private final ShoppingCartRepository shoppingCartRepository;
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final CartItemMapper cartItemMapper;

    @Transactional
    @Override
    public ShoppingCartResponseDto getUsersCart(Long userId) {
        ShoppingCart usersShoppingCart = getUsersShoppingCart(userId);
        return shoppingCartMapper.toDto(usersShoppingCart);
    }

    @Transactional
    @Override
    public CartItemResponseDto addBookToCart(Long userId, CartItemRequestDto dto) {
        ShoppingCart usersShoppingCart = getUsersShoppingCart(userId);
        Long bookId = dto.bookId();
        if (!bookRepository.existsById(bookId)) {
            throw new ShoppingCartException(
                    CANNOT_ADD_INVALID_BOOK_ID_MESSAGE.formatted(bookId));
        }
        if (isInCart(usersShoppingCart, bookId)) {
            throw new ShoppingCartException(
                    ITEM_ALREADY_IN_CART_MESSAGE.formatted(bookId));
        } else {
            CartItem cartItemToAdd = cartItemMapper.toModel(dto);
            cartItemToAdd.setShoppingCart(usersShoppingCart);
            return cartItemMapper.toDto(cartItemRepository.save(cartItemToAdd));
        }
    }

    @Transactional
    @Override
    public CartItemResponseDto updateQuantity(
            Long userId,
            Long cartItemId,
            CartItemUpdateQuantityDto dto) {
        ShoppingCart usersShoppingCart = getUsersShoppingCart(userId);
        CartItem itemToUpdate = getCartItemByIdAndShoppingCartId(cartItemId,
                usersShoppingCart.getId());
        cartItemMapper.update(dto, itemToUpdate);
        return cartItemMapper.toDto(cartItemRepository.save(itemToUpdate));
    }

    @Transactional
    @Override
    public void deleteCartItem(Long userId, Long cartItemId) {
        ShoppingCart usersShoppingCart = getUsersShoppingCart(userId);
        CartItem cartItemByIdAndShoppingCartId = getCartItemByIdAndShoppingCartId(cartItemId,
                usersShoppingCart.getId());
        usersShoppingCart.getCartItems().remove(cartItemByIdAndShoppingCartId);
    }

    private ShoppingCart getUsersShoppingCart(Long userId) {
        return shoppingCartRepository.findShoppingCartByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        SHOPPING_CART_INIT_ERROR_MSG.formatted(userId)));
    }

    private CartItem getCartItemByIdAndShoppingCartId(Long cartItemId, Long shoppingCartId) {
        return cartItemRepository.findCartItemByShoppingCartId(shoppingCartId).stream()
                .filter(cartItem -> cartItem.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new ShoppingCartException(
                        CART_ITEM_NOT_FOUND_MESSAGE.formatted(cartItemId)));
    }

    private boolean isInCart(ShoppingCart cart, Long bookId) {
        return cart.getCartItems().stream()
                .anyMatch(ci -> ci.getBook().getId().equals(bookId));
    }
}
