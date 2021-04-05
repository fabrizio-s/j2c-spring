package com.j2c.j2c.domain.entity;

import com.j2c.j2c.domain.enums.OrderStatus;
import com.j2c.j2c.domain.exception.DomainException;
import com.j2c.j2c.domain.test.MockEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

class OrderFulfillmentTest {

    private OrderFulfillment fulfillment;
    private MockEntity.MockOrderLineBuilder orderLineBuilder;
    private MockEntity.MockOrderFulfillmentLineBuilder fulfillmentLineBuilder;

    @BeforeEach
    void setUp() {
        final Order order = MockEntity.order().id(1L).status(OrderStatus.PROCESSING).build();
        fulfillment = MockEntity.orderFulfillment().id(1L).order(order).completed(false).build();
        orderLineBuilder = MockEntity.orderLine().id(1L).order(order).shippingRequired(true).quantity(100).reservedQuantity(25).fulfilledQuantity(15);
        fulfillmentLineBuilder = MockEntity.orderFulfillmentLine().id(1L).fulfillment(fulfillment).quantity(5);
    }

    @Test
    void addLine_NegativeQuantity_ShouldThrowDomainException() {
        final OrderLine orderLine = orderLineBuilder.build();

        assertThrows(
                DomainException.class,
                () -> fulfillment.newLine()
                        .orderLine(orderLine)
                        .quantity(-5)
                        .addLine()
        );
    }

    @Test
    void addLine_QuantityGreaterThanAssignableQuantity_ShouldThrowDomainException() {
        final OrderLine orderLine = orderLineBuilder.build();

        assertThrows(
                DomainException.class,
                () -> fulfillment.newLine()
                        .orderLine(orderLine)
                        .quantity(orderLine.getAssignableQuantity() + 1)
                        .addLine()
        );
    }

    @Test
    void addLine_PositiveQuantity_ShouldAddToReservedQuantity() {
        final OrderLine orderLine = orderLineBuilder.build();
        final int x = orderLine.getReservedQuantity();

        final OrderFulfillmentLine fulfillmentLine = fulfillment.newLine()
                .orderLine(orderLine)
                .quantity(7)
                .addLine();

        assertEquals(orderLine.getReservedQuantity(), fulfillmentLine.getQuantity() + x);
    }

    @Test
    void addLine_AssignableQuantity_ShouldAddToReservedQuantity() {
        final OrderLine orderLine = orderLineBuilder.build();
        final int x = orderLine.getReservedQuantity();

        final OrderFulfillmentLine fulfillmentLine = fulfillment.newLine()
                .orderLine(orderLine)
                .quantity(orderLine.getAssignableQuantity())
                .addLine();

        assertEquals(orderLine.getReservedQuantity(), fulfillmentLine.getQuantity() + x);
    }

    @Test
    void removeLine_FulfillmentIsCompleted_ShouldThrowDomainException() {
        final OrderLine orderLine = orderLineBuilder.build();
        final OrderFulfillmentLine fulfillmentLine = fulfillmentLineBuilder
                .orderLine(orderLine)
                .quantity(7)
                .build();

        setField(fulfillment, "completed", true);

        assertThrows(
                DomainException.class,
                () -> fulfillment.removeLine(fulfillmentLine)
        );
    }

    @Test
    void removeLine_NegativeQuantity_ShouldDoNothing() {
        final OrderLine orderLine = orderLineBuilder.build();
        final OrderFulfillmentLine fulfillmentLine = fulfillmentLineBuilder
                .orderLine(orderLine)
                .quantity(-3)
                .build();
        final int x = orderLine.getReservedQuantity();

        final OrderLine updatedOrderLine = fulfillment.removeLine(fulfillmentLine);

        assertEquals(updatedOrderLine.getReservedQuantity(), x);
    }

    @Test
    void removeLine_PositiveQuantity_ShouldSubtractFromReservedQuantity() {
        final OrderLine orderLine = orderLineBuilder.build();
        final OrderFulfillmentLine fulfillmentLine = fulfillmentLineBuilder
                .orderLine(orderLine)
                .quantity(7)
                .build();
        final int x = orderLine.getReservedQuantity();

        final OrderLine updatedOrderLine = fulfillment.removeLine(fulfillmentLine);

        assertEquals(updatedOrderLine.getReservedQuantity(), x - fulfillmentLine.getQuantity());
    }

    @Test
    void removeLine_ReservedQuantity_ShouldSubtractFromReservedQuantity() {
        final OrderLine orderLine = orderLineBuilder.build();
        final OrderFulfillmentLine fulfillmentLine = fulfillmentLineBuilder
                .orderLine(orderLine)
                .quantity(orderLine.getReservedQuantity())
                .build();
        final int x = orderLine.getReservedQuantity();

        final OrderLine updatedOrderLine = fulfillment.removeLine(fulfillmentLine);

        assertEquals(updatedOrderLine.getReservedQuantity(), x - fulfillmentLine.getQuantity());
    }

    @Test
    void removeLine_QuantityGreaterThanReservedQuantity_ShouldSetReservedQuantityToZero() {
        final OrderLine orderLine = orderLineBuilder.build();
        final OrderFulfillmentLine fulfillmentLine = fulfillmentLineBuilder
                .orderLine(orderLine)
                .quantity(orderLine.getReservedQuantity() + 1)
                .build();

        final OrderLine updatedOrderLine = fulfillment.removeLine(fulfillmentLine);

        assertEquals(updatedOrderLine.getReservedQuantity(), 0);
    }

    @Test
    void complete_NegativeQuantity_ShouldNotChangeFulfilledQuantity() {
        final OrderLine orderLine = orderLineBuilder.build();
        fulfillmentLineBuilder
                .orderLine(orderLine)
                .quantity(-1)
                .build();
        final int x = orderLine.getFulfilledQuantity();

        fulfillment.complete();

        assertEquals(orderLine.getFulfilledQuantity(), x);
    }

    @Test
    void complete_PositiveQuantity_ShouldAddToFulfilledQuantity() {
        final OrderLine orderLine = orderLineBuilder.build();
        final OrderFulfillmentLine fulfillmentLine = fulfillmentLineBuilder
                .orderLine(orderLine)
                .quantity(5)
                .build();
        final int x = orderLine.getFulfilledQuantity();

        fulfillment.complete();

        assertEquals(orderLine.getFulfilledQuantity(), x + fulfillmentLine.getQuantity());
    }

    @Test
    void complete_OrderLineQuantityMinusFulfilledQuantity_ShouldAddToFulfilledQuantity() {
        final OrderLine orderLine = orderLineBuilder.build();
        final OrderFulfillmentLine fulfillmentLine = fulfillmentLineBuilder
                .orderLine(orderLine)
                .quantity(orderLine.getQuantity() - orderLine.getFulfilledQuantity())
                .build();
        final int x = orderLine.getFulfilledQuantity();

        fulfillment.complete();

        assertEquals(orderLine.getFulfilledQuantity(), x + fulfillmentLine.getQuantity());
    }

    @Test
    void complete_QuantityIsGreaterThanOrderLineQuantityMinusFulfilledQuantity_ShouldThrowDomainException() {
        final OrderLine orderLine = orderLineBuilder.build();
        fulfillmentLineBuilder
                .orderLine(orderLine)
                .quantity(orderLine.getQuantity() - orderLine.getFulfilledQuantity() + 1)
                .build();

        assertThrows(
                DomainException.class,
                () -> fulfillment.complete()
        );
    }

}
