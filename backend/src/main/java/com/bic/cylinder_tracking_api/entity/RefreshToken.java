package com.bic.cylinder_tracking_api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@ToString
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank(message = "Token cannot be blank")
    @Column(nullable = false, unique = true)
    private String token;

    @NotNull
    @Column(columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime expiresAt;

    @Column(columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime  createdAt;

    @Column(columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime  revokedAt;

    @NotNull
    @Column(nullable = false)
    private UUID deviceId;

    private String ipAddress;

    private String userAgent;

    public RefreshToken() {
        this.id = UUID.randomUUID();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RefreshToken that = (RefreshToken) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}


