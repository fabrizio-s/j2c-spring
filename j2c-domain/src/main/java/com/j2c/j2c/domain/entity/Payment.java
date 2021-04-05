package com.j2c.j2c.domain.entity;

import com.neovisionaries.i18n.CurrencyCode;

public interface Payment extends Entity<String> {

    String getToken();

    Long getCapturedAmount();

    String getPaymentMethodId();

    CurrencyCode getCurrency();

    void update(Checkout checkout);

    void capture();

    void cancel();

    default boolean belongsToCheckout(final Checkout checkout) {
        if (isFree() || checkout == null) {
            return false;
        }
        final String paymentId = checkout.getPaymentId();
        if (paymentId == null) {
            return false;
        }
        return paymentId.equals(getId());
    }

    default boolean isFree() {
        return FreePayment.INSTANCE.equals(this);
    }

    static Payment free() {
        return FreePayment.INSTANCE;
    }

}
