package com.bic.cylinder_tracking_api.util;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.UUID;

public class UUIDValidator implements ConstraintValidator<ValidUUID, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;  // Handle null or blank case separately if needed (optional)
        }

        try {
            UUID.fromString(value);  // Try to parse the string to a UUID
            return true;  // Valid UUID
        } catch (IllegalArgumentException e) {
            return false;  // Invalid UUID format
        }
    }

    @Override
    public void initialize(ValidUUID constraintAnnotation) {
        // You can add initialization logic here if needed
    }
}

