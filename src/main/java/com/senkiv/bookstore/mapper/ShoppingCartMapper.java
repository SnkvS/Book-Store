package com.senkiv.bookstore.mapper;

import com.senkiv.bookstore.config.MapperConfig;
import com.senkiv.bookstore.dto.ShoppingCartResponseDto;
import com.senkiv.bookstore.model.ShoppingCart;
import com.senkiv.bookstore.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class, uses = CartItemMapper.class)
public interface ShoppingCartMapper {
    @Mapping(source = "user", target = "userId", qualifiedByName = "idFromUser")
    ShoppingCartResponseDto toDto(ShoppingCart model);

    @Named("idFromUser")
    default Long idFromUser(User user) {
        return user.getId();
    }
}
