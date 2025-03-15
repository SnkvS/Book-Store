package com.senkiv.bookstore.service;

import com.senkiv.bookstore.dto.BookDto;
import com.senkiv.bookstore.dto.CreateBookRequestDto;
import java.util.List;

public interface BookService {
    BookDto save(CreateBookRequestDto book);

    List<BookDto> findAll();

    BookDto findById(Long id);
}
