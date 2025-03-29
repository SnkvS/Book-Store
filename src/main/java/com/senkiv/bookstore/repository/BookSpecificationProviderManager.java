package com.senkiv.bookstore.repository;

import com.senkiv.bookstore.model.Book;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookSpecificationProviderManager implements SpecificationProviderManager<Book> {
    private static final String NO_PARAMETER_MESSAGE =
            "There is no such parameter forBook entity -> %s";
    private final List<SpecificationProvider<Book>> specificationProviders;

    @Override
    public SpecificationProvider<Book> getSpecification(String key) {
        return specificationProviders.stream()
                .filter(spec -> spec.getKey().equals(key))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        NO_PARAMETER_MESSAGE.formatted(key)));
    }
}
