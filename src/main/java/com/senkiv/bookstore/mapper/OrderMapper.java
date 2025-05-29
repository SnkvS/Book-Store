package com.senkiv.bookstore.mapper;

import com.senkiv.bookstore.config.MapperConfig;
import com.senkiv.bookstore.dto.OrderResponseDto;
import com.senkiv.bookstore.model.Order;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class, uses = OrderItemMapper.class)
public interface OrderMapper {
    OrderResponseDto toDto(Order order);
}
