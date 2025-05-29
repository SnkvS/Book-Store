package com.senkiv.bookstore.repository;

import com.senkiv.bookstore.model.ShoppingCart;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
    @EntityGraph("cart-with-items-user")
    Optional<ShoppingCart> findShoppingCartByUserId(Long id);
}
