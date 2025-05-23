package com.senkiv.bookstore.controller;

import com.senkiv.bookstore.dto.BookDtoWithoutCategories;
import com.senkiv.bookstore.dto.CategoryRequestDto;
import com.senkiv.bookstore.dto.CategoryResponseDto;
import com.senkiv.bookstore.service.BookService;
import com.senkiv.bookstore.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Category API")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/categories")
public class CategoryController {
    private final CategoryService categoryService;
    private final BookService bookService;

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(description = "Creates a category.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponseDto createCategory(@RequestBody @Valid CategoryRequestDto requestDto) {
        return categoryService.save(requestDto);
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(description = "Retrieves all categories.")
    @GetMapping
    public Page<CategoryResponseDto> getAll(Pageable pageable) {
        return categoryService.findAll(pageable);
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(description = "Retrieves a category by it`s id")
    @GetMapping("/{id}")
    public CategoryResponseDto getCategoryById(@PathVariable Long id) {
        return categoryService.findById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(description = "Updates category by it`s id.")
    @PutMapping("/{id}")
    public CategoryResponseDto updateCategory(@RequestBody @Valid CategoryRequestDto dto,
            @PathVariable Long id) {
        return categoryService.updateById(id, dto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(description = "Deletes category by it`s id.")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long id) {
        categoryService.deleteById(id);
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(description = "Retrieves books by specific category.")
    @GetMapping("/{id}/books")
    public Page<BookDtoWithoutCategories> getBooksByCategoryId(@PathVariable Long id,
            Pageable pageable) {
        return bookService.searchByCategories(pageable, id);
    }
}
