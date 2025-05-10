package com.senkiv.bookstore.validation;

import com.senkiv.bookstore.exception.FieldValidationException;
import com.senkiv.bookstore.validation.annotation.FieldMatch;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

public class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object> {
    public static final String COULD_NOT_ACCESS_FIELD_ON_CLASS =
            "Could not access field '%s' on class '%s'";
    private String firstFieldName;
    private String secondFieldName;

    @Override
    public void initialize(FieldMatch constraintAnnotation) {
        this.firstFieldName = constraintAnnotation.first();
        this.secondFieldName = constraintAnnotation.second();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        try {
            Object firstValue = getProperty(value, firstFieldName);
            Object secondValue = getProperty(value, secondFieldName);

            return Objects.equals(firstValue, secondValue);
        } catch (Exception e) {
            return false;
        }
    }

    private Object getProperty(Object object, String fieldName) throws FieldValidationException {
        Class<?> clazz = object.getClass();
        Method method;
        if (clazz.isRecord()) {
            try {
                method = clazz.getMethod(fieldName);
            } catch (NoSuchMethodException e) {
                throw new FieldValidationException(
                        COULD_NOT_ACCESS_FIELD_ON_CLASS.formatted(fieldName, clazz.getSimpleName()),
                        e);
            }
        } else {
            try {
                method = clazz.getMethod(String.format("get%s", capitalize(fieldName)));
            } catch (NoSuchMethodException e) {
                throw new FieldValidationException(COULD_NOT_ACCESS_FIELD_ON_CLASS
                        .formatted(fieldName, clazz.getSimpleName()), e);
            }
        }
        try {
            return method.invoke(object);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new FieldValidationException(COULD_NOT_ACCESS_FIELD_ON_CLASS
                    .formatted(fieldName, clazz.getSimpleName()), e);
        }
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
