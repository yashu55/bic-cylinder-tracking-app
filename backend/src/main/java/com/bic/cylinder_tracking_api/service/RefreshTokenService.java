package com.bic.cylinder_tracking_api.service;

import com.bic.cylinder_tracking_api.dto.auth.refresh_token.RefreshTokenRequestDTO;
import com.bic.cylinder_tracking_api.entity.RefreshToken;
import com.bic.cylinder_tracking_api.entity.User;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface RefreshTokenService {
    RefreshToken findRefreshToken(String token);

    String createRefreshToken(User userDetails, OffsetDateTime createdAt, UUID deviceId, String userAgent, String ipAddress);

    void validateRefreshToken(RefreshToken oldRefreshToken, RefreshTokenRequestDTO refreshToken);

    void deleteOldRefreshToken(RefreshToken oldRefreshToken);
}
