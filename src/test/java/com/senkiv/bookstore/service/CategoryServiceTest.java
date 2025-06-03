package com.senkiv.bookstore.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.senkiv.bookstore.dto.CategoryRequestDto;
import com.senkiv.bookstore.dto.CategoryResponseDto;
import com.senkiv.bookstore.exception.CategoryExistsException;
import com.senkiv.bookstore.mapper.CategoryMapper;
import com.senkiv.bookstore.model.Book;
import com.senkiv.bookstore.model.Category;
import com.senkiv.bookstore.repository.BookRepository;
import com.senkiv.bookstore.repository.CategoryRepository;
import com.senkiv.bookstore.service.impl.CategoryServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private CategoryMapper categoryMapper;
    @InjectMocks
    private CategoryServiceImpl categoryService;
    private Category category;
    private CategoryResponseDto categoryResponseDto;
    private CategoryRequestDto categoryRequestDto;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("Test Category");
        category.setDescription("Test Description");
        categoryResponseDto = new CategoryResponseDto(
                1L,
                "Test Category",
                "Test Description"
        );
        categoryRequestDto = new CategoryRequestDto(
                "Test Category",
                "Test Description"
        );
        pageable = PageRequest.of(0, 10);
    }

    @Test
    @DisplayName("Save category with valid data.")
    void save_ValidCategory_ReturnsCategoryResponseDto() {
        when(categoryRepository.existsByName(categoryRequestDto.name())).thenReturn(false);
        when(categoryMapper.toModel(categoryRequestDto)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(categoryResponseDto);

        CategoryResponseDto result = categoryService.save(categoryRequestDto);

        assertThat(result).isNotNull();
        assertEquals(categoryResponseDto, result);
        verify(categoryRepository).existsByName(categoryRequestDto.name());
        verify(categoryMapper).toModel(categoryRequestDto);
        verify(categoryRepository).save(category);
        verify(categoryMapper).toDto(category);
    }

    @Test
    @DisplayName("Should throw an exception in case of duplication of name.")
    void save_ExistingName_ThrowsCategoryExistsException() {
        when(categoryRepository.existsByName(categoryRequestDto.name())).thenReturn(true);

        assertThrows(CategoryExistsException.class, () -> categoryService.save(categoryRequestDto));
        verify(categoryRepository).existsByName(categoryRequestDto.name());
    }

    @Test
    @DisplayName("Should return all categories dto in Pageable")
    void findAll_ReturnsPageOfCategoryResponseDto() {
        List<Category> categories = List.of(category);
        Page<Category> categoryPage = new PageImpl<>(categories, pageable, categories.size());

        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        when(categoryMapper.toDto(category)).thenReturn(categoryResponseDto);

        Page<CategoryResponseDto> result = categoryService.findAll(pageable);

        assertThat(result).isNotNull();
        assertEquals(1, result.getTotalElements());
        assertEquals(categoryResponseDto, result.getContent().get(0));
        verify(categoryRepository).findAll(pageable);
        verify(categoryMapper).toDto(category);
    }

    @Test
    @DisplayName("Should return valid CategoryResponseDto in case of valid Id.")
    void findById_ExistingId_ReturnsCategoryResponseDto() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(categoryResponseDto);

        CategoryResponseDto result = categoryService.findById(1L);

        assertThat(result).isNotNull();
        assertEquals(categoryResponseDto, result);
        verify(categoryRepository).findById(1L);
        verify(categoryMapper).toDto(category);
    }

    @Test
    @DisplayName("Should throw an exception in case of non existing id.")
    void findById_NonExistingId_ThrowsEntityNotFoundException() {
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> categoryService.findById(999L));
        verify(categoryRepository).findById(999L);
    }

    @Test
    @DisplayName("Should update Category in case of valid id.")
    void updateById_ExistingId_ReturnsUpdatedCategoryResponseDto() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryMapper.update(categoryRequestDto, category)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(categoryResponseDto);

        CategoryResponseDto result = categoryService.updateById(1L, categoryRequestDto);

        assertThat(result).isNotNull();
        assertEquals(categoryResponseDto, result);
        verify(categoryRepository).findById(1L);
        verify(categoryMapper).update(categoryRequestDto, category);
        verify(categoryRepository).save(category);
        verify(categoryMapper).toDto(category);
    }

    @Test
    @DisplayName("Should throw exception in case of invalid id.")
    void updateById_NonExistingId_ThrowsEntityNotFoundException() {
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> categoryService.updateById(999L, categoryRequestDto));
        verify(categoryRepository).findById(999L);
    }

    @Test
    @DisplayName("Should delete category and update books in case of valid id.")
    void deleteById_ExistingId_DeletesCategoryAndUpdateBooks() {
        Book book = new Book();
        Set<Category> categories = new HashSet<>();
        categories.add(category);
        book.setCategories(categories);
        List<Book> books = List.of(book);

        when(categoryRepository.findCategoryById(1L)).thenReturn(Optional.of(category));
        when(bookRepository.findByCategoriesId(1L)).thenReturn(books);
        when(bookRepository.saveAll(books)).thenReturn(books);
        doNothing().when(categoryRepository).deleteById(1L);

        categoryService.deleteById(1L);

        verify(categoryRepository).findCategoryById(1L);
        verify(bookRepository).findByCategoriesId(1L);
        verify(bookRepository).saveAll(books);
        verify(categoryRepository).deleteById(1L);
        assertTrue(book.getCategories().isEmpty());
    }

    @Test
    @DisplayName("Should throw exception in case of invalid id.")
    void deleteById_NonExistingId_ThrowsCategoryExistsException() {
        when(categoryRepository.findCategoryById(999L)).thenReturn(Optional.empty());

        assertThrows(CategoryExistsException.class, () -> categoryService.deleteById(999L));
        verify(categoryRepository).findCategoryById(999L);
    }
}
