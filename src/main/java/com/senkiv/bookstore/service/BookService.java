package com.senkiv.bookstore.service;

import com.senkiv.bookstore.dto.BookDto;
import com.senkiv.bookstore.dto.BookSearchParametersDto;
import com.senkiv.bookstore.dto.CreateBookRequestDto;
import java.util.List;

public interface BookService {
    BookDto save(CreateBookRequestDto book);

    List<BookDto> findAll();

    BookDto findById(Long id);

    BookDto updateById(Long id, CreateBookRequestDto bookDto);

    void deleteById(Long id);

    List<BookDto> searchByParams(BookSearchParametersDto dto);
}
