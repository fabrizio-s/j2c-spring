package com.j2c.j2c.service.gateway.stripe;

import com.j2c.j2c.domain.entity.Payment;
import com.j2c.j2c.domain.entity.User;
import com.j2c.j2c.domain.enums.PaymentMethodType;
import com.j2c.j2c.domain.exception.EntityDoesNotExistException;
import com.j2c.j2c.service.exception.GatewayException;
import com.j2c.j2c.service.exception.ServiceException;
import com.j2c.j2c.service.gateway.*;
import com.j2c.j2c.service.dto.PaymentMethodDTO;
import com.neovisionaries.i18n.CurrencyCode;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PaymentMethodListParams;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.j2c.j2c.domain.util.J2cUtils.optional;
import static com.j2c.j2c.service.gateway.stripe.StripeUtils.toStripeCurrency;

@Component
class StripePaymentGateway implements PaymentGateway {

    private final String secretKey;
    private final StripeSupportedPaymentMethods supportedPaymentMethods;

    public StripePaymentGateway(
            @NonNull @Value("${j2c.service.gateway.stripe.key}") final String stripeSecretKey,
            final StripeSupportedPaymentMethods supportedPaymentMethods
    ) {
        this.secretKey = stripeSecretKey;
        this.supportedPaymentMethods = supportedPaymentMethods;
    }

    @PostConstruct
    protected void init() {
        Stripe.apiKey = secretKey;
    }

    @Override
    public List<? extends PaymentMethodDTO> findPaymentMethods(@NonNull final User user) {
        final String customerId = user.getExternalId();
        if (customerId == null) {
            return Collections.emptyList();
        }
        final PaymentMethodType type = PaymentMethodType.Card; // default, currently the only supported type of payment method
        final PaymentMethodListParams.Type stripeType = supportedPaymentMethods.getType(type);
        final PaymentMethodCollection paymentMethods = getPaymentMethodsByType(customerId, stripeType);
        return toPaymentMethodDTO(type, paymentMethods.getData());
    }

    @Override
    public void removePaymentMethod(@NonNull final User user, @NonNull final String paymentMethodId) {
        final PaymentMethod paymentMethod = retrievePaymentMethod(paymentMethodId);
        if (!paymentMethodBelongsToCustomer(user, paymentMethod)) {
            throw new ServiceException("The payment method with id '"
                    + paymentMethodId + "' does not belong to the customer with id '" + user.getId() + "'");
        }
        try {
            paymentMethod.detach();
        } catch (final StripeException exception) {
            throw new GatewayException(exception.getMessage(), exception);
        }
    }

    @Override
    public String createCustomer() {
        final CustomerCreateParams params = CustomerCreateParams.builder().build();
        try {
            final Customer customer = Customer.create(params);
            return customer.getId();
        } catch (final StripeException exception) {
            throw new GatewayException(exception.getMessage(), exception);
        }
    }

    @Override
    public Payment request(@NonNull final Long amount, @NonNull final CurrencyCode currency, final String customerId) {
        if (amount <= 0) {
            return Payment.free();
        }
        final PaymentIntentCreateParams params = new PaymentIntentCreateParams.Builder()
                .setAmount(amount)
                .setCurrency(toStripeCurrency(currency))
                .addAllPaymentMethodType(supportedPaymentMethods.getAllSupportedTypes())
                .setCaptureMethod(PaymentIntentCreateParams.CaptureMethod.MANUAL)
                .setCustomer(customerId)
                .build();
        try {
            final PaymentIntent paymentIntent = PaymentIntent.create(params);
            return new StripePayment(paymentIntent);
        } catch (final StripeException exception) {
            throw new GatewayException(exception.getMessage(), exception);
        }
    }

    @Override
    public Optional<Payment> findById(final String paymentId) {
        if (paymentId == null) {
            return Optional.empty();
        }
        final Optional<PaymentIntent> paymentIntent = retrievePayment(paymentId);
        return paymentIntent.map(StripePayment::new);
    }

    private Optional<PaymentIntent> retrievePayment(final String paymentIntentId) {
        try {
            return optional(PaymentIntent.retrieve(paymentIntentId));
        } catch (final StripeException exception) {
            return Optional.empty();
        }
    }

    private List<? extends PaymentMethodDTO> toPaymentMethodDTO(
            final PaymentMethodType type,
            final List<PaymentMethod> methods
    ) {
        return methods.stream()
                .map(t -> supportedPaymentMethods.getMapping(type).apply(t))
                .collect(Collectors.toList());
    }

    private PaymentMethodCollection getPaymentMethodsByType(
            final String customerId,
            final PaymentMethodListParams.Type type
    ) {
        final PaymentMethodListParams params = PaymentMethodListParams.builder()
                .setCustomer(customerId)
                .setType(type)
                .build();
        try {
            return PaymentMethod.list(params);
        } catch (final StripeException exception) {
            throw new GatewayException(exception.getMessage(), exception);
        }
    }

    private PaymentMethod retrievePaymentMethod(final String paymentMethodId) {
        try {
            return PaymentMethod.retrieve(paymentMethodId);
        } catch (final StripeException exception) {
            final String code = exception.getCode();
            if ("resource_missing".equals(code)) {
                throw new EntityDoesNotExistException(Payment.class, paymentMethodId, exception);
            }
            throw new GatewayException(exception.getMessage(), exception);
        }
    }

    private boolean paymentMethodBelongsToCustomer(final User user, final PaymentMethod paymentMethod) {
        final String externalId = user.getExternalId();
        if (externalId == null) {
            return false;
        }
        final String paymentMethodCustomerId = paymentMethod.getCustomer();
        if (paymentMethodCustomerId == null) {
            return false;
        }
        return externalId.equals(paymentMethodCustomerId);
    }

}
