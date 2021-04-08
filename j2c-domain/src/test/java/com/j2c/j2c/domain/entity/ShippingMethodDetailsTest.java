package com.j2c.j2c.domain.entity;

import com.j2c.j2c.domain.enums.ShippingMethodType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ShippingMethodDetailsTest {

    @Test
    void new_NullName_ShouldThrowIllegalArgumentException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> ShippingMethodDetails.builder()
                        .name(null)
                        .type(ShippingMethodType.Price)
                        .amount(50L)
                        .build()
        );
    }

    @Test
    void new_NullType_ShouldThrowIllegalArgumentException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> ShippingMethodDetails.builder()
                        .name("awddwa")
                        .type(null)
                        .amount(50L)
                        .build()
        );
    }

    @Test
    void new_NullAmount_ShouldThrowIllegalArgumentException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> ShippingMethodDetails.builder()
                        .name("awddwa")
                        .type(ShippingMethodType.Price)
                        .amount(null)
                        .build()
        );
    }

}
