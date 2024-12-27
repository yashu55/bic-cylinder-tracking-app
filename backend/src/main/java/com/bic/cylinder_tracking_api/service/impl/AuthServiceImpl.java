package com.bic.cylinder_tracking_api.service.impl;


import com.bic.cylinder_tracking_api.dto.auth.UserDetailsResponse;
import com.bic.cylinder_tracking_api.dto.auth.login.LoginRequestDTO;
import com.bic.cylinder_tracking_api.dto.auth.login.LoginResponseDTO;
import com.bic.cylinder_tracking_api.dto.auth.refresh_token.RefreshTokenRequestDTO;
import com.bic.cylinder_tracking_api.dto.auth.refresh_token.RefreshTokenResponseDTO;
import com.bic.cylinder_tracking_api.dto.enums.ResponseStatus;
import com.bic.cylinder_tracking_api.entity.LoginAudit;
import com.bic.cylinder_tracking_api.entity.RefreshToken;
import com.bic.cylinder_tracking_api.entity.User;
import com.bic.cylinder_tracking_api.entity.enums.LoginStatus;
import com.bic.cylinder_tracking_api.exception.CustomAuthenticationException;
import com.bic.cylinder_tracking_api.repository.LoginAuditRepository;
import com.bic.cylinder_tracking_api.repository.UserRepository;
import com.bic.cylinder_tracking_api.security.CustomUserDetails;
import com.bic.cylinder_tracking_api.security.JwtTokenUtil;
import com.bic.cylinder_tracking_api.service.AuthService;
import com.bic.cylinder_tracking_api.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final RefreshTokenService refreshTokenService;
    private final LoginAuditRepository loginAuditRepository;
    private final UserRepository userRepository;

    @Override
    public LoginResponseDTO authenticate(LoginRequestDTO loginRequest) {
        OffsetDateTime timestamp = OffsetDateTime.now(ZoneOffset.UTC);

        try {
            // Perform authentication
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(), loginRequest.getPassword()) );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Get authenticated user's details
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            // Generate tokens
            String accessToken = jwtTokenUtil.generateAccessToken(userDetails, timestamp);
            String refreshToken = refreshTokenService.createRefreshToken(userDetails.getUser(), timestamp, UUID.fromString(loginRequest.getDeviceId()), loginRequest.getUserAgent(), loginRequest.getIpAddress());

            // Login Audit
            logLoginAudit(userDetails.getUser(), LoginStatus.SUCCESS, timestamp, "Login Successful");

            // Build user details response
            UserDetailsResponse userDetailsResponse = UserDetailsResponse.builder()
                    .id(userDetails.getUser().getId())
                    .name(userDetails.getUser().getName())
                    .email(userDetails.getUser().getEmail())
                    .status(userDetails.getUser().getStatus())
                    .createdAt(userDetails.getUser().getCreatedAt())
                    .updatedAt(userDetails.getUser().getUpdatedAt())
                    .deletedAt(userDetails.getUser().getDeletedAt())
                    .roles(userDetails.getUser().getRoles())
                    .build();

            return  LoginResponseDTO.builder()
                    .message("Success Login")
                    .status(ResponseStatus.SUCCESS)
                    .errorCode(0)
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .userDetails(userDetailsResponse)
                    .isCookieResponse(false)
                    .build();

        } catch (BadCredentialsException ex) {
            String msg = "Invalid email or password";
            handleFailedLoginAudit(loginRequest.getEmail(), timestamp, msg);
            throw new CustomAuthenticationException(msg, HttpStatus.UNAUTHORIZED);
        } catch (UsernameNotFoundException ex) {
            String msg = "User not found";
            handleFailedLoginAudit(loginRequest.getEmail(), timestamp, msg);
            throw new CustomAuthenticationException(msg, HttpStatus.UNAUTHORIZED);
        } catch (LockedException ex) {
            String msg = "Your account is locked. Please contact support";
            handleFailedLoginAudit(loginRequest.getEmail(), timestamp, msg);
            throw new CustomAuthenticationException(msg, HttpStatus.FORBIDDEN);
        } catch (DisabledException ex) {
            String msg = "Your account is disabled. Please contact support";
            handleFailedLoginAudit(loginRequest.getEmail(), timestamp, msg);
            throw new CustomAuthenticationException(msg, HttpStatus.FORBIDDEN);
        } catch (AccountExpiredException ex) {
            String msg = "Your account has expired. Please renew your subscription";
            handleFailedLoginAudit(loginRequest.getEmail(), timestamp, msg);
            throw new CustomAuthenticationException(msg, HttpStatus.FORBIDDEN);
        } catch (CredentialsExpiredException ex) {
            String msg = "Your credentials have expired. Please reset your password";
            handleFailedLoginAudit(loginRequest.getEmail(), timestamp, msg);
            throw new CustomAuthenticationException(msg, HttpStatus.FORBIDDEN);
        } catch (Exception ex) {
            String msg = "An unexpected error occurred. Please try again later";
            handleFailedLoginAudit(loginRequest.getEmail(), timestamp, msg);
            throw new CustomAuthenticationException(msg, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public RefreshTokenResponseDTO refreshAccessToken(RefreshTokenRequestDTO refreshRequest) {

        // Validate and get refresh token
        RefreshToken oldRefreshToken;
        try {
            oldRefreshToken = refreshTokenService.findRefreshToken(refreshRequest.getRefreshToken());
        } catch (CustomAuthenticationException e) {
            throw new CustomAuthenticationException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        try{
            refreshTokenService.validateRefreshToken(oldRefreshToken, refreshRequest);
        }catch (CustomAuthenticationException e){
            refreshTokenService.deleteOldRefreshToken(oldRefreshToken);
            throw new CustomAuthenticationException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }


        // Retrieve user details associated with the refresh token
        User user = oldRefreshToken.getUser();
        CustomUserDetails userDetails = new CustomUserDetails(user); // Assuming this wraps your User entity

        // Generate a new access token
        OffsetDateTime createdAt = OffsetDateTime.now(ZoneOffset.UTC);
        String newAccessToken = jwtTokenUtil.generateAccessToken(userDetails, createdAt);
        String newRefreshToken = refreshTokenService.createRefreshToken(user, createdAt,
                UUID.fromString(refreshRequest.getDeviceId()),
                refreshRequest.getUserAgent(), refreshRequest.getIpAddress());

        // Delete old Refresh token
        refreshTokenService.deleteOldRefreshToken(oldRefreshToken);

        // Build the UserDetailsResponse
        UserDetailsResponse userDetailsResponse = UserDetailsResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .deletedAt(user.getDeletedAt())
                .roles(user.getRoles())
                .build();

        // Build and return the response DTO
        return RefreshTokenResponseDTO.builder()
                .status(ResponseStatus.SUCCESS)
                .errorCode(0)
                .message("Access token refreshed successfully")
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .userDetails(userDetailsResponse)
                .isCookieResponse(false)
                .build();
    }




    private void logLoginAudit(User user, LoginStatus status, OffsetDateTime timestamp, String description) {
        LoginAudit audit = new LoginAudit();
        audit.setUser(user);
        audit.setStatus(status);
        audit.setTimestamp(timestamp);
        audit.setDescription(description);
        loginAuditRepository.save(audit);
    }

    private void handleFailedLoginAudit(String email, OffsetDateTime timestamp, String description) {
        User user = userRepository.findByEmail(email).orElse(null);
        logLoginAudit(user, LoginStatus.FAILURE, timestamp, description);
    }


}

