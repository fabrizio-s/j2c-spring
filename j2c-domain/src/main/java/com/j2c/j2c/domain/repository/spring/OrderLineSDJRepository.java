package com.j2c.j2c.domain.repository.spring;

import com.j2c.j2c.domain.entity.OrderLine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface OrderLineSDJRepository
        extends JpaRepository<OrderLine, Long> {

    Page<OrderLine> findAllByOrderId(Long orderId, Pageable pageable);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE OrderLine T SET T.product = null WHERE T.product.id = :productId")
    void dereferenceProduct(Long productId);

}
