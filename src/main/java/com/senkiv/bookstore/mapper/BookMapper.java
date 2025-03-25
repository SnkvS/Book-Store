package com.senkiv.bookstore.mapper;

import com.senkiv.bookstore.config.MapperConfig;
import com.senkiv.bookstore.dto.BookDto;
import com.senkiv.bookstore.dto.BookSearchParametersDto;
import com.senkiv.bookstore.dto.CreateBookRequestDto;
import com.senkiv.bookstore.model.Book;
import java.util.HashMap;
import java.util.Map;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    Book toModel(CreateBookRequestDto dto);

    Book update(CreateBookRequestDto dto, @MappingTarget Book book);

    BookDto toDto(Book model);

    default Map<String, String> toMap(BookSearchParametersDto dto) {
        Map<String, String> parameterMap = new HashMap<>();
        if (dto.author() != null) {
            parameterMap.put("author", dto.author());
        }
        if (dto.title() != null) {
            parameterMap.put("title", dto.title());
        }
        if (dto.isbn() != null) {
            parameterMap.put("isbn", dto.isbn());
        }
        return parameterMap;
    }

}
