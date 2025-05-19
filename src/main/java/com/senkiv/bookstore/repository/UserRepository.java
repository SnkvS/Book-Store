package com.senkiv.bookstore.repository;

import com.senkiv.bookstore.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    @EntityGraph(value = "user-with-roles-graph", type = EntityGraphType.LOAD)
    Optional<User> findByEmail(String username);
}
