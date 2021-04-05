package com.j2c.j2c.domain.repository;

import com.j2c.j2c.domain.entity.OrderFulfillmentLine;
import com.j2c.j2c.domain.repository.spring.OrderFulfillmentLineSDJRepository;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderFulfillmentLineRepository
        extends BaseRepository<OrderFulfillmentLine, Long> {

    private final OrderFulfillmentLineSDJRepository repository;

    protected OrderFulfillmentLineRepository(final OrderFulfillmentLineSDJRepository repository) {
        super(OrderFulfillmentLine.class, repository);
        this.repository = repository;
    }

    public List<OrderFulfillmentLine> findAllByFulfillmentId(@NonNull final Long fulfillmentId) {
        return repository.findAllByFulfillmentId(fulfillmentId);
    }

    public Page<OrderFulfillmentLine> findAllByFulfillmentId(@NonNull final Long fulfillmentId, final Pageable pageable) {
        return repository.findAllByFulfillmentId(fulfillmentId, pageable);
    }

}
