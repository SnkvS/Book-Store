package com.senkiv.bookstore.service.impl;

import com.senkiv.bookstore.dto.BookDto;
import com.senkiv.bookstore.dto.CreateBookRequestDto;
import com.senkiv.bookstore.mapper.BookMapper;
import com.senkiv.bookstore.model.Book;
import com.senkiv.bookstore.repository.BookRepository;
import com.senkiv.bookstore.service.BookService;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    public static final String NO_BOOK_WITH_SUCH_ID =
            "There is no book with such id -> %s";
    public static final String NO_ENTITY_WITH_SUCH_ID = "There is no entity with such id -> %d";
    private final BookMapper mapper;
    private final BookRepository bookRepository;

    @Override
    public BookDto save(CreateBookRequestDto bookDto) {
        return mapper.toDto(bookRepository.save(mapper.toModel(bookDto)));
    }

    @Override
    public List<BookDto> findAll() {
        return bookRepository.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
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
        Optional<Book> byId = bookRepository.findById(id);
        if (byId.isPresent()) {
            Book model = mapper.toModel(bookDto);
            model.setId(id);
            bookRepository.save(model);
            return mapper.toDto(model);
        }
        throw new EntityNotFoundException(NO_ENTITY_WITH_SUCH_ID.formatted(id));
    }

    @Override
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }
}
