package com.j2c.j2c.domain.entity;

import com.j2c.j2c.domain.exception.DomainException;
import com.j2c.j2c.domain.enums.ShippingMethodType;
import lombok.*;

import javax.persistence.*;

import static com.j2c.j2c.domain.entity.MaxLengths.*;
import static com.j2c.j2c.domain.enums.ShippingMethodType.Weight;
import static com.j2c.j2c.domain.exception.DomainErrorMessages.METHOD_DOES_NOT_BELONG_TO_ZONE;
import static com.j2c.j2c.domain.util.J2cUtils.assertNotNull;
import static com.j2c.j2c.domain.util.J2cUtils.optional;

@javax.persistence.Entity
@Table(name = "shippingmethod")
public class ShippingMethod extends BaseEntity<Long> {

    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Getter
    @Column(name = "name", nullable = false,
            length = SHIPPINGMETHOD_NAME_MAXLENGTH)
    private String name;

    @Getter
    @Column(name = "min_value", nullable = false,
            precision = 5, scale = 2)
    private Long min;

    @Getter
    @Column(name = "max_value", nullable = false,
            precision = 5, scale = 2)
    private Long max;

    @Getter
    @Column(name = "rate", nullable = false)
    private Long rate;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, updatable = false,
            length = SHIPPINGMETHOD_TYPE_MAXLENGTH)
    private ShippingMethodType type;

    @Getter
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id", nullable = false, updatable = false,
            foreignKey = @ForeignKey(ConstraintMode.CONSTRAINT))
    private ShippingZone zone;

    @SuppressWarnings("unused")
    ShippingMethod() {}

    @Builder
    private ShippingMethod(
            final String name,
            final Long min,
            final Long max,
            final Long rate,
            final ShippingMethodType type,
            final ShippingZone zone
    ) {
        this.name = assertNotNull(name, "name");
        this.min = assertNotNull(min, "min");
        this.max = assertNotNull(max, "max");
        this.rate = assertNotNull(rate, "rate");
        this.type = assertNotNull(type, "type");
        this.zone = assertNotNull(zone, "zone");
        if (min > max) {
            throw new DomainException("Shipping Method MIN must not be greater than MAX!", this);
        }
    }

    public ShippingMethod verifyBelongsToZone(final ShippingZone zone) {
        if (!belongsToZone(zone)) {
            throw new DomainException(String.format(METHOD_DOES_NOT_BELONG_TO_ZONE, id, zone.getId()), this);
        }
        return this;
    }

    public boolean belongsToZone(final ShippingZone order) {
        if (order == null) {
            return false;
        }
        return order.getId() != null && order.getId().equals(this.zone.getId());
    }

    public boolean canBeAppliedToCheckout(@NonNull final Checkout checkout) {
        final Address shippingAddress = checkout.getActualShippingAddress();
        if (shippingAddress == null
                || !checkout.isShippingRequired()
                || !zone.hasCountry(shippingAddress.getCountry())) {
            return false;
        }
        final long parameter = getParameter(checkout);
        return parameter >= min && parameter <= max;
    }

    public void setName(final String name) {
        this.name = assertNotNull(name, "name");
    }

    public void setMin(final Long min) {
        this.min = assertNotNull(min, "min");
    }

    public void setMax(final Long max) {
        this.max = assertNotNull(max, "max");
    }

    public void setRate(final Long rate) {
        this.rate = assertNotNull(rate, "rate");
    }

    private long getParameter(final Checkout checkout) {
        if (ShippingMethodType.Price.equals(type)) {
            return checkout.getPrice();
        } else if (Weight.equals(type)) {
            return optional(checkout.getTotalMass())
                    .map(Long::valueOf)
                    .orElse(0L);
        }
        throw new IllegalArgumentException("Unsupported shipping method type: " + type);
    }

}
