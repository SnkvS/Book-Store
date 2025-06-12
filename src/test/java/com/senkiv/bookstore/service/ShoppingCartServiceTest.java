package com.senkiv.bookstore.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.senkiv.bookstore.dto.CartItemRequestDto;
import com.senkiv.bookstore.dto.CartItemResponseDto;
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
import com.senkiv.bookstore.service.impl.ShoppingCartServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ShoppingCartServiceTest {
    private static final Long USER_ID = 1L;
    private static final Long BOOK_ID = 1L;
    private static final Long CART_ITEM_ID = 1L;
    private static final int QUANTITY = 2;
    @Mock
    private ShoppingCartRepository shoppingCartRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private ShoppingCartMapper shoppingCartMapper;
    @Mock
    private CartItemMapper cartItemMapper;
    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartService;
    private ShoppingCart shoppingCart;
    private User user;
    private Book book;
    private CartItem cartItem;
    private ShoppingCartResponseDto shoppingCartResponseDto;
    private CartItemResponseDto cartItemResponseDto;
    private CartItemRequestDto cartItemRequestDto;
    private CartItemUpdateQuantityDto cartItemUpdateQuantityDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(USER_ID);
        book = new Book();
        book.setId(BOOK_ID);
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setIsbn("978-3-16-148410-0");
        book.setPrice(new BigDecimal("29.99"));
        cartItem = new CartItem();
        cartItem.setId(CART_ITEM_ID);
        cartItem.setBook(book);
        cartItem.setQuantity(QUANTITY);
        shoppingCart = new ShoppingCart();
        shoppingCart.setId(USER_ID);
        shoppingCart.setUser(user);
        Set<CartItem> cartItems = new HashSet<>();
        cartItems.add(cartItem);
        shoppingCart.setCartItems(cartItems);
        cartItem.setShoppingCart(shoppingCart);
        cartItemResponseDto = new CartItemResponseDto(CART_ITEM_ID, BOOK_ID, QUANTITY);
        Set<CartItemResponseDto> cartItemResponseDtos = new HashSet<>();
        cartItemResponseDtos.add(cartItemResponseDto);
        shoppingCartResponseDto = new ShoppingCartResponseDto(USER_ID, USER_ID,
                cartItemResponseDtos);
        cartItemRequestDto = new CartItemRequestDto(BOOK_ID, QUANTITY);
        cartItemUpdateQuantityDto = new CartItemUpdateQuantityDto(QUANTITY + 1);
    }

    @Test
    @DisplayName("Get user's cart with valid user ID")
    void getUsersCart_ValidUserId_ReturnsShoppingCartResponseDto() {
        when(shoppingCartRepository.findShoppingCartByUserId(USER_ID)).thenReturn(
                Optional.of(shoppingCart));
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(shoppingCartResponseDto);
        ShoppingCartResponseDto result = shoppingCartService.getUsersCart(USER_ID);
        assertThat(result).isNotNull();
        assertEquals(shoppingCartResponseDto, result);
        verify(shoppingCartRepository).findShoppingCartByUserId(USER_ID);
        verify(shoppingCartMapper).toDto(shoppingCart);
    }

    @Test
    @DisplayName("Get user's cart with invalid user ID throws EntityNotFoundException")
    void getUsersCart_InvalidUserId_ThrowsEntityNotFoundException() {
        when(shoppingCartRepository.findShoppingCartByUserId(999L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> shoppingCartService.getUsersCart(999L));
        verify(shoppingCartRepository).findShoppingCartByUserId(999L);
    }

    @Test
    @DisplayName("Add book to cart with valid data")
    void addBookToCart_ValidData_ReturnsShoppingCartResponseDto() {
        when(shoppingCartRepository.findShoppingCartByUserId(USER_ID)).thenReturn(
                Optional.of(shoppingCart));
        when(bookRepository.findById(BOOK_ID)).thenReturn(Optional.of(book));
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(shoppingCartResponseDto);
        ShoppingCartResponseDto result = shoppingCartService.addBookToCart(USER_ID,
                cartItemRequestDto);
        assertThat(result).isNotNull();
        assertEquals(shoppingCartResponseDto, result);
        verify(shoppingCartRepository).findShoppingCartByUserId(USER_ID);
        verify(bookRepository).findById(BOOK_ID);
        verify(shoppingCartMapper).toDto(shoppingCart);
    }

    @Test
    @DisplayName("Add book to cart with invalid book ID throws EntityNotFoundException")
    void addBookToCart_InvalidBookId_ThrowsEntityNotFoundException() {
        when(shoppingCartRepository.findShoppingCartByUserId(USER_ID)).thenReturn(
                Optional.of(shoppingCart));
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());
        CartItemRequestDto invalidBookDto = new CartItemRequestDto(999L, QUANTITY);
        assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.addBookToCart(USER_ID, invalidBookDto));
        verify(shoppingCartRepository).findShoppingCartByUserId(USER_ID);
        verify(bookRepository).findById(999L);
    }

    @Test
    @DisplayName("Add new book to cart creates new cart item")
    void addBookToCart_NewBook_CreatesNewCartItem() {
        CartItem newCartItem = new CartItem();
        Book newBook = new Book();
        newBook.setId(2L);
        newCartItem.setBook(newBook);
        newCartItem.setQuantity(QUANTITY);
        when(shoppingCartRepository.findShoppingCartByUserId(USER_ID)).thenReturn(
                Optional.of(shoppingCart));
        when(bookRepository.findById(2L)).thenReturn(Optional.of(newBook));
        CartItemRequestDto newBookDto = new CartItemRequestDto(2L, QUANTITY);
        when(cartItemMapper.toModel(newBookDto)).thenReturn(newCartItem);
        when(cartItemRepository.save(newCartItem)).thenReturn(newCartItem);
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(shoppingCartResponseDto);
        ShoppingCartResponseDto result = shoppingCartService.addBookToCart(USER_ID, newBookDto);
        assertThat(result).isNotNull();
        assertEquals(shoppingCartResponseDto, result);
        verify(shoppingCartRepository).findShoppingCartByUserId(USER_ID);
        verify(bookRepository).findById(2L);
        verify(cartItemMapper).toModel(newBookDto);
        verify(cartItemRepository).save(newCartItem);
        verify(shoppingCartMapper).toDto(shoppingCart);
    }

    @Test
    @DisplayName("Update quantity with valid data")
    void updateQuantity_ValidData_ReturnsShoppingCartResponseDto() {
        when(shoppingCartRepository.findShoppingCartByUserId(USER_ID)).thenReturn(
                Optional.of(shoppingCart));
        when(cartItemRepository.findCartItemByShoppingCartId(USER_ID)).thenReturn(Set.of(cartItem));
        when(cartItemMapper.update(cartItemUpdateQuantityDto, cartItem)).thenReturn(cartItem);
        when(cartItemRepository.save(cartItem)).thenReturn(cartItem);
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(shoppingCartResponseDto);
        ShoppingCartResponseDto result = shoppingCartService.updateQuantity(
                USER_ID, CART_ITEM_ID, cartItemUpdateQuantityDto);
        assertThat(result).isNotNull();
        assertEquals(shoppingCartResponseDto, result);
        verify(shoppingCartRepository).findShoppingCartByUserId(USER_ID);
        verify(cartItemRepository).findCartItemByShoppingCartId(USER_ID);
        verify(cartItemMapper).update(cartItemUpdateQuantityDto, cartItem);
        verify(cartItemRepository).save(cartItem);
        verify(shoppingCartMapper).toDto(shoppingCart);
    }

    @Test
    @DisplayName("Update quantity with invalid cart item ID throws EntityNotFoundException")
    void updateQuantity_InvalidCartItemId_ThrowsEntityNotFoundException() {
        when(shoppingCartRepository.findShoppingCartByUserId(USER_ID)).thenReturn(
                Optional.of(shoppingCart));
        when(cartItemRepository.findCartItemByShoppingCartId(USER_ID)).thenReturn(Set.of(cartItem));
        assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.updateQuantity(USER_ID, 999L, cartItemUpdateQuantityDto));
        verify(shoppingCartRepository).findShoppingCartByUserId(USER_ID);
        verify(cartItemRepository).findCartItemByShoppingCartId(USER_ID);
    }

    @Test
    @DisplayName("Delete cart item with valid data")
    void deleteCartItem_ValidData_RemovesCartItem() {
        when(shoppingCartRepository.findShoppingCartByUserId(USER_ID)).thenReturn(
                Optional.of(shoppingCart));
        when(cartItemRepository.findCartItemByShoppingCartId(USER_ID)).thenReturn(Set.of(cartItem));
        shoppingCartService.deleteCartItem(USER_ID, CART_ITEM_ID);
        verify(shoppingCartRepository).findShoppingCartByUserId(USER_ID);
        verify(cartItemRepository).findCartItemByShoppingCartId(USER_ID);
        assertThat(shoppingCart.getCartItems()).isEmpty();
    }

    @Test
    @DisplayName("Delete cart item with invalid cart item ID throws EntityNotFoundException")
    void deleteCartItem_InvalidCartItemId_ThrowsEntityNotFoundException() {
        when(shoppingCartRepository.findShoppingCartByUserId(USER_ID)).thenReturn(
                Optional.of(shoppingCart));
        when(cartItemRepository.findCartItemByShoppingCartId(USER_ID)).thenReturn(Set.of(cartItem));
        assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.deleteCartItem(USER_ID, 999L));
        verify(shoppingCartRepository).findShoppingCartByUserId(USER_ID);
        verify(cartItemRepository).findCartItemByShoppingCartId(USER_ID);
    }

    @Test
    @DisplayName("Create user's cart with valid user")
    void createUsersCart_ValidUser_CreatesShoppingCart() {
        when(shoppingCartRepository.save(any(ShoppingCart.class))).thenReturn(shoppingCart);
        assertDoesNotThrow(() -> shoppingCartService.createUsersCart(user));
        verify(shoppingCartRepository).save(any(ShoppingCart.class));
    }
}
