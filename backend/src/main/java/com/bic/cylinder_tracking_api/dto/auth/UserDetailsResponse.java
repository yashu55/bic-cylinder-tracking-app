package com.bic.cylinder_tracking_api.dto.auth;

import com.bic.cylinder_tracking_api.entity.Role;
import com.bic.cylinder_tracking_api.entity.enums.UserStatus;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.Set;

@Data
@Builder
public class UserDetailsResponse {
    private Long id;
    private String name;
    private String email;
    private UserStatus status;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private OffsetDateTime deletedAt;
    private Set<Role> roles;
}
