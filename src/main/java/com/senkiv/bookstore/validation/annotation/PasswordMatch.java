package com.senkiv.bookstore.validation.annotation;

import com.senkiv.bookstore.validation.RepeatedPasswordValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = RepeatedPasswordValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PasswordMatch {

    String PASSWORDS_ARE_NOT_MATCHING = "Passwords are not matching";

    String message() default PASSWORDS_ARE_NOT_MATCHING;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
