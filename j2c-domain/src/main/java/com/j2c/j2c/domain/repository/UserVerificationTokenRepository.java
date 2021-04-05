package com.j2c.j2c.domain.repository;

import com.j2c.j2c.domain.entity.UserVerificationToken;
import com.j2c.j2c.domain.exception.EntityDoesNotExistException;
import com.j2c.j2c.domain.repository.spring.UserVerificationTokenSDJRepository;
import lombok.NonNull;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class UserVerificationTokenRepository
        extends BaseRepository<UserVerificationToken, UUID> {

    private final UserVerificationTokenSDJRepository repository;

    protected UserVerificationTokenRepository(final UserVerificationTokenSDJRepository repository) {
        super(UserVerificationToken.class, repository);
        this.repository = repository;
    }

    public UserVerificationToken findByUserId(@NonNull final Long userId) {
        return repository.findByUserId(userId)
                .orElseThrow(() -> new EntityDoesNotExistException(String.format("Verification Token for user with id '%s' does not exist", userId), this.type));
    }

}
