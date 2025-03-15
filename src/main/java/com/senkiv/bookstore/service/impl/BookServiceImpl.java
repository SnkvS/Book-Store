package com.senkiv.bookstore.service.impl;

import com.senkiv.bookstore.dto.BookDto;
import com.senkiv.bookstore.dto.CreateBookRequestDto;
import com.senkiv.bookstore.mapper.BookMapper;
import com.senkiv.bookstore.repository.BookRepository;
import com.senkiv.bookstore.service.BookService;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
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
                .orElseThrow(EntityNotFoundException::new);
    }
}
