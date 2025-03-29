package com.senkiv.bookstore.repository;

import com.senkiv.bookstore.dto.BookSearchParametersDto;
import org.springframework.data.jpa.domain.Specification;

public interface SpecificationBuilder<T> {
    Specification<T> build(BookSearchParametersDto parameter);
}
