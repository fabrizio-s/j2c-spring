package com.j2c.j2c.domain.entity;

import com.j2c.j2c.domain.exception.DomainException;
import com.j2c.j2c.domain.enums.OrderStatus;
import lombok.*;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.j2c.j2c.domain.entity.MaxLengths.ORDERFULFILLMENT_TRACKINGNUMBER_MAXLENGTH;
import static com.j2c.j2c.domain.exception.DomainErrorMessages.*;
import static com.j2c.j2c.domain.util.J2cUtils.assertNotNull;

@javax.persistence.Entity
@Table(name = "orderfulfillment",
        indexes = @Index(columnList = "order_id")
)
public class OrderFulfillment extends BaseEntity<Long> {

    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Getter
    @Column(name = "tracking_number", length = ORDERFULFILLMENT_TRACKINGNUMBER_MAXLENGTH)
    private String trackingNumber;

    @Getter
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, updatable = false,
            foreignKey = @ForeignKey(ConstraintMode.CONSTRAINT))
    private Order order;

    @Getter
    @Column(name = "completed", nullable = false)
    private boolean completed;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL,
            mappedBy = "fulfillment", orphanRemoval = true)
    private List<OrderFulfillmentLine> lines = new ArrayList<>();

    @SuppressWarnings("unused")
    OrderFulfillment() {}

    @Builder(access = AccessLevel.PACKAGE)
    private OrderFulfillment(final Order order) {
        this.order = assertNotNull(order, "order");
    }

    @Builder(builderClassName = "OrderFulfillmentLineCreator",
            builderMethodName = "newLine",
            buildMethodName = "addLine")
    private OrderFulfillmentLine addFulfillmentLine(
            final OrderLine orderLine,
            final int quantity
    ) {
        if (orderLine == null) {
            throw new IllegalArgumentException("orderLine must not be null");
        } else if (!order.isSameAs(orderLine.getOrder())) {
            throw new DomainException(String.format(FULFILLMENT_ORDER_DIFFERS_FROM_ORDER_LINE_ORDER, id), this);
        } else if (!order.isProcessable()) {
            throw new DomainException(String.format(ORDER_NOT_PROCESSABLE, order.getId(), order.getStatus()), this);
        } else if (isCompleted()) {
            throw new DomainException(String.format(FULFILLMENT_ALREADY_COMPLETED, id), this);
        } else if (!orderLine.isShippingRequired()) {
            throw new DomainException(String.format(ORDER_LINE_DOES_NOT_REQUIRE_SHIPPING, orderLine.getId()), this);
        } else if (alreadyHasFulfillmentLineForOrderLine(orderLine)) {
            return null;
        }
        final OrderFulfillmentLine line = OrderFulfillmentLine.builder()
                .orderLine(orderLine)
                .fulfillment(this)
                .quantity(quantity)
                .build();
        lines.add(line);
        return line;
    }

    public OrderLine removeLine(final OrderFulfillmentLine fulfillmentLine) {
        if (!order.isProcessable()) {
            throw new DomainException(String.format(ORDER_NOT_PROCESSABLE, order.getId(), order.getStatus()), this);
        } else if (completed) {
            throw new DomainException(String.format(FULFILLMENT_ALREADY_COMPLETED, id), this);
        } else if (!fulfillmentLine.belongsToFulfillment(this)) {
            return null;
        }
        lines.remove(fulfillmentLine);
        return fulfillmentLine.remove();
    }

    public List<OrderLine> complete() {
        if (!order.isProcessable()) {
            throw new DomainException(String.format(ORDER_NOT_PROCESSABLE, order.getId(), order.getStatus()), this);
        } else if (completed) {
            throw new DomainException(String.format(FULFILLMENT_ALREADY_COMPLETED, id), this);
        } else if (OrderStatus.PROCESSING.equals(order.getStatus())) {
            order.partiallyFulfill();
        }
        final List<OrderLine> orderLines = lines.stream()
                .map(OrderFulfillmentLine::complete)
                .collect(Collectors.toList());
        completed = true;
        return orderLines;
    }

    public void setTrackingNumber(final String trackingNumber) {
        if (trackingNumber == null) {
            throw new IllegalArgumentException("trackingNumber must not be null");
        } else if (OrderStatus.CANCELLED.equals(order.getStatus())) {
            throw new DomainException(String.format(ORDER_ALREADY_CANCELLED, order.getId()), this);
        } else if (!completed) {
            throw new DomainException(String.format(FULFILLMENT_NOT_COMPLETED, id), this);
        }
        this.trackingNumber = trackingNumber;
    }

    public OrderFulfillment verifyBelongsToOrder(final Order order) {
        if (!belongsToOrder(order)) {
            throw new DomainException(String.format(FULFILLMENT_DOES_NOT_BELONG_TO_ORDER, id, order.getId()), this);
        }
        return this;
    }

    public boolean belongsToOrder(final Order order) {
        if (order == null) {
            return false;
        }
        return order.isSameAs(this.order);
    }

    List<OrderLine> remove() {
        return lines.stream()
                .map(OrderFulfillmentLine::remove)
                .collect(Collectors.toList());
    }

    private boolean alreadyHasFulfillmentLineForOrderLine(final OrderLine orderLine) {
        return lines.stream()
                .map(OrderFulfillmentLine::getOrderLine)
                .anyMatch(ol -> ol.isSameAs(orderLine));
    }

}
