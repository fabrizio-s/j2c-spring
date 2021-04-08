package com.j2c.j2c.domain.entity;

import com.neovisionaries.i18n.CurrencyCode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FreePaymentTest {

    @Test
    void getId_HappyPath_ShouldNotBeNull() {
        final Payment payment = Payment.free();

        assertNotNull(payment.getId());
    }

    @Test
    void getToken_HappyPath_ShouldNotBeNull() {
        final Payment payment = Payment.free();

        assertNotNull(payment.getToken());
    }

    @Test
    void getCapturedAmount_HappyPath_ShouldEqual0() {
        final Payment payment = Payment.free();

        assertEquals(0L, payment.getCapturedAmount());
    }

    @Test
    void getPaymentMethodId_HappyPath_ShouldNotBeNull() {
        final Payment payment = Payment.free();

        assertNotNull(payment.getPaymentMethodId());
    }

    @Test
    void getCurrency_HappyPath_ShouldReturnUNDEFINED() {
        final Payment payment = Payment.free();

        assertEquals(CurrencyCode.UNDEFINED, payment.getCurrency());
    }

}
