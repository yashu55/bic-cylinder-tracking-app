package com.bic.cylinder_tracking_api.dto.auth.login;


import com.bic.cylinder_tracking_api.util.ValidUUID;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoginRequestDTO {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;

    @NotNull
    @ValidUUID
    private String deviceId;
    private String ipAddress;
    private String userAgent;
}

