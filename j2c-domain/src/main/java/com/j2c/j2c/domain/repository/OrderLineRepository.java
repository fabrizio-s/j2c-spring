package com.j2c.j2c.domain.repository;

import com.j2c.j2c.domain.entity.OrderLine;
import com.j2c.j2c.domain.repository.spring.OrderLineSDJRepository;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class OrderLineRepository
        extends BaseRepository<OrderLine, Long> {

    private final OrderLineSDJRepository repository;

    protected OrderLineRepository(final OrderLineSDJRepository repository) {
        super(OrderLine.class, repository);
        this.repository = repository;
    }

    public Page<OrderLine> findAllByOrderId(@NonNull final Long orderId, final Pageable pageable) {
        return repository.findAllByOrderId(orderId, pageable);
    }

}
