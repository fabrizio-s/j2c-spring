package com.j2c.j2c.domain.entity;

import com.j2c.j2c.domain.test.MockEntity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderLineTest {

    @Test
    void isFulfilled_FulfilledQuantityIsLessThanQuantity_ShouldReturnFalse() {
        final OrderLine orderLine = MockEntity.orderLine()
                .quantity(10)
                .fulfilledQuantity(7)
                .build();

        assertFalse(orderLine.isFulfilled());
    }

    @Test
    void isFulfilled_FulfilledQuantityIsEqualToQuantity_ShouldReturnTrue() {
        final OrderLine orderLine = MockEntity.orderLine()
                .quantity(10)
                .fulfilledQuantity(10)
                .build();

        assertTrue(orderLine.isFulfilled());
    }

    @Test
    void isFulfilled_FulfilledQuantityIsGreaterThanQuantity_ShouldReturnTrue() {
        final OrderLine orderLine = MockEntity.orderLine()
                .quantity(10)
                .fulfilledQuantity(13)
                .build();

        assertTrue(orderLine.isFulfilled());
    }

    @Test
    void getAssignableQuantity_HappyPath_ShouldEqualQuantityMinusFulfilledMinusReserved() {
        final OrderLine orderLine = MockEntity.orderLine()
                .quantity(10)
                .fulfilledQuantity(3)
                .reservedQuantity(2)
                .build();

        assertEquals(
                orderLine.getQuantity() - orderLine.getFulfilledQuantity() - orderLine.getReservedQuantity(),
                orderLine.getAssignableQuantity()
        );
    }

    @Test
    void getAssignableQuantity_FulfilledAndReservedQuantitiesAreGreaterThanQuantity_ShouldReturn0() {
        final OrderLine orderLine = MockEntity.orderLine()
                .quantity(10)
                .fulfilledQuantity(523173)
                .reservedQuantity(856735)
                .build();

        assertEquals(
                0,
                orderLine.getAssignableQuantity()
        );
    }

}
