package com.bic.cylinder_tracking_api.controller;


import com.bic.cylinder_tracking_api.dto.auth.login.LoginRequestDTO;
import com.bic.cylinder_tracking_api.dto.auth.refresh_token.RefreshTokenRequestDTO;
import com.bic.cylinder_tracking_api.security.CookieUtil;
import com.bic.cylinder_tracking_api.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final CookieUtil cookieUtil;


    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequestDTO loginRequest) {
        var response = authService.authenticate(loginRequest);
        if(cookieUtil.getSendAsCookie()) {
            HttpHeaders headers = cookieUtil.buildCookieHeaders(response.getAccessToken(), response.getRefreshToken());
            response.setAccessToken(null);
            response.setRefreshToken(null);
            response.setCookieResponse(true);
            return ResponseEntity.ok().headers(headers).body(response);
        }
        else
            return ResponseEntity.ok(response);
    }

    @PostMapping("/refreshaccesstoken")
    public ResponseEntity<?> refreshAccessToken(@Valid @RequestBody RefreshTokenRequestDTO refreshRequest) {
        var response = authService.refreshAccessToken(refreshRequest);

        if(cookieUtil.getSendAsCookie()) {
            HttpHeaders headers = cookieUtil.buildCookieHeaders(response.getAccessToken(), response.getRefreshToken());
            response.setAccessToken(null);
            response.setRefreshToken(null);
            response.setCookieResponse(true);
            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .body(response);
        }
        else
            return ResponseEntity.ok(response);
    }

}

