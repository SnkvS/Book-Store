package com.senkiv.bookstore.repository;

public interface SpecificationProviderManager<T> {
    SpecificationProvider<T> getSpecification(String key);
}
