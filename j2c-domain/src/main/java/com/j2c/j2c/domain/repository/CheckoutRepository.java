package com.j2c.j2c.domain.repository;

import com.j2c.j2c.domain.entity.Checkout;
import com.j2c.j2c.domain.repository.spring.CheckoutSDJRepository;
import org.springframework.stereotype.Repository;

@Repository
public class CheckoutRepository
        extends BaseRepository<Checkout, Long> {

    protected CheckoutRepository(final CheckoutSDJRepository repository) {
        super(Checkout.class, repository);
    }

}
