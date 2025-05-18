package com.senkiv.bookstore.repository;

import com.senkiv.bookstore.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    @Query(value = "SELECT u FROM User u JOIN FETCH u.roles WHERE u.email = :username")
    Optional<User> findByEmail(String username);
}
