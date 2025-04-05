package com.senkiv.bookstore.repository.book;

import com.senkiv.bookstore.model.Book;
import com.senkiv.bookstore.repository.BookSpecificationBuilder;
import com.senkiv.bookstore.repository.SpecificationProvider;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class IsbnSpecificationProvider implements SpecificationProvider<Book> {
    @Override
    public Specification<Book> getSpecification(String key, List<String> params) {
        return (root, criteriaQuery, criteriaBuilder) -> root.get(key).in(params);
    }

    @Override
    public String getKey() {
        return BookSpecificationBuilder.ISBN;
    }
}
