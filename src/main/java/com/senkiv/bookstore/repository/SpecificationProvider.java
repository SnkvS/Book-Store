package com.senkiv.bookstore.repository;

import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public interface SpecificationProvider<T> {
    Specification<T> getSpecification(String key, List<String> params);

    String getKey();
}
