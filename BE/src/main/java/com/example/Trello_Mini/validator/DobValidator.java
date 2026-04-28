package com.example.Trello_Mini.validator;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DobValidator implements ConstraintValidator<DobConstraint, LocalDate> {

    private int min;

    @Override
    public void initialize(DobConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        this.min = constraintAnnotation.min();
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (Objects.isNull(value)) {
            // Nên dùng @NotNull thêm để check null, validator này chỉ check khi giá trị hợp lệ có gửi lên
            return true;
        }

        long years = ChronoUnit.YEARS.between(value, LocalDate.now());

        return years >= min;
    }
}
