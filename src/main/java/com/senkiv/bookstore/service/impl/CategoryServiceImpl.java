package com.senkiv.bookstore.service.impl;

import com.senkiv.bookstore.dto.CategoryRequestDto;
import com.senkiv.bookstore.dto.CategoryResponseDto;
import com.senkiv.bookstore.exception.CategoryExistsException;
import com.senkiv.bookstore.mapper.CategoryMapper;
import com.senkiv.bookstore.model.Book;
import com.senkiv.bookstore.model.Category;
import com.senkiv.bookstore.repository.BookRepository;
import com.senkiv.bookstore.repository.CategoryRepository;
import com.senkiv.bookstore.service.CategoryService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {
    public static final String CATEGORY_WITH_NAME_S_ALREADY_EXISTS =
            "Category with such name already exists -> %s.";
    public static final String THERE_IS_NO_CATEGORY_WITH_SUCH_ID_D =
            "There is no category with such id -> %s.";
    private final CategoryRepository categoryRepository;
    private final CategoryMapper mapper;
    private final BookRepository bookRepository;

    @Override
    public CategoryResponseDto save(CategoryRequestDto dto) {
        if (categoryRepository.existsByName(dto.name())) {
            throw new CategoryExistsException(
                    CATEGORY_WITH_NAME_S_ALREADY_EXISTS.formatted(dto.name()));
        }

        return mapper.toDto(categoryRepository.save(mapper.toModel(dto)));
    }

    @Override
    public Page<CategoryResponseDto> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable)
                .map(mapper::toDto);
    }

    @Override
    public CategoryResponseDto findById(Long id) {
        return categoryRepository.findById(id).map(mapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(
                        THERE_IS_NO_CATEGORY_WITH_SUCH_ID_D.formatted(id)));
    }

    @Override
    public CategoryResponseDto updateById(Long id, CategoryRequestDto categoryDto) {
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(
                        THERE_IS_NO_CATEGORY_WITH_SUCH_ID_D.formatted(id)));
        Category updatedCategory = mapper.update(categoryDto, category);
        return mapper.toDto(categoryRepository.save(updatedCategory));
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        //remove all relationships to the entity that should be deleted
        Category category = categoryRepository.findCategoryById(id)
                .orElseThrow(() -> new CategoryExistsException(
                        THERE_IS_NO_CATEGORY_WITH_SUCH_ID_D.formatted(id)));
        List<Book> booksWithCategory = bookRepository.findByCategoriesId(id);
        booksWithCategory.forEach((book) -> {
            book.getCategories().remove(category);
        });
        bookRepository.saveAll(booksWithCategory);
        categoryRepository.deleteById(id);
    }
}

