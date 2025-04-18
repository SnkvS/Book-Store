package com.senkiv.bookstore.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.hibernate.validator.constraints.ISBN;

public record BookSearchParametersDto(List<@NotBlank String> title,
                                      List<@NotBlank String> author,
                                      List<@NotBlank @ISBN String> isbn) {
}
