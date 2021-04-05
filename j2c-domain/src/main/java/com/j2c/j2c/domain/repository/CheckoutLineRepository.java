package com.j2c.j2c.domain.repository;

import com.j2c.j2c.domain.entity.CheckoutLine;
import com.j2c.j2c.domain.repository.spring.CheckoutLineSDJRepository;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class CheckoutLineRepository
        extends BaseRepository<CheckoutLine, Long> {

    private final CheckoutLineSDJRepository repository;

    protected CheckoutLineRepository(final CheckoutLineSDJRepository repository) {
        super(CheckoutLine.class, repository);
        this.repository = repository;
    }

    public Page<CheckoutLine> findAllByCheckoutId(@NonNull final Long checkoutId, final Pageable pageable) {
        return repository.findAllByCheckoutId(checkoutId, pageable);
    }

}
