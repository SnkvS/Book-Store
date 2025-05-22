package com.senkiv.bookstore.service;

import com.senkiv.bookstore.dto.BookDto;
import com.senkiv.bookstore.dto.BookDtoWithoutCategories;
import com.senkiv.bookstore.dto.BookSearchParametersDto;
import com.senkiv.bookstore.dto.CreateBookRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {
    BookDto save(CreateBookRequestDto book);

    Page<BookDto> findAll(Pageable pageable);

    BookDto findById(Long id);

    BookDto updateById(Long id, CreateBookRequestDto bookDto);

    void deleteById(Long id);

    Page<BookDto> searchByParams(Pageable pageable, BookSearchParametersDto dto);

    Page<BookDtoWithoutCategories> searchByCategories(Pageable pageable, Long categoryId);
}
