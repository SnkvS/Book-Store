package com.senkiv.bookstore.mapper;

import com.senkiv.bookstore.config.MapperConfig;
import com.senkiv.bookstore.dto.CartItemRequestDto;
import com.senkiv.bookstore.dto.CartItemResponseDto;
import com.senkiv.bookstore.dto.CartItemUpdateQuantityDto;
import com.senkiv.bookstore.model.Book;
import com.senkiv.bookstore.model.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class)
public interface CartItemMapper {
    @Mapping(source = "bookId", target = "book", qualifiedByName = "bookFromId")
    CartItem toModel(CartItemRequestDto dto);

    @Mapping(source = "book", target = "bookId", qualifiedByName = "idFromBook")
    CartItemResponseDto toDto(CartItem model);

    CartItem update(CartItemUpdateQuantityDto dto, @MappingTarget CartItem model);

    @Named("idFromBook")
    default Long idFromBook(Book book) {
        return book.getId();
    }

    @Named("bookFromId")
    default Book bookFromId(Long id) {
        Book book = new Book();
        book.setId(id);
        return book;
    }
}
