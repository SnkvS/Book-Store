package com.senkiv.bookstore.validation.annotation;

import com.senkiv.bookstore.validation.FieldMatchValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = FieldMatchValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface FieldMatch {

    String FIELDS_ARE_NOT_MATCHING = "Required field are not matching";

    String message() default FIELDS_ARE_NOT_MATCHING;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String first();

    String second();
}
