package com.senkiv.bookstore.repository.book;

import com.senkiv.bookstore.model.Book;
import com.senkiv.bookstore.repository.SpecificationProvider;
import java.util.Arrays;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class TitleSpecificationProvider implements SpecificationProvider<Book> {
    private static final String KEY = "title";

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public Specification<Book> getSpecification(String[] params) {
        return (root, criteriaQuery, criteriaBuilder) -> root.get(KEY).in(Arrays.asList(params));
    }
}
