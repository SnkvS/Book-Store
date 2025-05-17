package com.senkiv.bookstore.repository;

import com.senkiv.bookstore.model.Role;
import com.senkiv.bookstore.model.Role.RoleName;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    boolean existsRoleByRoleName(RoleName roleName);

    Optional<Role> findByRoleName(RoleName roleName);
}
