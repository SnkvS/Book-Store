package com.senkiv.bookstore.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import org.hibernate.validator.constraints.ISBN;

public record CreateBookRequestDto(@NotNull String title, @NotNull String author,
                                   @NotNull @ISBN String isbn,
                                   @Min(value = 0) BigDecimal price,
                                   String description, String coverImage) {
}
