package com.j2c.j2c.domain.entity;

import com.j2c.j2c.domain.enums.MassUnit;
import com.j2c.j2c.domain.enums.OrderStatus;
import com.j2c.j2c.domain.enums.ShippingMethodType;
import com.j2c.j2c.domain.test.MockEntity;
import com.neovisionaries.i18n.CurrencyCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static com.j2c.j2c.domain.test.TestUtils.nextObject;
import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderTest {

    private Order order;
    private MockEntity.MockOrderLineBuilder orderLineBuilder;
    private MockEntity.MockOrderFulfillmentLineBuilder incompleteFulfillmentLineBuilder;
    private MockEntity.MockOrderFulfillmentLineBuilder completedFulfillmentLineBuilder;

    @BeforeEach
    void setUp() {
        order = MockEntity.order().status(OrderStatus.PARTIALLY_FULFILLED).previousStatus(OrderStatus.PROCESSING).build();
        final int fulfilledQuantity = 31;
        final int reservedQuantity = 27;

        orderLineBuilder = MockEntity.orderLine().order(order).shippingRequired(true).quantity(100).fulfilledQuantity(fulfilledQuantity).reservedQuantity(reservedQuantity);

        final OrderFulfillment incompleteFulfillment = MockEntity.orderFulfillment().order(order).completed(false).build();
        incompleteFulfillmentLineBuilder = MockEntity.orderFulfillmentLine().fulfillment(incompleteFulfillment).quantity(reservedQuantity);

        final OrderFulfillment completedFulfillment = MockEntity.orderFulfillment().order(order).completed(true).build();
        completedFulfillmentLineBuilder = MockEntity.orderFulfillmentLine().fulfillment(completedFulfillment).quantity(fulfilledQuantity);
    }

    @Test
    void new_CheckoutLineRequiresShipping_OrderLineFulfilledQuantityShouldEqual0() {
        final Checkout checkout = checkoutWithLines(
                Checkout.PreCheckoutLine.builder()
                        .variant(publishedProduct(false))
                        .quantity(1)
                        .build()
        );

        final Order order = Order.builder()
                .checkout(checkout)
                .capturedAmount(100L)
                .build();

        final List<OrderLine> orderLines = order.getLines();

        assertEquals(1, orderLines.size());

        final OrderLine orderLine = orderLines.get(0);

        assertEquals(0, orderLine.getFulfilledQuantity());
    }

    @Test
    void new_CheckoutLineDoesNotRequireShipping_OrderLineFulfilledQuantityShouldEqualQuantity() {
        final Checkout checkout = checkoutWithLines(
                Checkout.PreCheckoutLine.builder()
                        .variant(publishedProduct(true))
                        .quantity(1)
                        .build()
        );

        final Order order = Order.builder()
                .checkout(checkout)
                .capturedAmount(100L)
                .build();

        final List<OrderLine> orderLines = order.getLines();

        assertEquals(1, orderLines.size());

        final OrderLine orderLine = orderLines.get(0);

        assertEquals(orderLine.getQuantity(), orderLine.getFulfilledQuantity());
    }

    @Test
    void new_AnyOrderLinesRequireShipping_StatusShouldEqualCREATED() {
        final Checkout checkout = checkoutWithLines(
                Checkout.PreCheckoutLine.builder()
                        .variant(publishedProduct(true))
                        .quantity(1)
                        .build(),
                Checkout.PreCheckoutLine.builder()
                        .variant(publishedProduct(true))
                        .quantity(1)
                        .build(),
                Checkout.PreCheckoutLine.builder()
                        .variant(publishedProduct(false))
                        .quantity(1)
                        .build()
        );

        final Order order = Order.builder()
                .checkout(checkout)
                .capturedAmount(100L)
                .build();

        assertEquals(OrderStatus.CREATED, order.getStatus());
    }

    @Test
    void new_NoOrderLinesRequireShipping_ShouldFulfillAutomatically() {
        final Checkout checkout = checkoutWithLines(
                Checkout.PreCheckoutLine.builder()
                        .variant(publishedProduct(true))
                        .quantity(1)
                        .build()
        );

        final Order order = Order.builder()
                .checkout(checkout)
                .capturedAmount(100L)
                .build();

        assertEquals(OrderStatus.FULFILLED, order.getStatus());
    }

    @Test
    void removeFulfillment_IncompleteFulfillment_ShouldSubtractFromReservedQuantity() {
        final OrderLine line = orderLineBuilder.build();
        final OrderFulfillmentLine incompleteFulfillmentLine = incompleteFulfillmentLineBuilder.orderLine(line).build();
        final int x = line.getReservedQuantity();

        order.removeFulfillment(incompleteFulfillmentLine.getFulfillment());

        assertEquals(line.getReservedQuantity(), x - incompleteFulfillmentLine.getQuantity());
    }

    @Test
    void removeFulfillment_CompletedFulfillment_ShouldSubtractFromFulfilledQuantity() {
        final OrderLine line = orderLineBuilder.build();
        final OrderFulfillmentLine completedFulfillmentLine = completedFulfillmentLineBuilder.orderLine(line).build();
        final int x = line.getFulfilledQuantity();

        order.removeFulfillment(completedFulfillmentLine.getFulfillment());

        assertEquals(line.getFulfilledQuantity(), x - completedFulfillmentLine.getQuantity());
    }

    @Test
    void fulfill_HappyPath_PreviousStatusShouldTransition() {
        final OrderStatus status = OrderStatus.PARTIALLY_FULFILLED;
        final Order order = MockEntity.order()
                .status(status)
                .previousStatus(OrderStatus.PROCESSING)
                .build();

        order.fulfill();

        assertEquals(status, order.getPreviousStatus());
    }

    @Test
    void fulfill_StatusEqualsPreviousStatus_PreviousStatusShouldNotChange() {
        final OrderStatus status = OrderStatus.PROCESSING;
        final Order order = MockEntity.order()
                .status(status)
                .previousStatus(status)
                .build();

        order.fulfill();

        assertEquals(status, order.getPreviousStatus());
    }

    @Test
    void undoFulfill_HappyPath_PreviousStatusShouldNotChange() {
        final OrderStatus previousStatus = OrderStatus.CONFIRMED;
        final Order order = MockEntity.order()
                .status(OrderStatus.FULFILLED)
                .previousStatus(previousStatus)
                .build();

        order.undoFulfill();

        assertEquals(previousStatus, order.getPreviousStatus());
    }

    @Test
    void cancel_StatusIsFinalizingStatus_PreviousStatusShouldNotChange() {
        final OrderStatus previousStatus = OrderStatus.PARTIALLY_FULFILLED;
        final Order order = MockEntity.order()
                .status(OrderStatus.FULFILLED)
                .previousStatus(previousStatus)
                .build();

        order.cancel();

        assertEquals(previousStatus, order.getPreviousStatus());
    }

    @Test
    void cancel_StatusIsNotFinalizingStatus_PreviousStatusShouldTransition() {
        final OrderStatus status = OrderStatus.PARTIALLY_FULFILLED;
        final Order order = MockEntity.order()
                .status(status)
                .previousStatus(OrderStatus.PROCESSING)
                .build();

        order.cancel();

        assertEquals(status, order.getPreviousStatus());
    }

    @Test
    void cancel_StatusEqualsPreviousStatus_PreviousStatusShouldNotChange() {
        final OrderStatus status = OrderStatus.PROCESSING;
        final Order order = MockEntity.order()
                .status(status)
                .previousStatus(status)
                .build();

        order.cancel();

        assertEquals(status, order.getPreviousStatus());
    }

    @Test
    void reinstate_HappyPath_PreviousStatusShouldNotChange() {
        final OrderStatus previousStatus = OrderStatus.PROCESSING;
        final Order order = MockEntity.order()
                .status(OrderStatus.CANCELLED)
                .previousStatus(previousStatus)
                .build();

        order.reinstate();

        assertEquals(previousStatus, order.getPreviousStatus());
    }

    private static Checkout checkoutWithLines(final Checkout.PreCheckoutLine... lines) {
        final Checkout checkout = Checkout.builder()
                .customer(MockEntity.user().build())
                .email("admin@j2c.com")
                .currency(CurrencyCode.EUR)
                .ipAddress("127.0.0.1")
                .massUnit(MassUnit.g)
                .lines(Arrays.asList(lines))
                .build();
        checkout.setPayment(Payment.free());
        checkout.addAddress(nextObject(Address.class));
        if (checkout.isShippingRequired()) {
            checkout.useSingleAddress(false);
            final ShippingZone zone = MockEntity.shippingZone().build();
            checkout.addShippingAddress(
                    nextObject(Address.class).copy()
                            .country(
                                    MockEntity.shippingCountry()
                                            .zone(zone)
                                            .build()
                                            .getCode()
                            )
                            .build()
            );
            checkout.setShippingMethod(
                    MockEntity.shippingMethod()
                            .zone(zone)
                            .type(ShippingMethodType.Price)
                            .min(0L)
                            .max(Long.MAX_VALUE)
                            .rate(50L)
                            .build()
            );
        }
        return checkout;
    }

    private static ProductVariant publishedProduct(final boolean digital) {
        return MockEntity.productVariant()
                .price(50L)
                .product(
                        MockEntity.product()
                                .defaultPrice(50L)
                                .published(true)
                                .digital(digital)
                                .build()
                )
                .build();
    }

}
