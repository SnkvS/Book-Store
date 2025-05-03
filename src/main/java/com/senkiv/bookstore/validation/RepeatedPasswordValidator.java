package com.senkiv.bookstore.validation;

import com.senkiv.bookstore.dto.UserRegistrationRequestDto;
import com.senkiv.bookstore.validation.annotation.PasswordMatch;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RepeatedPasswordValidator implements
        ConstraintValidator<PasswordMatch, UserRegistrationRequestDto> {
    @Override
    public boolean isValid(UserRegistrationRequestDto dto, ConstraintValidatorContext context) {
        return dto.password().equals(dto.repeatPassword());
    }
}
