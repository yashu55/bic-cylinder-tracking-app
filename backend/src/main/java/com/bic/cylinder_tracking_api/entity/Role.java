package com.bic.cylinder_tracking_api.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Short id;

    @NotBlank(message = "Role name is required")
    @Size(max = 50, message = "Role name must be less than 50 characters")
    @Column(nullable = false, unique = true)
    private String name;

    @Size(max = 255, message = "Description must be less than 255 characters")
    private String description;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return Objects.equals(id, role.id) && Objects.equals(name, role.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}


