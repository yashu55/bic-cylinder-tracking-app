package com.bic.cylinder_tracking_api.exception;

import com.bic.cylinder_tracking_api.dto.MessageResponseDTO;
import com.bic.cylinder_tracking_api.dto.enums.ResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(CustomAuthenticationException.class)
    public ResponseEntity<?> handleAuthenticationException(CustomAuthenticationException ex) {
        logger.error("Exception occurred during Authentication: {}", ex.getMessage());
        MessageResponseDTO response = MessageResponseDTO.builder()
                .status(ResponseStatus.FAILURE)
                .errorCode(ex.getStatus().value())
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(ex.getStatus()).body(response);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        logger.error("Exception occurred: {}", ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        MessageResponseDTO response = MessageResponseDTO.builder()
                .status(ResponseStatus.FAILURE)
                .errorCode(HttpStatus.BAD_REQUEST.value())
                .message("Request Validation Failed")
                .errors(errors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<MessageResponseDTO> handleGenericException(Exception ex) {
        logger.error(Arrays.toString(ex.getStackTrace()));
        MessageResponseDTO response = MessageResponseDTO.builder()
                .status(ResponseStatus.FAILURE)
                .errorCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("An unexpected error occurred")
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}

