package com.bic.cylinder_tracking_api.dto.auth.refresh_token;

import com.bic.cylinder_tracking_api.util.ValidUUID;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RefreshTokenRequestDTO {

    private String refreshToken;

    @NotNull
    @ValidUUID
    private String deviceId;

    @NotNull
    private Long userId;
    private String ipAddress;
    private String userAgent;
}
