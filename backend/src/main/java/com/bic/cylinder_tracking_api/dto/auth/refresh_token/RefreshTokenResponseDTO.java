package com.bic.cylinder_tracking_api.dto.auth.refresh_token;

import com.bic.cylinder_tracking_api.dto.MessageResponseDTO;
import com.bic.cylinder_tracking_api.dto.auth.UserDetailsResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class RefreshTokenResponseDTO extends MessageResponseDTO {
    private String accessToken;
    private String refreshToken;
    private UserDetailsResponse userDetails;
    private boolean isCookieResponse;

}
