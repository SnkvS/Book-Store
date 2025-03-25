package com.senkiv.bookstore.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class SpecificationProvider<T> {
    public Specification<T> getSpecification(String param, String fieldName) {
        return (
                (root, query, criteriaBuilder) -> criteriaBuilder
                .like(criteriaBuilder.lower(root.get(fieldName)), "%" + param.toLowerCase() + "%"));
    }
}
