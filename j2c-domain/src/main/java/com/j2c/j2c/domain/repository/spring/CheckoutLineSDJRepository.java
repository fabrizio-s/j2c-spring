package com.j2c.j2c.domain.repository.spring;

import com.j2c.j2c.domain.entity.CheckoutLine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CheckoutLineSDJRepository
        extends JpaRepository<CheckoutLine, Long> {

    Page<CheckoutLine> findAllByCheckoutId(Long checkoutId, Pageable pageable);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE CheckoutLine T SET T.product = null WHERE T.product.id = :productId")
    void dereferenceProduct(Long productId);

}
