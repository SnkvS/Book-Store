package com.senkiv.bookstore.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import org.hibernate.validator.constraints.ISBN;

public record CreateBookRequestDto(@NotBlank String title, @NotBlank String author,
                                   @NotBlank @ISBN String isbn,
                                   @Min(value = 0) BigDecimal price,
                                   String description, String coverImage) {
}
