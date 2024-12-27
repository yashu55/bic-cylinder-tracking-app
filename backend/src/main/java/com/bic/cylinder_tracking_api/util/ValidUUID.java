package com.bic.cylinder_tracking_api.util;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = UUIDValidator.class)  // Link to the validator class
@Target({ ElementType.FIELD, ElementType.PARAMETER }) // Applies to fields and parameters
@Retention(RetentionPolicy.RUNTIME)  // Retain at runtime so it can be accessed via reflection
public @interface ValidUUID {

    String message() default "Invalid UUID format";  // Default error message

    Class<?>[] groups() default {};  // Grouping constraints

    Class<? extends Payload>[] payload() default {};  // Custom metadata
}

