package com.j2c.j2c.domain.repository.spring;

import com.j2c.j2c.domain.entity.ShippingMethod;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ShippingMethodSDJRepositoryTest {

    @Autowired
    private ShippingMethodSDJRepository repository;

    @Autowired
    private CheckoutSDJRepository checkoutRepository;

    @Test
    void findAllForCheckoutPriceOrWeight() {
        final Page<ShippingMethod> page = repository.findAllInZoneByPriceOrWeight(1L, 750L, 5000L, PageRequest.of(0, 20));
        final List<ShippingMethod> shippingMethods = page.getContent();
        assertEquals(3, shippingMethods.size());
    }

}
