package com.j2c.j2c.domain.entity;

import com.j2c.j2c.domain.enums.OrderStatus;
import com.j2c.j2c.domain.exception.DomainException;
import com.j2c.j2c.domain.test.MockEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderFulfillmentLineTest {

    private OrderFulfillmentLine fulfillmentLine;

    @BeforeEach
    void setUp() {
        final Order order = MockEntity.order().id(1L).status(OrderStatus.PROCESSING).build();
        final OrderFulfillment fulfillment = MockEntity.orderFulfillment().id(1L).order(order).completed(false).build();
        final OrderLine orderLine = MockEntity.orderLine()
                .id(1L)
                .order(order)
                .shippingRequired(true)
                .quantity(100)
                .reservedQuantity(25)
                .fulfilledQuantity(15)
                .build();
        fulfillmentLine = MockEntity.orderFulfillmentLine()
                .id(1L)
                .fulfillment(fulfillment)
                .orderLine(orderLine)
                .quantity(3)
                .build();
    }

    @Test
    void setQuantity_NegativeQuantity_ShouldThrowDomainException() {
        assertThrows(
                DomainException.class,
                () -> fulfillmentLine.setQuantity(-1)
        );
    }

    @Test
    void setQuantity_PositiveQuantity_ShouldUpdateOrderLineReservedQuantity() {
        final OrderLine orderLine = fulfillmentLine.getOrderLine();
        final int r = orderLine.getReservedQuantity();
        final int c = fulfillmentLine.getQuantity();

        fulfillmentLine.setQuantity(25);

        assertEquals(orderLine.getReservedQuantity() - r, fulfillmentLine.getQuantity() - c);
    }

    @Test
    void setQuantity_AssignableQuantityPlusCurrentQuantity_ShouldUpdateOrderLineReservedQuantity() {
        final OrderLine orderLine = fulfillmentLine.getOrderLine();
        final int r = orderLine.getReservedQuantity();
        final int c = fulfillmentLine.getQuantity();

        fulfillmentLine.setQuantity(orderLine.getAssignableQuantity() + c);

        assertEquals(orderLine.getReservedQuantity() - r, fulfillmentLine.getQuantity() - c);
    }

    @Test
    void setQuantity_QuantityGreaterThanAssignableQuantityPlusCurrentQuantity_ShouldThrowDomainException() {
        final OrderLine orderLine = fulfillmentLine.getOrderLine();
        final int c = fulfillmentLine.getQuantity();

        assertThrows(
                DomainException.class,
                () -> fulfillmentLine.setQuantity(orderLine.getAssignableQuantity() + c + 1)
        );
    }

}
