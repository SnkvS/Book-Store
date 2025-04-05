package com.senkiv.bookstore.repository;

import com.senkiv.bookstore.dto.BookSearchParametersDto;
import com.senkiv.bookstore.model.Book;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BookSpecificationBuilder implements SpecificationBuilder<Book> {
    public static final String AUTHOR = "author";
    public static final String ISBN = "isbn";
    public static final String TITLE = "title";
    private final SpecificationProviderManager<Book> specificationProviderManager;

    @Override
    public Specification<Book> build(BookSearchParametersDto parametersDto) {
        Specification<Book> specification = Specification.where(null);
        if (parametersDto.title() != null && !parametersDto.title().isEmpty()) {
            specification = addSpecification(TITLE, parametersDto.title(), specification);
        }
        if (parametersDto.author() != null && !parametersDto.author().isEmpty()) {
            specification = addSpecification(AUTHOR, parametersDto.author(), specification);
        }
        if (parametersDto.isbn() != null && !parametersDto.isbn().isEmpty()) {
            specification = addSpecification(ISBN, parametersDto.isbn(), specification);
        }
        return specification;
    }

    private Specification<Book> addSpecification(String key, List<String> parameters,
            Specification<Book> specification) {
        SpecificationProvider<Book> specProvider =
                specificationProviderManager.getSpecification(
                        key);
        Specification<Book> author = specProvider.getSpecification(key, parameters);
        specification = specification.and(author);
        return specification;
    }
}
