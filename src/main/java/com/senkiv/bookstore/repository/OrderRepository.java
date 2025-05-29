package com.senkiv.bookstore.repository;

import com.senkiv.bookstore.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @EntityGraph("order-with-items")
    Page<Order> findOrdersByUserId(Long userId, Pageable pageable);

    boolean existsByIdAndUserId(Long orderId, Long userId);
}
