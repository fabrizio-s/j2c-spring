package com.j2c.j2c.domain.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import static com.j2c.j2c.domain.entity.MaxLengths.PAYMENT_ID_MAXLENGTH;
import static com.j2c.j2c.domain.entity.MaxLengths.PAYMENT_TOKEN_MAXLENGTH;
import static com.j2c.j2c.domain.util.J2cUtils.assertNotNull;

@Embeddable
public class PaymentDetails {

    @Getter
    @Column(name = "payment_id",
            length = PAYMENT_ID_MAXLENGTH)
    private String id;

    @Getter
    @Column(name = "payment_token",
            length = PAYMENT_TOKEN_MAXLENGTH)
    private String token;

    @SuppressWarnings("unused")
    PaymentDetails() {}

    @Builder(access = AccessLevel.PACKAGE)
    private PaymentDetails(
            final String id,
            final String token
    ) {
        this.id = assertNotNull(id, "id");
        this.token = assertNotNull(token, "token");
    }

    @Override
    public String toString() {
        return "PaymentDetails(" +
                "id='" + id + '\'' +
                ", token='" + token + '\'' +
                ')';
    }

}
