package com.senkiv.bookstore.dto;

import com.senkiv.bookstore.validation.annotation.PasswordMatch;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@PasswordMatch
public record UserRegistrationRequestDto(
        @Email
        @NotBlank
        String email,
        @NotBlank
        @Size(min = 8, max = 20)
        String password,
        String repeatPassword,
        @NotBlank
        String firstName,
        @NotBlank
        String lastName,
        String shippingAddress
) {
}
