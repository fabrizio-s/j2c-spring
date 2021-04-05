package com.j2c.j2c.domain.repository.spring;

import com.j2c.j2c.domain.entity.OrderFulfillment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderFulfillmentSDJRepository
        extends JpaRepository<OrderFulfillment, Long> {

    Page<OrderFulfillment> findAllByOrderId(Long orderId, Pageable pageable);

}
