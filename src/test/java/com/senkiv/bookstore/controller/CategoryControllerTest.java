package com.senkiv.bookstore.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senkiv.bookstore.dto.BookDtoWithoutCategories;
import com.senkiv.bookstore.dto.CategoryRequestDto;
import com.senkiv.bookstore.dto.CategoryResponseDto;
import com.senkiv.bookstore.service.BookService;
import com.senkiv.bookstore.service.CategoryService;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class CategoryControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private CategoryService categoryService;
    @MockitoBean
    private BookService bookService;
    private CategoryResponseDto categoryResponseDto;
    private CategoryRequestDto categoryRequestDto;
    private BookDtoWithoutCategories bookDtoWithoutCategories;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        categoryResponseDto = new CategoryResponseDto(
                1L,
                "Test Category",
                "Test Description"
        );
        categoryRequestDto = new CategoryRequestDto(
                "Test Category",
                "Test Description"
        );
        bookDtoWithoutCategories = new BookDtoWithoutCategories(
                1L,
                "Test Book",
                "Test Author",
                "978-3-16-148410-0",
                new BigDecimal("29.99"),
                "Test Description",
                "test-cover.jpg"
        );
        pageable = PageRequest.of(0, 10);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Create category should return created category DTO")
    void createCategory_ReturnsCreatedCategoryDto() throws Exception {
        when(categoryService.save(any(CategoryRequestDto.class))).thenReturn(categoryResponseDto);

        mockMvc.perform(post("/categories")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(categoryResponseDto.id()))
                .andExpect(jsonPath("$.name").value(categoryResponseDto.name()))
                .andExpect(jsonPath("$.description").value(categoryResponseDto.description()));
    }

    @Test
    @WithMockUser
    @DisplayName("Get all categories should return page of category DTOs")
    void getAll_ReturnsPageOfCategoryDto() throws Exception {
        List<CategoryResponseDto> categories = List.of(categoryResponseDto);
        Page<CategoryResponseDto> categoryPage = new PageImpl<>(categories, pageable,
                categories.size());
        when(categoryService.findAll(any(Pageable.class))).thenReturn(categoryPage);

        mockMvc.perform(get("/categories")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(categoryResponseDto.id()))
                .andExpect(jsonPath("$.content[0].name").value(categoryResponseDto.name()))
                .andExpect(jsonPath("$.content[0].description").value(
                        categoryResponseDto.description()))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @WithMockUser
    @DisplayName("Get category by ID should return category DTO")
    void getCategoryById_ReturnsCategoryDto() throws Exception {
        when(categoryService.findById(1L)).thenReturn(categoryResponseDto);

        mockMvc.perform(get("/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(categoryResponseDto.id()))
                .andExpect(jsonPath("$.name").value(categoryResponseDto.name()))
                .andExpect(jsonPath("$.description").value(categoryResponseDto.description()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Update category should return updated category DTO")
    void updateCategory_ReturnsUpdatedCategoryDto() throws Exception {
        when(categoryService.updateById(eq(1L), any(CategoryRequestDto.class))).thenReturn(
                categoryResponseDto);

        mockMvc.perform(put("/categories/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(categoryResponseDto.id()))
                .andExpect(jsonPath("$.name").value(categoryResponseDto.name()))
                .andExpect(jsonPath("$.description").value(categoryResponseDto.description()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Delete category should return no content")
    void deleteCategory_CallsServiceMethod() throws Exception {
        doNothing().when(categoryService).deleteById(1L);

        mockMvc.perform(delete("/categories/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    @DisplayName("Get books by category ID should return page of book DTOs without categories")
    void getBooksByCategoryId_ReturnsPageOfBookDtoWithoutCategories() throws Exception {
        List<BookDtoWithoutCategories> books = List.of(bookDtoWithoutCategories);
        Page<BookDtoWithoutCategories> bookPage = new PageImpl<>(books, pageable, books.size());
        when(bookService.searchByCategories(any(Pageable.class), eq(1L))).thenReturn(bookPage);

        mockMvc.perform(get("/categories/1/books")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(bookDtoWithoutCategories.id()))
                .andExpect(jsonPath("$.content[0].title").value(bookDtoWithoutCategories.title()))
                .andExpect(jsonPath("$.content[0].author").value(bookDtoWithoutCategories.author()))
                .andExpect(jsonPath("$.content[0].isbn").value(bookDtoWithoutCategories.isbn()))
                .andExpect(jsonPath("$.totalElements").value(1));
    }
}
