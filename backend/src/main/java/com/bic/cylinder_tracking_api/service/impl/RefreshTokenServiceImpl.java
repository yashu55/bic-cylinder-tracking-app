package com.bic.cylinder_tracking_api.service.impl;

import com.bic.cylinder_tracking_api.dto.auth.refresh_token.RefreshTokenRequestDTO;
import com.bic.cylinder_tracking_api.entity.RefreshToken;
import com.bic.cylinder_tracking_api.entity.User;
import com.bic.cylinder_tracking_api.exception.CustomAuthenticationException;
import com.bic.cylinder_tracking_api.repository.RefreshTokenRepository;
import com.bic.cylinder_tracking_api.security.JwtTokenUtil;
import com.bic.cylinder_tracking_api.service.RefreshTokenService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenUtil jwtTokenUtil;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;


    @Override
    @Transactional
    public String createRefreshToken(User user, OffsetDateTime createdAt, UUID deviceId, String userAgent, String ipAddress) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setCreatedAt(createdAt);
        refreshToken.setExpiresAt(createdAt.plusNanos(refreshTokenExpiration * 1_000_000));
        String token = jwtTokenUtil.generateRefreshToken(refreshToken.getId().toString(), createdAt);
        refreshToken.setToken(token);
        refreshToken.setDeviceId(deviceId);
        refreshToken.setIpAddress(ipAddress);
        refreshToken.setUserAgent(userAgent);

        refreshTokenRepository.save(refreshToken);
        return token;
    }

    @Transactional
    @Override
    public RefreshToken findRefreshToken(String token) {
        return refreshTokenRepository
                .findByToken(token)
                .orElseThrow(() -> new CustomAuthenticationException("Refresh token not found", HttpStatus.BAD_REQUEST)
                );
    }

    @Override
    @Transactional
    public void deleteOldRefreshToken(RefreshToken oldRefreshToken) {

        if (oldRefreshToken.getRevokedAt() != null)
            return;

        refreshTokenRepository.deleteById(oldRefreshToken.getId());
        refreshTokenRepository.flush();
    }

    @Transactional
    @Override
    public void validateRefreshToken(RefreshToken oldRefreshToken, RefreshTokenRequestDTO refreshTokenRequest) {
        // Validate JWT structure and signature first
        jwtTokenUtil.validateJWTRefreshToken(refreshTokenRequest.getRefreshToken());
        // Fetch the token from the database

        if (oldRefreshToken.getRevokedAt() != null)
            throw new CustomAuthenticationException("Revoked refresh token", HttpStatus.BAD_REQUEST);

        if (oldRefreshToken.getExpiresAt().isBefore(OffsetDateTime.now(ZoneOffset.UTC)))
            throw new CustomAuthenticationException("Expired refresh token", HttpStatus.BAD_REQUEST);

        if (!oldRefreshToken.getDeviceId().toString().equals(refreshTokenRequest.getDeviceId()))
            throw new CustomAuthenticationException("Invalid refresh token. Device Id mismatch", HttpStatus.BAD_REQUEST);

        if (!Objects.equals(oldRefreshToken.getUser().getId(), refreshTokenRequest.getUserId()))
            throw new CustomAuthenticationException("Invalid refresh token. User Id mismatch", HttpStatus.BAD_REQUEST);

    }



//
//    @Transactional
//    public void revokeRefreshToken(String token) {
//        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
//                .orElseThrow(() -> new CustomAuthenticationException("Refresh token not found", HttpStatus.NOT_FOUND));
//        refreshToken.setRevokedAt(ZonedDateTime.now());
//        refreshTokenRepository.save(refreshToken);
//    }
//
//    @Transactional
//    public void deleteAllRefreshTokensByUser(User user) {
//        refreshTokenRepository.deleteByUser(user);
//    }

//    @Override
//    @Transactional
//    public void deleteRefreshToken(RefreshToken refreshToken){
//        refreshTokenRepository.delete(refreshToken);
//        refreshTokenRepository.flush();
//    }

}
