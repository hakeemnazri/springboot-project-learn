package com.spring_boot.ecommerce.repositories;

import com.spring_boot.ecommerce.model.AppRole;
import com.spring_boot.ecommerce.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(AppRole appRole);
}
