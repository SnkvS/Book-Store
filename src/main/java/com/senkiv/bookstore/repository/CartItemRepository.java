package com.senkiv.bookstore.repository;

import com.senkiv.bookstore.model.CartItem;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Set<CartItem> findCartItemByShoppingCartId(Long id);

    boolean existsCartItemByShoppingCartIdAndBookId(Long shoppingCartId, Long bookId);
}
