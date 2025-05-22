package com.senkiv.bookstore.service;

import com.senkiv.bookstore.dto.CategoryRequestDto;
import com.senkiv.bookstore.dto.CategoryResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryService {
    CategoryResponseDto save(CategoryRequestDto dto);

    Page<CategoryResponseDto> findAll(Pageable pageable);

    CategoryResponseDto findById(Long id);

    CategoryResponseDto updateById(Long id, CategoryRequestDto bookDto);

    void deleteById(Long id);
}
