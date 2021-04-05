package com.j2c.j2c.it.util;

import com.j2c.j2c.domain.entity.Payment;
import com.j2c.j2c.domain.entity.User;
import com.j2c.j2c.service.dto.CardDTO;
import com.j2c.j2c.service.dto.PaymentMethodDTO;
import com.j2c.j2c.service.gateway.PaymentGateway;
import com.neovisionaries.i18n.CurrencyCode;
import lombok.NonNull;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.annotation.Primary;

import java.util.*;

import static com.j2c.j2c.domain.util.J2cUtils.optional;

@Primary
@TestComponent
public class MockPaymentGateway implements PaymentGateway {

    private final Map<String, MockPayment> payments = new HashMap<>();

    @Override
    public String createCustomer() {
        return null;
    }

    @Override
    public Payment request(@NonNull final Long amount, @NonNull final CurrencyCode currency, final String customerId) {
        final MockPayment mockPayment = MockPayment.builder()
                .id(UUID.randomUUID().toString())
                .token(UUID.randomUUID().toString())
                .capturedAmount(amount)
                .currency(currency)
                .build();
        payments.put(mockPayment.getId(), mockPayment);
        return mockPayment;
    }

    @Override
    public Optional<Payment> findById(final String paymentId) {
        return optional(payments.get(paymentId));
    }

    @Override
    public List<? extends PaymentMethodDTO> findPaymentMethods(final User user) {
        return List.of(
                CardDTO.builder()
                        .id(UUID.randomUUID().toString())
                        .last4("4242")
                        .brand("visa")
                        .build()
        );
    }

    @Override
    public void removePaymentMethod(final User user, final String paymentMethodId) {
    }

}
