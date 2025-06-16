package com.senkiv.bookstore.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senkiv.bookstore.dto.CartItemRequestDto;
import com.senkiv.bookstore.dto.CartItemResponseDto;
import com.senkiv.bookstore.dto.CartItemUpdateQuantityDto;
import com.senkiv.bookstore.dto.ShoppingCartResponseDto;
import com.senkiv.bookstore.model.Role;
import com.senkiv.bookstore.model.User;
import com.senkiv.bookstore.service.ShoppingCartService;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ShoppingCartControllerTest {
    private static final Long USER_ID = 1L;
    private static final Long BOOK_ID = 1L;
    private static final Long CART_ITEM_ID = 1L;
    private static final int QUANTITY = 2;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private ShoppingCartService shoppingCartService;
    private ShoppingCartResponseDto shoppingCartResponseDto;
    private CartItemRequestDto cartItemRequestDto;
    private CartItemUpdateQuantityDto cartItemUpdateQuantityDto;

    @BeforeEach
    void setUp() {
        CartItemResponseDto cartItemResponseDto = new CartItemResponseDto(CART_ITEM_ID, BOOK_ID,
                QUANTITY);
        Set<CartItemResponseDto> cartItems = new HashSet<>();
        cartItems.add(cartItemResponseDto);
        shoppingCartResponseDto = new ShoppingCartResponseDto(USER_ID, USER_ID, cartItems);
        cartItemRequestDto = new CartItemRequestDto(BOOK_ID, QUANTITY);
        cartItemUpdateQuantityDto = new CartItemUpdateQuantityDto(QUANTITY + 1);
    }

    @Test
    @DisplayName("Get shopping cart should return cart DTO")
    void getCart_ReturnsShoppingCartResponseDto() throws Exception {
        when(shoppingCartService.getUsersCart(USER_ID)).thenReturn(shoppingCartResponseDto);
        mockMvc.perform(get("/cart")
                        .with(user(getTestUser()))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(shoppingCartResponseDto.id()))
                .andExpect(jsonPath("$.userId").value(shoppingCartResponseDto.userId()))
                .andExpect(jsonPath("$.cartItems[0].id").value(CART_ITEM_ID))
                .andExpect(jsonPath("$.cartItems[0].bookId").value(BOOK_ID))
                .andExpect(jsonPath("$.cartItems[0].quantity").value(QUANTITY));
    }

    @Test
    @DisplayName("Add book to cart should return updated cart DTO")
    void addBook_ReturnsUpdatedShoppingCartResponseDto() throws Exception {
        when(shoppingCartService.addBookToCart(eq(USER_ID), any(CartItemRequestDto.class)))
                .thenReturn(shoppingCartResponseDto);

        mockMvc.perform(post("/cart")
                        .with(user(getTestUser()))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartItemRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(shoppingCartResponseDto.id()))
                .andExpect(jsonPath("$.userId").value(shoppingCartResponseDto.userId()))
                .andExpect(jsonPath("$.cartItems[0].id").value(CART_ITEM_ID))
                .andExpect(jsonPath("$.cartItems[0].bookId").value(BOOK_ID))
                .andExpect(jsonPath("$.cartItems[0].quantity").value(QUANTITY));
    }

    @Test
    @DisplayName("Update cart item quantity should return updated cart DTO")
    void updateItemQuantity_ReturnsUpdatedShoppingCartResponseDto() throws Exception {
        when(shoppingCartService.updateQuantity(eq(USER_ID), eq(CART_ITEM_ID),
                any(CartItemUpdateQuantityDto.class)))
                .thenReturn(shoppingCartResponseDto);

        mockMvc.perform(put("/cart/" + CART_ITEM_ID)
                        .with(user(getTestUser()))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartItemUpdateQuantityDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(shoppingCartResponseDto.id()))
                .andExpect(jsonPath("$.userId").value(shoppingCartResponseDto.userId()))
                .andExpect(jsonPath("$.cartItems[0].id").value(CART_ITEM_ID))
                .andExpect(jsonPath("$.cartItems[0].bookId").value(BOOK_ID))
                .andExpect(jsonPath("$.cartItems[0].quantity").value(QUANTITY));
    }

    @Test
    @DisplayName("Delete cart item should return no content")
    void delete_ReturnsNoContent() throws Exception {
        doNothing().when(shoppingCartService).deleteCartItem(USER_ID, CART_ITEM_ID);

        mockMvc.perform(delete("/cart/" + CART_ITEM_ID)
                        .with(user(getTestUser()))
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    private User getTestUser() {
        User user = new User();
        user.setId(USER_ID);
        user.setEmail("user@example.com");
        user.setPassword("password");
        user.setFirstName("John");
        user.setLastName("Doe");
        Role role = new Role();
        role.setId(1L);
        role.setRoleName(Role.RoleName.USER);
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);

        return user;
    }
}
