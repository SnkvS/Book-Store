package com.senkiv.bookstore.repository.book;

import com.senkiv.bookstore.model.Book;
import com.senkiv.bookstore.repository.SpecificationProvider;
import java.util.Arrays;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class AuthorSpecificationProvider implements SpecificationProvider<Book> {
    private static final String KEY = "author";

    @Override
    public Specification<Book> getSpecification(String[] params) {
        return ((root, query, criteriaBuilder) -> root.get(KEY).in(Arrays.asList(params)));
    }

    @Override
    public String getKey() {
        return KEY;
    }
}
