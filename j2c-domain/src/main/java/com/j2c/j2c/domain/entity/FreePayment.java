package com.j2c.j2c.domain.entity;

import com.neovisionaries.i18n.CurrencyCode;

public final class FreePayment implements Payment {

    static final FreePayment INSTANCE = new FreePayment();

    private FreePayment() {}

    @Override
    public String getId() {
        return "J2C_FREE_PAYMENT_ID";
    }

    @Override
    public String getToken() {
        return "J2C_FREE_PAYMENT_TOKEN";
    }

    @Override
    public Long getCapturedAmount() {
        return 0L;
    }

    @Override
    public String getPaymentMethodId() {
        return "J2C_FREE_PAYMENT_PAYMENTMETHOD";
    }

    @Override
    public CurrencyCode getCurrency() {
        return CurrencyCode.UNDEFINED;
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
