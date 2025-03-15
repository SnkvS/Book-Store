package com.senkiv.bookstore.repository;

import com.senkiv.bookstore.model.Book;
import java.util.List;

public interface BookRepository {
    Book save(Book book);

    List<Book> findAll();
}
