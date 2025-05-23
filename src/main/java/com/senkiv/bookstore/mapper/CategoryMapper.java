package com.senkiv.bookstore.mapper;

import com.senkiv.bookstore.config.MapperConfig;
import com.senkiv.bookstore.dto.CategoryRequestDto;
import com.senkiv.bookstore.dto.CategoryResponseDto;
import com.senkiv.bookstore.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface CategoryMapper {
    Category toModel(CategoryRequestDto dto);

    CategoryResponseDto toDto(Category model);

    Category update(CategoryRequestDto dto, @MappingTarget Category category);
}
