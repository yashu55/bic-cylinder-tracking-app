package com.bic.cylinder_tracking_api.entity;


import com.bic.cylinder_tracking_api.entity.enums.LoginStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.Objects;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
@Entity
@Table(name = "login_audit")
public class LoginAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // EAGER fetch since user info is needed frequently with login audit data
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    @NotNull
    @Enumerated(EnumType.STRING)
    private LoginStatus status;

    @Column(nullable = false)
    private OffsetDateTime timestamp;

    private String description;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        LoginAudit that = (LoginAudit) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}


