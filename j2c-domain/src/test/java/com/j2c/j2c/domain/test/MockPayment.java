package com.j2c.j2c.domain.test;

import com.j2c.j2c.domain.entity.Checkout;
import com.j2c.j2c.domain.entity.Payment;
import com.neovisionaries.i18n.CurrencyCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MockPayment implements Payment {

    private final String id;
    private final String token;
    private final Long capturedAmount;
    private final String paymentMethodId;
    private final CurrencyCode currency;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getToken() {
        return token;
    }

    @Override
    public Long getCapturedAmount() {
        return capturedAmount;
    }

    @Override
    public String getPaymentMethodId() {
        return paymentMethodId;
    }

    @Override
    public CurrencyCode getCurrency() {
        return currency;
    }

    @Override
    public void update(final Checkout checkout) {
    }

    @Override
    public void capture() {
    }

    @Override
    public void cancel() {
    }

}
