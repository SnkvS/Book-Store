package com.senkiv.bookstore.dto;

import java.math.BigDecimal;

public record BookDto(Long id, String title, String author, String isbn, BigDecimal price,
                      String description, String coverImage) {
}
