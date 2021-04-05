package com.j2c.j2c.service.gateway;

import com.j2c.j2c.domain.entity.Payment;
import com.j2c.j2c.domain.entity.User;
import com.j2c.j2c.service.dto.PaymentMethodDTO;
import com.neovisionaries.i18n.CurrencyCode;

import java.util.List;
import java.util.Optional;

public interface PaymentGateway {

    String createCustomer();

    Payment request(Long amount, CurrencyCode currency, String customerId);

    Optional<Payment> findById(String paymentId);

    List<? extends PaymentMethodDTO> findPaymentMethods(User user);

    void removePaymentMethod(User user, String paymentMethodId);

}
