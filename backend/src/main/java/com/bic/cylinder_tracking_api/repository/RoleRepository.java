package com.bic.cylinder_tracking_api.repository;


import com.bic.cylinder_tracking_api.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Short> {
    Optional<Role> findByName(String name);
}

