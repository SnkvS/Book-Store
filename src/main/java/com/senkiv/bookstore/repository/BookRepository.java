package com.senkiv.bookstore.repository;

import com.senkiv.bookstore.model.Book;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {
    boolean existsBookByIsbn(String isbn);

    @EntityGraph(value = "books-with-categories", type = EntityGraphType.LOAD)
    List<Book> findByCategoriesId(Long categoryId);

    @EntityGraph(value = "books-with-categories", type = EntityGraphType.LOAD)
    Page<Book> findByCategoriesId(Pageable pageable, Long categoryId);

    @EntityGraph(value = "books-with-categories", type = EntityGraphType.LOAD)
    Page<Book> findAll(Pageable pageable);
}
