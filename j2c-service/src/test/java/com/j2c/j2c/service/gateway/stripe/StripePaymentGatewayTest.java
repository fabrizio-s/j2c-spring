package com.j2c.j2c.service.gateway.stripe;

import com.j2c.j2c.domain.entity.Checkout;
import com.j2c.j2c.domain.entity.User;
import com.j2c.j2c.domain.entity.Payment;
import com.j2c.j2c.service.dto.CardDTO;
import com.j2c.j2c.service.test.MockEntity;
import com.neovisionaries.i18n.CurrencyCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

//@Disabled
class StripePaymentGatewayTest {

    private StripePaymentGateway paymentGateway;
    private final String paymentIntentId = "pi_1IYZLvCkNO9M4QMd8zwvVJe2";

    @BeforeEach
    void setUp() {
        final StripeSupportedPaymentMethods mapper = new StripeSupportedPaymentMethods();
        paymentGateway = new StripePaymentGateway(System.getenv("J2C_STRIPE_TEST_KEY"), mapper);
        paymentGateway.init();
    }

//    @Test
    void getPaymentMethods() {
        final User user = MockEntity.user()
                .externalId("cus_J3rYpuUOaRjpRZ")
                .build();

        final CardDTO cardDTO = (CardDTO) paymentGateway.findPaymentMethods(user).get(0);
        System.out.println("cardDTO.getId() = " + cardDTO.getId());
        System.out.println("cardDTO.getBrand() = " + cardDTO.getBrand());
        System.out.println("cardDTO.getLast4() = " + cardDTO.getLast4());
    }

//    @Test
    void request() {
        final Payment payment = paymentGateway.request(50L, CurrencyCode.EUR, null);

        System.out.println("request.getPaymentId() = " + payment.getId()); // set paymentIntentId to this value
        System.out.println("request.getPaymentToken() = " + payment.getToken()); // set paymentToken in stripe-test.js (external) to this value
    }

//    @Test
    void capture() {
        final Payment payment = paymentGateway.findById(paymentIntentId).orElseThrow();
        payment.capture();
    }

//    @Test
    void cancel() {
        final Payment payment = paymentGateway.findById(paymentIntentId).orElseThrow();
        payment.cancel();
    }

}
