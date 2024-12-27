package com.bic.cylinder_tracking_api.service;

import com.bic.cylinder_tracking_api.dto.auth.login.LoginRequestDTO;
import com.bic.cylinder_tracking_api.dto.auth.login.LoginResponseDTO;
import com.bic.cylinder_tracking_api.dto.auth.refresh_token.RefreshTokenRequestDTO;
import com.bic.cylinder_tracking_api.dto.auth.refresh_token.RefreshTokenResponseDTO;

public interface AuthService {
    LoginResponseDTO authenticate(LoginRequestDTO loginRequest);

    RefreshTokenResponseDTO refreshAccessToken(RefreshTokenRequestDTO refreshRequest);


}
