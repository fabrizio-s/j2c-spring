package com.j2c.j2c.domain.repository.spring;

import com.j2c.j2c.domain.entity.Role;
import com.j2c.j2c.domain.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleSDJRepository
        extends JpaRepository<Role, Long> {

    Optional<Role> findByType(RoleType type);

}
