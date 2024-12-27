package com.bic.cylinder_tracking_api.repository;


import com.bic.cylinder_tracking_api.entity.LoginAudit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginAuditRepository extends JpaRepository<LoginAudit, Long> {
}

