package com.senkiv.bookstore.service.impl;

import com.senkiv.bookstore.dto.CartItemRequestDto;
import com.senkiv.bookstore.dto.CartItemUpdateQuantityDto;
import com.senkiv.bookstore.dto.ShoppingCartResponseDto;
import com.senkiv.bookstore.mapper.CartItemMapper;
import com.senkiv.bookstore.mapper.ShoppingCartMapper;
import com.senkiv.bookstore.model.Book;
import com.senkiv.bookstore.model.CartItem;
import com.senkiv.bookstore.model.ShoppingCart;
import com.senkiv.bookstore.model.User;
import com.senkiv.bookstore.repository.BookRepository;
import com.senkiv.bookstore.repository.CartItemRepository;
import com.senkiv.bookstore.repository.ShoppingCartRepository;
import com.senkiv.bookstore.service.ShoppingCartService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    public static final String SHOPPING_CART_INIT_ERROR_MSG =
            "Something went wrong during initialization of shopping cart for user with id -> %s";
    public static final String CANNOT_ADD_INVALID_BOOK_ID_MESSAGE =
            "Cannot add to cart book with invalid id -> %s.";
    public static final String CART_ITEM_NOT_FOUND_MESSAGE =
            "There is no item in cart with id -> %s";
    private final ShoppingCartRepository shoppingCartRepository;
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final CartItemMapper cartItemMapper;

    @Override
    public ShoppingCartResponseDto getUsersCart(Long userId) {
        ShoppingCart usersShoppingCart = getUsersShoppingCart(userId);
        return shoppingCartMapper.toDto(usersShoppingCart);
    }

    @Override
    public ShoppingCartResponseDto addBookToCart(Long userId, CartItemRequestDto dto) {
        ShoppingCart usersShoppingCart = getUsersShoppingCart(userId);
        Long bookId = dto.bookId();
        Book bookDb = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException(CANNOT_ADD_INVALID_BOOK_ID_MESSAGE));
        usersShoppingCart.getCartItems().stream()
                .filter(cartItem -> cartItem.getBook().equals(bookDb)).findFirst()
                .ifPresentOrElse(item -> item.setQuantity(item.getQuantity() + dto.quantity()),
                        () -> {
                            CartItem model = cartItemMapper.toModel(dto);
                            model.setShoppingCart(usersShoppingCart);
                            model = cartItemRepository.save(model);
                            usersShoppingCart.getCartItems().add(model);

                        });
        return shoppingCartMapper.toDto(usersShoppingCart);
    }

    @Override
    public ShoppingCartResponseDto updateQuantity(
            Long userId,
            Long cartItemId,
            CartItemUpdateQuantityDto dto) {
        ShoppingCart usersShoppingCart = getUsersShoppingCart(userId);
        CartItem itemToUpdate = getCartItemByIdAndShoppingCartId(cartItemId,
                usersShoppingCart.getId());
        cartItemMapper.update(dto, itemToUpdate);
        cartItemRepository.save(itemToUpdate);
        return shoppingCartMapper.toDto(usersShoppingCart);
    }

    @Override
    public void deleteCartItem(Long userId, Long cartItemId) {
        ShoppingCart usersShoppingCart = getUsersShoppingCart(userId);
        CartItem cartItemByIdAndShoppingCartId = getCartItemByIdAndShoppingCartId(cartItemId,
                usersShoppingCart.getId());
        usersShoppingCart.getCartItems().remove(cartItemByIdAndShoppingCartId);
    }

    @Override
    public void createUsersCart(User user) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        shoppingCartRepository.save(shoppingCart);
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
                .orElseThrow(() -> new EntityNotFoundException(
                        CART_ITEM_NOT_FOUND_MESSAGE.formatted(cartItemId)));
    }
}
