package com.bic.cylinder_tracking_api.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@EqualsAndHashCode(callSuper = true)
@Data
public class CustomAuthenticationException extends RuntimeException {
    private final HttpStatus status;

    public CustomAuthenticationException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

}
