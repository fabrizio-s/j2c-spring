package com.j2c.j2c.domain.entity;

import com.j2c.j2c.domain.exception.DomainException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;

import static com.j2c.j2c.domain.exception.DomainErrorMessages.*;
import static com.j2c.j2c.domain.util.J2cUtils.assertNotNull;

@javax.persistence.Entity
@Table(name = "orderfulfillmentline",
        indexes = {
                @Index(columnList = "orderline_id"),
                @Index(columnList = "orderfulfillment_id")
        }
)
public class OrderFulfillmentLine extends BaseEntity<Long> {

    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Getter
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "orderfulfillment_id", nullable = false, updatable = false,
            foreignKey = @ForeignKey(ConstraintMode.CONSTRAINT))
    private OrderFulfillment fulfillment;

    @Getter
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "orderline_id", nullable = false, updatable = false,
            foreignKey = @ForeignKey(ConstraintMode.CONSTRAINT))
    private OrderLine orderLine;

    @Getter
    @Column(name = "quantity", nullable = false)
    private int quantity;

    @SuppressWarnings("unused")
    OrderFulfillmentLine() {}

    @Builder(access = AccessLevel.PACKAGE)
    private OrderFulfillmentLine(
            final OrderFulfillment fulfillment,
            final OrderLine orderLine,
            final int quantity
    ) {
        this.fulfillment = assertNotNull(fulfillment, "fulfillment");
        this.orderLine = assertNotNull(orderLine, "orderLine");
        replaceQuantity(quantity);
    }

    public OrderFulfillmentLine verifyBelongsToFulfillment(final OrderFulfillment fulfillment) {
        if (!belongsToFulfillment(fulfillment)) {
            throw new DomainException(String.format(LINE_DOES_NOT_BELONG_TO_FULFILLMENT, id, fulfillment.getId()), this);
        }
        return this;
    }

    public void setQuantity(final int newQuantity) {
        final Order order = fulfillment.getOrder();
        if (!order.isProcessable()) {
            throw new DomainException(String.format(ORDER_NOT_PROCESSABLE, order.getId(), order.getStatus()), order);
        } else if (fulfillment.isCompleted()) {
            throw new DomainException(String.format(FULFILLMENT_ALREADY_COMPLETED, fulfillment.getId()), fulfillment);
        }
        replaceQuantity(newQuantity);
    }

    public boolean belongsToFulfillment(final OrderFulfillment fulfillment) {
        if (fulfillment == null) {
            return false;
        }
        return fulfillment.getId() != null && fulfillment.getId().equals(this.fulfillment.getId());
    }

    OrderLine complete() {
        orderLine.removeReservedQuantity(quantity);
        orderLine.addFulfilledQuantity(quantity);
        return orderLine;
    }

    OrderLine remove() {
        if (!fulfillment.isCompleted()) {
            orderLine.removeReservedQuantity(quantity);
        } else {
            orderLine.removeFulfilledQuantity(quantity);
        }
        return orderLine;
    }

    private void replaceQuantity(final int newQuantity) {
        if (newQuantity <= 0) {
            throw new DomainException(String.format(FULFILLMENT_LINE_QUANTITY_MUST_BE_POSITIVE, id), this);
        }
        orderLine.removeReservedQuantity(quantity);
        orderLine.addReservedQuantity(newQuantity);
        quantity = newQuantity;
    }

}
