package com.bic.cylinder_tracking_api.security;


import com.bic.cylinder_tracking_api.exception.CustomAuthenticationException;
import com.bic.cylinder_tracking_api.security.enums.KeyType;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtTokenUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenUtil.class);

    @Value("${jwt.access-token-secret}")
    private String accessTokenSecret;

    @Value("${jwt.refresh-token-secret}")
    private String refreshTokenSecret;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    public String generateAccessToken(CustomUserDetails userDetails, OffsetDateTime createdAt) {
        Date issuedAt = Date.from(createdAt.toInstant());
        Date expiration = new Date(issuedAt.getTime() + accessTokenExpiration);
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("type", KeyType.ACCESS_TOKEN)
                .claim("user-id", userDetails.getUser().getId())
                .claim("user-status", userDetails.getUser().getStatus())
                .claim("user-roles", userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS512, key(KeyType.ACCESS_TOKEN))
                .compact();
    }

    public String generateRefreshToken(String id, OffsetDateTime createdAt) {
        Date issuedAt = Date.from(createdAt.toInstant());
        Date expiration = new Date(issuedAt.getTime() + refreshTokenExpiration);
        return Jwts.builder()
                .setSubject(id)
                .claim("type", KeyType.REFRESH_TOKEN)
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS512, key(KeyType.REFRESH_TOKEN))
                .compact();
    }

    private Key key(KeyType keyType) {
        return Keys.hmacShaKeyFor(keyType == KeyType.ACCESS_TOKEN ?
                accessTokenSecret.getBytes(StandardCharsets.UTF_8)
                : refreshTokenSecret.getBytes(StandardCharsets.UTF_8)
        );
    }

    public String getSubjectFromAccessToken(String authToken) {
        return Jwts.parserBuilder().setSigningKey(key(KeyType.ACCESS_TOKEN)).build()
                .parseClaimsJws(authToken).getBody().getSubject();
    }

    public boolean validateAccessToken(String authToken) {
        try {
            // Parse the JWT and extract claims
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key(KeyType.ACCESS_TOKEN)) // Provide the appropriate signing key
                    .build()
                    .parseClaimsJws(authToken)
                    .getBody();
            // Check the "type" claim
            String tokenType = claims.get("type", String.class);
            if (KeyType.ACCESS_TOKEN != KeyType.valueOf(tokenType)) {
                logger.error("Invalid token type: expected 'ACCESS_TOKEN' but found '{}'", tokenType);
                return false;
            }
            return true; // Valid token and type is "access"
        } catch (MalformedJwtException | SecurityException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is invalid: {}", e.getMessage());
        }
        return false; // Token validation failed
    }

    public void validateJWTRefreshToken(String refreshToken) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key(KeyType.REFRESH_TOKEN))
                    .build()
                    .parseClaimsJws(refreshToken)
                    .getBody();

            // Check if the token type is 'Refresh_Token'
            String tokenType = claims.get("type", String.class);
            if (KeyType.REFRESH_TOKEN != KeyType.valueOf(tokenType)) {
                logger.error("Invalid token type: expected 'REFRESH_TOKEN' but found '{}'", tokenType);
                throw new IllegalArgumentException("Invalid token type: expected 'REFRESH_TOKEN' but found: " + tokenType);
            }
        }
        catch (ExpiredJwtException e) {
            logger.error("Refresh token is expired: {}", e.getMessage());
            throw new CustomAuthenticationException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch (MalformedJwtException | SecurityException | UnsupportedJwtException | IllegalArgumentException e) {
            logger.error("Invalid Refresh token: {}", e.getMessage());
            throw new CustomAuthenticationException(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e){
            logger.error("Error in Refresh token: {}", e.getMessage());
            throw new CustomAuthenticationException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}

