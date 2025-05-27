package com.senkiv.bookstore.mapper;

import com.senkiv.bookstore.config.MapperConfig;
import com.senkiv.bookstore.dto.BookDto;
import com.senkiv.bookstore.dto.BookDtoWithoutCategories;
import com.senkiv.bookstore.dto.CreateBookRequestDto;
import com.senkiv.bookstore.model.Book;
import com.senkiv.bookstore.model.Category;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    Book toModel(CreateBookRequestDto dto);

    Book update(CreateBookRequestDto dto, @MappingTarget Book book);

    BookDto toDto(Book model);

    default Set<Long> mapToLong(Set<Category> categories) {
        return categories.stream()
                .map(Category::getId)
                .collect(Collectors.toSet());
    }

    default Set<Category> map(Set<Long> categoriesId) {
        return categoriesId.stream()
                .map(id -> {
                    Category category = new Category();
                    category.setId(id);
                    return category;
                })
                .collect(Collectors.toSet());

    }

    BookDtoWithoutCategories toDtoWithoutCategories(Book model);

    @Named("idFromBook")
    default Long idFromBook(Book book) {
        return book.getId();
    }
}
