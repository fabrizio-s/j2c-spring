package com.j2c.j2c.domain.repository;

import com.j2c.j2c.domain.entity.UserAddress;
import com.j2c.j2c.domain.repository.spring.UserAddressSDJRepository;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class UserAddressRepository
        extends BaseRepository<UserAddress, Long> {

    private final UserAddressSDJRepository repository;

    protected UserAddressRepository(final UserAddressSDJRepository repository) {
        super(UserAddress.class, repository);
        this.repository = repository;
    }

    public Page<UserAddress> findAllByUserId(@NonNull final Long userId, final Pageable pageable) {
        return repository.findAllByUserId(userId, pageable);
    }

}
