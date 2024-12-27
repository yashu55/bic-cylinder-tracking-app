package com.bic.cylinder_tracking_api.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CookieUtil {

    @Value("${cookie.send-as-cookie}")
    private boolean sendAsCookie;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    @Value("${cookie.http-only}")
    private boolean httpOnly;

    @Value("${cookie.secure}")
    private boolean isSecure;

    @Value("${cookie.path}")
    private String path;

    @Value("${cookie.same-site}")
    private String sameSite;

    @Value("${cookie.domain}")
    private String domain;

    public boolean getSendAsCookie(){
        return sendAsCookie;
    }

    public HttpHeaders buildCookieHeaders(String accessToken, String refreshToken){
        HttpHeaders headers = new HttpHeaders();
        createCookies(accessToken, refreshToken)
                .forEach(cookie -> headers.add(HttpHeaders.SET_COOKIE, cookie.toString()));
       return headers;
    }

    private List<ResponseCookie> createCookies(String accessToken, String refreshToken) {
        return List.of(
                createHttpOnlyCookie("access_token", accessToken, accessTokenExpiration),
                createHttpOnlyCookie("refresh_token", refreshToken, refreshTokenExpiration)
        );
    }

    private ResponseCookie createHttpOnlyCookie(String name, String value, Long expiration) {
        return ResponseCookie.from(name, value)
                .httpOnly(httpOnly)
                .secure(isSecure)
                .path(path)
                .domain(domain)
//                .sameSite(sameSite)
                .maxAge(expiration/1000) // Example expiry, adjust as necessary
                .build();
    }
}
