package com.j2c.j2c.domain.entity;

import com.j2c.j2c.domain.exception.DomainException;
import lombok.*;

import javax.persistence.*;

import static com.j2c.j2c.domain.entity.MaxLengths.*;
import static com.j2c.j2c.domain.exception.DomainErrorMessages.*;
import static com.j2c.j2c.domain.util.J2cUtils.assertNotNull;

@javax.persistence.Entity
@Table(name = "orderline",
        indexes = {
                @Index(columnList = "order_id"),
                @Index(columnList = "product_id")
        }
)
public class OrderLine extends BaseEntity<Long> {

    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Getter
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, updatable = false,
            foreignKey = @ForeignKey(ConstraintMode.CONSTRAINT))
    private Order order;

    @Getter
    @Column(name = "product_name", nullable = false, updatable = false,
            length = PRODUCT_NAME_MAXLENGTH)
    private String productName;

    @Getter
    @Column(name = "variant_name", updatable = false,
            length = PRODUCTVARIANT_NAME_MAXLENGTH)
    private String variantName;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", updatable = false,
            foreignKey = @ForeignKey(ConstraintMode.CONSTRAINT))
    private Product product;

    @Getter
    @Column(name = "unit_price_amount",
            nullable = false, updatable = false)
    private Long unitPriceAmount;

    @Getter
    @Column(name = "quantity", nullable = false, updatable = false)
    private int quantity;

    @Getter
    @Column(name = "fulfilled_quantity", nullable = false)
    private int fulfilledQuantity;

    @Getter
    @Column(name = "reserved_quantity", nullable = false)
    private int reservedQuantity;

    @Getter
    @Column(name = "shipping_required", nullable = false, updatable = false)
    private boolean shippingRequired;

    @SuppressWarnings("unused")
    OrderLine() {}

    @Builder(access = AccessLevel.PACKAGE)
    private OrderLine(
            final Order order,
            final CheckoutLine checkoutLine
    ) {
        if (checkoutLine == null) {
            throw new IllegalArgumentException("checkoutLine must not be null");
        }
        this.order = assertNotNull(order, "order");
        productName = assertNotNull(checkoutLine.getProductName(), "productName");
        variantName = checkoutLine.getVariantName();
        product = assertNotNull(checkoutLine.getProduct(), "product");
        unitPriceAmount = assertNotNull(checkoutLine.getUnitPriceAmount(), "unitPriceAmount");
        quantity = checkoutLine.getQuantity();
        shippingRequired = checkoutLine.isShippingRequired();
        if (!shippingRequired) {
            fulfilledQuantity = quantity;
        }
    }

    public boolean isFulfilled() {
        return fulfilledQuantity >= quantity;
    }

    public OrderLine verifyBelongsToOrder(final Order order) {
        if (!belongsToOrder(order)) {
            throw new DomainException(String.format(LINE_DOES_NOT_BELONG_TO_ORDER, id, order.getId()), this);
        }
        return this;
    }

    public boolean belongsToOrder(final Order order) {
        if (order == null) {
            return false;
        }
        return order.isSameAs(this.order);
    }

    public int getAssignableQuantity() {
        return Math.max(quantity - fulfilledQuantity - reservedQuantity, 0);
    }

    void addFulfilledQuantity(final int quantityToAdd) {
        if (quantityToAdd <= 0) {
            return;
        }
        final int newFulfilledQuantity = fulfilledQuantity + quantityToAdd;
        if (newFulfilledQuantity > quantity) {
            throw new DomainException(String.format(CANNOT_FULFILL_MORE_THAN_ORDER_LINE_QUANTITY, id), this);
        }
        fulfilledQuantity = newFulfilledQuantity;
    }

    void removeFulfilledQuantity(final int quantityToRemove) {
        if (quantityToRemove > fulfilledQuantity) {
            fulfilledQuantity = 0;
        } else if (quantityToRemove > 0) {
            fulfilledQuantity -= quantityToRemove;
        }
    }

    void addReservedQuantity(final int quantity) {
        if (quantity <= 0) {
            return;
        } else if (quantity > getAssignableQuantity()) {
            throw new DomainException(String.format(INSUFFICIENT_ORDER_LINE_ASSIGNABLE_QUANTITY, id, getAssignableQuantity()), this);
        }
        reservedQuantity += quantity;
    }

    void removeReservedQuantity(final int quantity) {
        if (quantity > reservedQuantity) {
            reservedQuantity = 0;
        } else if (quantity > 0) {
            reservedQuantity -= quantity;
        }
    }

}
