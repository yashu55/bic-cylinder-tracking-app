package com.bic.cylinder_tracking_api.dto;

import com.bic.cylinder_tracking_api.dto.enums.ResponseStatus;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.Map;

@Data
@SuperBuilder
public class MessageResponseDTO {

    private ResponseStatus status;
    private int errorCode;
    private String message;
    private Map<String, String> errors;
}
