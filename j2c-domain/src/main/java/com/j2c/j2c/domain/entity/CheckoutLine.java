package com.j2c.j2c.domain.entity;

import com.j2c.j2c.domain.exception.DomainException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;

import static com.j2c.j2c.domain.entity.MaxLengths.PRODUCTVARIANT_NAME_MAXLENGTH;
import static com.j2c.j2c.domain.entity.MaxLengths.PRODUCT_NAME_MAXLENGTH;
import static com.j2c.j2c.domain.exception.DomainErrorMessages.LINE_DOES_NOT_BELONG_TO_CHECKOUT;
import static com.j2c.j2c.domain.util.J2cUtils.assertNotNull;

@javax.persistence.Entity
@Table(name = "checkoutline",
        indexes = {
                @Index(columnList = "checkout_id"),
                @Index(columnList = "product_id")
        }
)
public class CheckoutLine extends BaseEntity<Long> {

    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Getter
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "checkout_id", nullable = false, updatable = false,
            foreignKey = @ForeignKey(ConstraintMode.CONSTRAINT))
    private Checkout checkout;

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
    @Column(name = "shipping_required", nullable = false, updatable = false)
    private boolean shippingRequired;

    @SuppressWarnings("unused")
    CheckoutLine() {}

    @Builder(access = AccessLevel.PACKAGE)
    private CheckoutLine(
            final Checkout checkout,
            final Checkout.PreCheckoutLine preCheckoutLine
    ) {
        assertNotNull(preCheckoutLine, "preCheckoutLine");
        this.checkout = assertNotNull(checkout, "checkout");
        this.product = preCheckoutLine.getVariant().getProduct();
        this.productName = product.getName();
        this.variantName = preCheckoutLine.getVariant().getName();
        this.unitPriceAmount = preCheckoutLine.getVariant().getEffectivePrice();
        this.quantity = preCheckoutLine.getQuantity();
        this.shippingRequired = preCheckoutLine.requiresShipping();
    }

    public CheckoutLine verifyBelongsToCheckout(final Checkout checkout) {
        if (!belongsToCheckout(checkout)) {
            throw new DomainException(String.format(LINE_DOES_NOT_BELONG_TO_CHECKOUT, id, checkout.getId()), this);
        }
        return this;
    }

    public boolean belongsToCheckout(final Checkout checkout) {
        if (checkout == null) {
            return false;
        }
        return checkout.isSameAs(this.checkout);
    }

}
