package com.j2c.j2c.domain.repository.spring;

import com.j2c.j2c.domain.entity.Checkout;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheckoutSDJRepository
        extends JpaRepository<Checkout, Long> {
}
