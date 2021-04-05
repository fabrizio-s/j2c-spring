package com.j2c.j2c.domain.repository.spring;

import com.j2c.j2c.domain.entity.UserVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserVerificationTokenSDJRepository
        extends JpaRepository<UserVerificationToken, UUID> {

    Optional<UserVerificationToken> findByUserId(Long userId);

}
