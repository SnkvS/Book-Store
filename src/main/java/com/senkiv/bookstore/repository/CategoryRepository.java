package com.senkiv.bookstore.repository;

import com.senkiv.bookstore.model.Category;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(String name);

    @EntityGraph(value = "categories-with-books", type = EntityGraphType.LOAD)
    Optional<Category> findCategoryById(Long id);
}
