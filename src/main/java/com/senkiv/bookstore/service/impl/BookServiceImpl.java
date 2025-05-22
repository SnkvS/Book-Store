package com.senkiv.bookstore.service.impl;

import com.senkiv.bookstore.dto.BookDto;
import com.senkiv.bookstore.dto.BookDtoWithoutCategories;
import com.senkiv.bookstore.dto.BookSearchParametersDto;
import com.senkiv.bookstore.dto.CreateBookRequestDto;
import com.senkiv.bookstore.exception.BookExistsException;
import com.senkiv.bookstore.mapper.BookMapper;
import com.senkiv.bookstore.model.Book;
import com.senkiv.bookstore.repository.BookRepository;
import com.senkiv.bookstore.repository.BookSpecificationBuilder;
import com.senkiv.bookstore.service.BookService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private static final String NO_BOOK_WITH_SUCH_ID =
            "There is no book with such id -> %s";
    private static final String BOOK_WITH_SUCH_ISBN_ALREADY_EXISTS =
            "Book with such isbn already exists -> %s.";
    private final BookMapper mapper;
    private final BookRepository bookRepository;
    private final BookSpecificationBuilder specificationBuilder;

    @Override
    public BookDto save(CreateBookRequestDto bookDto) {
        if (!bookRepository.existsBookByIsbn(bookDto.isbn())) {
            return mapper.toDto(bookRepository.save(mapper.toModel(bookDto)));
        }
        throw new BookExistsException(
                BOOK_WITH_SUCH_ISBN_ALREADY_EXISTS.formatted(bookDto.isbn()));
    }

    @Override
    public Page<BookDto> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable)
                .map(mapper::toDto);

    }

    @Override
    public BookDto findById(Long id) {
        return bookRepository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(
                        NO_BOOK_WITH_SUCH_ID.formatted(id)));
    }

    @Override
    public BookDto updateById(Long id, CreateBookRequestDto bookDto) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        NO_BOOK_WITH_SUCH_ID.formatted(id)));
        mapper.update(bookDto, book);
        bookRepository.save(book);
        return mapper.toDto(book);
    }

    @Override
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }

    @Override
    public Page<BookDto> searchByParams(Pageable pageable, BookSearchParametersDto dto) {
        return bookRepository.findAll(specificationBuilder.build(dto), pageable)
                .map(mapper::toDto);
    }

    @Override
    public Page<BookDtoWithoutCategories> searchByCategories(Pageable pageable, Long categoryId) {
        return bookRepository.findAllByCategory(pageable, categoryId)
                .map(mapper::toDtoWithoutCategories);
    }
}
