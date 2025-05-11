package com.senkiv.bookstore.validation;

import com.senkiv.bookstore.validation.annotation.FieldMatch;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Objects;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

public class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object> {
    private String firstFieldName;
    private String secondFieldName;

    @Override
    public void initialize(FieldMatch constraintAnnotation) {
        this.firstFieldName = constraintAnnotation.first();
        this.secondFieldName = constraintAnnotation.second();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        BeanWrapper objectWrapper = new BeanWrapperImpl(value);
        Object firstValue = objectWrapper.getPropertyValue(firstFieldName);
        Object secondValue = objectWrapper.getPropertyValue(secondFieldName);
        return Objects.equals(firstValue, secondValue);
    }
}
