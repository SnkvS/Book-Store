package com.senkiv.bookstore.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.senkiv.bookstore.dto.BookDto;
import com.senkiv.bookstore.dto.BookDtoWithoutCategories;
import com.senkiv.bookstore.dto.BookSearchParametersDto;
import com.senkiv.bookstore.dto.CreateBookRequestDto;
import com.senkiv.bookstore.exception.BookExistsException;
import com.senkiv.bookstore.mapper.BookMapper;
import com.senkiv.bookstore.model.Book;
import com.senkiv.bookstore.model.Category;
import com.senkiv.bookstore.repository.BookRepository;
import com.senkiv.bookstore.repository.BookSpecificationBuilder;
import com.senkiv.bookstore.service.impl.BookServiceImpl;
import java.math.BigDecimal;
import java.util.Collections;
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
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {
    @Mock
    private BookRepository bookRepository;
    @Mock
    private BookMapper bookMapper;
    @Mock
    private BookSpecificationBuilder specificationBuilder;
    @InjectMocks
    private BookServiceImpl bookService;
    private Book book;
    private BookDto bookDto;
    private CreateBookRequestDto createBookRequestDto;
    private Set<Category> categories;
    private Set<Long> categoryIds;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        categoryIds = Set.of(1L, 2L);
        categories = new HashSet<>();
        Category category1 = new Category();
        category1.setId(1L);
        Category category2 = new Category();
        category2.setId(2L);
        categories.add(category1);
        categories.add(category2);
        book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setIsbn("978-3-16-148410-0");
        book.setPrice(new BigDecimal("29.99"));
        book.setDescription("Test Description");
        book.setCoverImage("test-cover.jpg");
        book.setCategories(categories);
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
    @DisplayName("Save book with valid data.")
    void save_ValidBook_ReturnsBookDto() {
        // Given
        when(bookRepository.existsBookByIsbn(createBookRequestDto.isbn())).thenReturn(false);
        when(bookMapper.toModel(createBookRequestDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        BookDto result = bookService.save(createBookRequestDto);

        assertThat(result).isNotNull();
        assertEquals(bookDto, result);
        verify(bookRepository).existsBookByIsbn(createBookRequestDto.isbn());
        verify(bookMapper).toModel(createBookRequestDto);
        verify(bookRepository).save(book);
        verify(bookMapper).toDto(book);
    }

    @Test
    @DisplayName("Should throw an exception in case of duplication of ISBN.")
    void save_ExistingIsbn_ThrowsBookExistsException() {
        when(bookRepository.existsBookByIsbn(createBookRequestDto.isbn())).thenReturn(true);

        assertThrows(BookExistsException.class, () -> bookService.save(createBookRequestDto));
        verify(bookRepository).existsBookByIsbn(createBookRequestDto.isbn());
    }

    @Test
    @DisplayName("Should return all books dto in Pageable")
    void findAll_ReturnsPageOfBookDto() {
        List<Book> books = List.of(book);
        Page<Book> bookPage = new PageImpl<>(books, pageable, books.size());

        when(bookRepository.findAll(pageable)).thenReturn(bookPage);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        Page<BookDto> result = bookService.findAll(pageable);

        assertThat(result).isNotNull();
        assertEquals(1, result.getTotalElements());
        assertEquals(bookDto, result.getContent().get(0));
        verify(bookRepository).findAll(pageable);
        verify(bookMapper).toDto(book);
    }

    @Test
    @DisplayName("Should return valid BookDto in case of valid Id.")
    void findById_ExistingId_ReturnsBookDto() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        BookDto result = bookService.findById(1L);

        assertThat(result).isNotNull();
        assertEquals(bookDto, result);
        verify(bookRepository).findById(1L);
        verify(bookMapper).toDto(book);
    }

    @Test
    @DisplayName("Should throw an exception in case of non existing id.")
    void findById_NonExistingId_ThrowsException() {
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> bookService.findById(999L));
        verify(bookRepository).findById(999L);
    }

    @Test
    @DisplayName("Should update Book in case of valid id.")
    void updateById_ExistingId_ReturnsUpdatedBookDto() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookMapper.update(createBookRequestDto, book)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        BookDto result = bookService.updateById(1L, createBookRequestDto);

        assertThat(result).isNotNull();
        assertEquals(bookDto, result);
        verify(bookRepository).findById(1L);
        verify(bookMapper).update(createBookRequestDto, book);
        verify(bookRepository).save(book);
        verify(bookMapper).toDto(book);
    }

    @Test
    @DisplayName("Should throw exception in case of invalid id.")
    void updateById_NonExistingId_ThrowsException() {
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(Exception.class,
                () -> bookService.updateById(999L, createBookRequestDto));
        verify(bookRepository).findById(999L);
    }

    @Test
    @DisplayName("Should delete book in case of valid id.")
    void deleteById_CallsRepositoryDeleteById() {
        doNothing().when(bookRepository).deleteById(1L);

        bookService.deleteById(1L);

        verify(bookRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should do nothing in case of invalid id.")
    void deleteById_NonExistingId_DoesNotThrowException() {
        doNothing().when(bookRepository).deleteById(999L);

        assertDoesNotThrow(() -> bookService.deleteById(999L));
        verify(bookRepository).deleteById(999L);
    }

    @Test
    @DisplayName("Should return Page<BookDto> in case of valid search parameters.")
    void searchByParams_ReturnsPageOfBookDto() {
        List<Book> books = List.of(book);
        Page<Book> bookPage = new PageImpl<>(books, pageable, books.size());
        BookSearchParametersDto searchParams = new BookSearchParametersDto(
                List.of("Test"),
                List.of("Author"),
                Collections.emptyList()
        );
        Specification<Book> specification = (root, query, criteriaBuilder) -> null;

        when(specificationBuilder.build(searchParams)).thenReturn(specification);
        when(bookRepository.findAll(specification, pageable)).thenReturn(bookPage);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        Page<BookDto> result = bookService.searchByParams(pageable, searchParams);

        assertThat(result).isNotNull();
        assertEquals(1, result.getTotalElements());
        assertEquals(bookDto, result.getContent().get(0));
        verify(specificationBuilder).build(searchParams);
        verify(bookRepository).findAll(specification, pageable);
        verify(bookMapper).toDto(book);
    }

    @Test
    @DisplayName("Should return Page<BookDto> in case of empty parameters.")
    void searchByParams_EmptyParameters_ReturnsPageOfBookDto() {
        List<Book> books = List.of(book);
        Page<Book> bookPage = new PageImpl<>(books, pageable, books.size());
        BookSearchParametersDto emptySearchParams = new BookSearchParametersDto(
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList()
        );
        Specification<Book> specification = (root, query, criteriaBuilder) -> null;

        when(specificationBuilder.build(emptySearchParams)).thenReturn(specification);
        when(bookRepository.findAll(specification, pageable)).thenReturn(bookPage);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        Page<BookDto> result = bookService.searchByParams(pageable, emptySearchParams);

        assertThat(result).isNotNull();
        assertEquals(1, result.getTotalElements());
        assertEquals(bookDto, result.getContent().get(0));
        verify(specificationBuilder).build(emptySearchParams);
        verify(bookRepository).findAll(specification, pageable);
        verify(bookMapper).toDto(book);
    }

    @Test
    @DisplayName("Should return BookDto without categories.")
    void searchByCategories_ReturnsPageOfBookDtoWithoutCategories() {
        List<Book> books = List.of(book);
        Page<Book> bookPage = new PageImpl<>(books, pageable, books.size());
        BookDtoWithoutCategories bookDtoWithoutCategories = new BookDtoWithoutCategories(
                1L,
                "Test Book",
                "Test Author",
                "978-3-16-148410-0",
                new BigDecimal("29.99"),
                "Test Description",
                "test-cover.jpg"
        );

        when(bookRepository.findByCategoriesId(pageable, 1L)).thenReturn(bookPage);
        when(bookMapper.toDtoWithoutCategories(book)).thenReturn(bookDtoWithoutCategories);

        Page<BookDtoWithoutCategories> result = bookService.searchByCategories(pageable, 1L);

        assertThat(result).isNotNull();
        assertEquals(1, result.getTotalElements());
        assertEquals(bookDtoWithoutCategories, result.getContent().get(0));
        verify(bookRepository).findByCategoriesId(pageable, 1L);
        verify(bookMapper).toDtoWithoutCategories(book);
    }

    @Test
    @DisplayName("Should return empty page in case of unknown Category.")
    void searchByCategories_NonExistingCategoryId_ReturnsEmptyPage() {
        Page<Book> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(bookRepository.findByCategoriesId(pageable, 999L)).thenReturn(emptyPage);

        Page<BookDtoWithoutCategories> result = bookService.searchByCategories(pageable, 999L);

        assertThat(result).isNotNull();
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
        verify(bookRepository).findByCategoriesId(pageable, 999L);
    }
}
