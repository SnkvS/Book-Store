package com.senkiv.bookstore.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.Set;
import org.hibernate.validator.constraints.ISBN;

public record CreateBookRequestDto(@NotBlank String title, @NotBlank String author,
                                   @NotBlank @ISBN String isbn,
                                   @Positive BigDecimal price,
                                   String description, String coverImage,
                                   @NotEmpty Set<Long> categories) {
}
