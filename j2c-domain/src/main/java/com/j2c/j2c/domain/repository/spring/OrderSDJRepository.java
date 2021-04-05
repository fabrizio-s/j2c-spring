package com.j2c.j2c.domain.repository.spring;

import com.j2c.j2c.domain.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface OrderSDJRepository
        extends JpaRepository<Order, Long> {

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE Order T SET T.customer = null WHERE T.customer.id = :customerId")
    void dereferenceCustomer(Long customerId);

    Page<Order> findAllByCustomerId(Long customerId, Pageable pageable);

}
