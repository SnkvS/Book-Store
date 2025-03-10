package com.senkiv.bookstore.service;

import com.senkiv.bookstore.model.Book;
import java.util.List;

public interface BookService {
    Book save(Book book);

    List<Book> findAll();
}
