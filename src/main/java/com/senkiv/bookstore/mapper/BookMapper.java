package com.senkiv.bookstore.mapper;

import com.senkiv.bookstore.config.MapperConfig;
import com.senkiv.bookstore.dto.BookDto;
import com.senkiv.bookstore.dto.CreateBookRequestDto;
import com.senkiv.bookstore.model.Book;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    Book toModel(CreateBookRequestDto dto);

    Book update(CreateBookRequestDto dto, @MappingTarget Book book);

    BookDto toDto(Book model);
}
