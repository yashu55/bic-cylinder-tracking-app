package com.bic.cylinder_tracking_api.repository;


import com.bic.cylinder_tracking_api.entity.RefreshToken;
import com.bic.cylinder_tracking_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByToken(String token);

    @Modifying
    long deleteByUser(User user);


}

