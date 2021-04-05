package com.j2c.j2c.domain.repository.spring;

import com.j2c.j2c.domain.entity.OrderFulfillmentLine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderFulfillmentLineSDJRepository
        extends JpaRepository<OrderFulfillmentLine, Long> {

    List<OrderFulfillmentLine> findAllByFulfillmentId(Long fulfillmentId);

    Page<OrderFulfillmentLine> findAllByFulfillmentId(Long fulfillmentId, Pageable pageable);

}
