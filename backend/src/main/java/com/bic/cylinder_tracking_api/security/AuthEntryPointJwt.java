package com.bic.cylinder_tracking_api.security;

import com.bic.cylinder_tracking_api.dto.MessageResponseDTO;
import com.bic.cylinder_tracking_api.dto.enums.ResponseStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        logger.error("Unauthorized error: {}", authException.getMessage());

        // Set response properties
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // Prepare custom response
        MessageResponseDTO messageResponse = MessageResponseDTO.builder()
                .status(ResponseStatus.FAILURE) // Assuming FAILURE indicates an unsuccessful operation
                .errorCode(HttpServletResponse.SC_UNAUTHORIZED) // Use 401 as the error code for unauthorized access
                .message(authException.getMessage())
                .errors(Map.of(
                        "error", "Unauthorized",
                        "path", request.getServletPath()
                ))
                .build();

        // Write the response using ObjectMapper
        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), messageResponse);


    }
}
