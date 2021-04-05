package com.j2c.j2c.service.gateway.stripe;

import com.j2c.j2c.domain.entity.Checkout;
import com.j2c.j2c.service.exception.GatewayException;
import com.j2c.j2c.domain.entity.Payment;
import com.neovisionaries.i18n.CurrencyCode;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentUpdateParams;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import static com.j2c.j2c.service.gateway.stripe.StripeUtils.toStripeCurrency;
import static com.j2c.j2c.service.gateway.stripe.StripeUtils.toEnum;

@RequiredArgsConstructor
public class StripePayment implements Payment {

    @NonNull
    private PaymentIntent paymentIntent;

    @Override
    public String getId() {
        return paymentIntent.getId();
    }

    @Override
    public String getToken() {
        return paymentIntent.getClientSecret();
    }

    @Override
    public Long getCapturedAmount() {
        return paymentIntent.getAmountReceived();
    }

    @Override
    public String getPaymentMethodId() {
        return paymentIntent.getPaymentMethod();
    }

    @Override
    public CurrencyCode getCurrency() {
        return toEnum(paymentIntent.getCurrency());
    }

    @Override
    public void update(final Checkout checkout) {
        final PaymentIntentUpdateParams.Builder params = PaymentIntentUpdateParams.builder()
                .setAmount(checkout.getTotalPrice());
        if (hasDifferentCurrency(checkout)) {
            params.setCurrency(toStripeCurrency(checkout.getCurrency()));
        }
        try {
            paymentIntent = paymentIntent.update(params.build());
        } catch (final StripeException exception) {
            throw new GatewayException(exception.getMessage(), exception);
        }
    }

    @Override
    public void capture() {
        try {
            paymentIntent = paymentIntent.capture();
        } catch (final StripeException exception) {
            throw new GatewayException(exception.getMessage(), exception);
        }
    }

    @Override
    public void cancel() {
        try {
            paymentIntent = paymentIntent.cancel();
        } catch (final StripeException exception) {
            throw new GatewayException(exception.getMessage(), exception);
        }
    }

    private boolean hasDifferentCurrency(final Checkout checkout) {
        final CurrencyCode currency = toEnum(paymentIntent.getCurrency());
        return !checkout.getCurrency().equals(currency);
    }

}
