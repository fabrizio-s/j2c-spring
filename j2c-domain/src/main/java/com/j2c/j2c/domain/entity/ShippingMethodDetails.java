package com.j2c.j2c.domain.entity;

import com.j2c.j2c.domain.enums.ShippingMethodType;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import static com.j2c.j2c.domain.entity.MaxLengths.*;
import static com.j2c.j2c.domain.util.J2cUtils.assertNotNull;

@Embeddable
public class ShippingMethodDetails {

    @Getter
    @Column(name = "shippingmethod_amount",
            precision = 8, scale = 2)
    private Long amount;

    @Getter
    @Column(name = "shippingmethod_name",
            length = SHIPPINGMETHOD_NAME_MAXLENGTH)
    private String name;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "shippingmethod_type",
            length = SHIPPINGMETHOD_TYPE_MAXLENGTH)
    private ShippingMethodType type;

    @SuppressWarnings("unused")
    ShippingMethodDetails() {}

    @Builder(access = AccessLevel.PACKAGE)
    private ShippingMethodDetails(
            final Long amount,
            final String name,
            final ShippingMethodType type
    ) {
        this.amount = assertNotNull(amount, "amount");
        this.name = assertNotNull(name, "name");
        this.type = assertNotNull(type, "type");
    }

    @Override
    public String toString() {
        return "ShippingMethodDetails(" +
                "name='" + name + '\'' +
                ')';
    }

}
