package com.j2c.j2c.domain.repository;

import com.j2c.j2c.domain.entity.OrderFulfillment;
import com.j2c.j2c.domain.repository.spring.OrderFulfillmentSDJRepository;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class OrderFulfillmentRepository
        extends BaseRepository<OrderFulfillment, Long> {

    private final OrderFulfillmentSDJRepository repository;

    protected OrderFulfillmentRepository(final OrderFulfillmentSDJRepository repository) {
        super(OrderFulfillment.class, repository);
        this.repository = repository;
    }

    public Page<OrderFulfillment> findAllByOrderId(@NonNull final Long orderId, final Pageable pageable) {
        return repository.findAllByOrderId(orderId, pageable);
    }

}
