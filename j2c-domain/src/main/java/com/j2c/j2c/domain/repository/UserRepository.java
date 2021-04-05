package com.j2c.j2c.domain.repository;

import com.j2c.j2c.domain.entity.User;
import com.j2c.j2c.domain.exception.EntityDoesNotExistException;
import com.j2c.j2c.domain.repository.spring.OrderSDJRepository;
import com.j2c.j2c.domain.repository.spring.UserSDJRepository;
import com.j2c.j2c.domain.repository.spring.UserVerificationTokenSDJRepository;
import lombok.NonNull;
import org.springframework.stereotype.Repository;

import static com.j2c.j2c.domain.util.J2cUtils.optional;

@Repository
public class UserRepository
        extends BaseRepository<User, Long> {

    private final UserSDJRepository repository;
    private final UserVerificationTokenSDJRepository verificationTokenRepository;
    private final OrderSDJRepository orderRepository;

    protected UserRepository(
            final UserSDJRepository repository,
            final UserVerificationTokenSDJRepository verificationTokenRepository,
            final OrderSDJRepository orderRepository
    ) {
        super(User.class, repository);
        this.repository = repository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.orderRepository = orderRepository;
    }

    public boolean existsByEmail(@NonNull final String email) {
        return repository.existsByEmail(email);
    }

    public User findByEmail(@NonNull final String email) {
        return repository.findByEmail(email)
                .orElseThrow(() -> new EntityDoesNotExistException(String.format("User with email '%s' does not exist", email), type));
    }

    @Override
    public void remove(final User user) {
        optional(user)
                .map(User::getId)
                .ifPresent(userId -> {
                    verificationTokenRepository.findByUserId(userId)
                            .ifPresent(verificationTokenRepository::delete);
                    orderRepository.dereferenceCustomer(userId);
                });
        super.remove(user);
    }

}
