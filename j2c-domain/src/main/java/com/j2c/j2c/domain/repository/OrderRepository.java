package com.j2c.j2c.domain.repository;

import com.j2c.j2c.domain.entity.Order;
import com.j2c.j2c.domain.repository.spring.OrderSDJRepository;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class OrderRepository
        extends BaseRepository<Order, Long> {

    private final OrderSDJRepository repository;

    protected OrderRepository(final OrderSDJRepository repository) {
        super(Order.class, repository);
        this.repository = repository;
    }

    public Page<Order> findAllByCustomerId(@NonNull final Long customerId, final Pageable pageable) {
        return repository.findAllByCustomerId(customerId, pageable);
    }

}
