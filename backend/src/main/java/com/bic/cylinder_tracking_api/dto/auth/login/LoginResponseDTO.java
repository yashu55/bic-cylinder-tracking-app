package com.bic.cylinder_tracking_api.dto.auth.login;

import com.bic.cylinder_tracking_api.dto.MessageResponseDTO;
import com.bic.cylinder_tracking_api.dto.auth.UserDetailsResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class LoginResponseDTO extends MessageResponseDTO {
    private String accessToken;
    private String refreshToken;
    private UserDetailsResponse userDetails;
    private boolean isCookieResponse;
}
