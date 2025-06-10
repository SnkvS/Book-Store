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
import com.senkiv.bookstore.dto.BookDto;
import com.senkiv.bookstore.dto.BookSearchParametersDto;
import com.senkiv.bookstore.dto.CreateBookRequestDto;
import com.senkiv.bookstore.service.BookService;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Set;
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
class BookControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private BookService bookService;
    private BookDto bookDto;
    private CreateBookRequestDto createBookRequestDto;
    private Set<Long> categoryIds;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        categoryIds = Set.of(1L, 2L);
        bookDto = new BookDto();
        bookDto.setId(1L);
        bookDto.setTitle("Test Book");
        bookDto.setAuthor("Test Author");
        bookDto.setIsbn("978-3-16-148410-0");
        bookDto.setPrice(new BigDecimal("29.99"));
        bookDto.setDescription("Test Description");
        bookDto.setCoverImage("test-cover.jpg");
        bookDto.setCategories(categoryIds);
        createBookRequestDto = new CreateBookRequestDto(
                "Test Book",
                "Test Author",
                "978-3-16-148410-0",
                new BigDecimal("29.99"),
                "Test Description",
                "test-cover.jpg",
                categoryIds
        );
        pageable = PageRequest.of(0, 10);
    }

    @Test
    @WithMockUser
    @DisplayName("Get all books should return page of book DTOs")
    void getAll_ReturnsPageOfBookDto() throws Exception {
        List<BookDto> books = List.of(bookDto);
        Page<BookDto> bookPage = new PageImpl<>(books, pageable, books.size());
        when(bookService.findAll(any(Pageable.class))).thenReturn(bookPage);

        mockMvc.perform(get("/books")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(bookDto.getId()))
                .andExpect(jsonPath("$.content[0].title").value(bookDto.getTitle()))
                .andExpect(jsonPath("$.content[0].author").value(bookDto.getAuthor()))
                .andExpect(jsonPath("$.content[0].isbn").value(bookDto.getIsbn()))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @WithMockUser
    @DisplayName("Get book by ID should return book DTO")
    void getBookById_ReturnsBookDto() throws Exception {
        when(bookService.findById(1L)).thenReturn(bookDto);

        mockMvc.perform(get("/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookDto.getId()))
                .andExpect(jsonPath("$.title").value(bookDto.getTitle()))
                .andExpect(jsonPath("$.author").value(bookDto.getAuthor()))
                .andExpect(jsonPath("$.isbn").value(bookDto.getIsbn()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Create book should return created book DTO")
    void createBook_ReturnsCreatedBookDto() throws Exception {
        when(bookService.save(any(CreateBookRequestDto.class))).thenReturn(bookDto);

        mockMvc.perform(post("/books")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createBookRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(bookDto.getId()))
                .andExpect(jsonPath("$.title").value(bookDto.getTitle()))
                .andExpect(jsonPath("$.author").value(bookDto.getAuthor()))
                .andExpect(jsonPath("$.isbn").value(bookDto.getIsbn()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Update book should return updated book DTO")
    void updateBook_ReturnsUpdatedBookDto() throws Exception {
        when(bookService.updateById(eq(1L), any(CreateBookRequestDto.class))).thenReturn(bookDto);

        mockMvc.perform(put("/books/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createBookRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookDto.getId()))
                .andExpect(jsonPath("$.title").value(bookDto.getTitle()))
                .andExpect(jsonPath("$.author").value(bookDto.getAuthor()))
                .andExpect(jsonPath("$.isbn").value(bookDto.getIsbn()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Delete book should return no content")
    void deleteBook_ReturnsNoContent() throws Exception {
        doNothing().when(bookService).deleteById(1L);

        mockMvc.perform(delete("/books/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    @DisplayName("Search books should return page of book DTOs")
    void searchBooks_ReturnsPageOfBookDto() throws Exception {
        List<BookDto> books = List.of(bookDto);
        Page<BookDto> bookPage = new PageImpl<>(books, pageable, books.size());
        BookSearchParametersDto searchParams = new BookSearchParametersDto(
                List.of("Test"),
                List.of("Author"),
                Collections.emptyList()
        );

        when(bookService.searchByParams(any(Pageable.class), any(BookSearchParametersDto.class)))
                .thenReturn(bookPage);

        mockMvc.perform(get("/books/search")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchParams)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(bookDto.getId()))
                .andExpect(jsonPath("$.content[0].title").value(bookDto.getTitle()))
                .andExpect(jsonPath("$.content[0].author").value(bookDto.getAuthor()))
                .andExpect(jsonPath("$.totalElements").value(1));
    }
}
