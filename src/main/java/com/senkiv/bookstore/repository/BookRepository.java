package com.senkiv.bookstore.repository;

import com.senkiv.bookstore.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {
    boolean existsBookByIsbn(String isbn);

    @Query("SELECT b FROM Book b JOIN b.categories c WHERE c.id = :id")
    Page<Book> findAllByCategory(Pageable pageable, @Param("id") Long id);

    @EntityGraph(value = "books-with-categories", type = EntityGraphType.LOAD)
    Page<Book> findAll(Pageable pageable);
}
