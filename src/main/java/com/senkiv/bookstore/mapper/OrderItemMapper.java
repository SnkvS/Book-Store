package com.senkiv.bookstore.mapper;

import com.senkiv.bookstore.config.MapperConfig;
import com.senkiv.bookstore.dto.OrderItemResponseDto;
import com.senkiv.bookstore.model.Book;
import com.senkiv.bookstore.model.CartItem;
import com.senkiv.bookstore.model.OrderItem;
import java.math.BigDecimal;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class)
public interface OrderItemMapper {
    @Mapping(source = "book", target = "price", qualifiedByName = "bookPrice")
    @Mapping(target = "id", ignore = true)
    OrderItem toOrderItem(CartItem cartItem);

    @Mapping(target = "bookId", source = "book", qualifiedByName = "bookId")
    OrderItemResponseDto toDto(OrderItem orderItem);

    Set<OrderItemResponseDto> map(Set<OrderItem> orderItems);

    @Named("bookPrice")
    static BigDecimal getBookPrice(Book book) {
        return book.getPrice();
    }

    @Named("bookId")
    default Long getBookId(Book book) {
        return book.getId();
    }
}
