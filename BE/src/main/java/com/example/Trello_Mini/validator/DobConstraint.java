package com.example.Trello_Mini.validator;

import java.lang.annotation.*;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {DobValidator.class})
@Documented
public @interface DobConstraint {

    String message() default "Invalid Date of Birth";

    int min() default 18;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
