package com.j2c.j2c.service.test;

import com.j2c.j2c.domain.entity.*;
import com.j2c.j2c.domain.exception.EntityDoesNotExistException;
import com.neovisionaries.i18n.CurrencyCode;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestComponent;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@TestComponent
@RequiredArgsConstructor
public class CheckoutServiceStubber {

    private final MockBeanProvider mockBeanProvider;

    @Builder(builderClassName = "MockCheckout",
            builderMethodName = "checkout",
            buildMethodName = "stub")
    private void _checkout(
            final User customer,
            final Checkout checkout,
            final List<ProductVariant> variants,
            final Configuration configuration,
            final Payment payment
    ) {
        mockFindUserById(customer);
        mockCheckoutExistsById(checkout);
        mockFindAllLinesById(variants);
        mockGetConfiguration(configuration);
        mockRequestPayment(payment);
    }

    @Builder(builderClassName = "MockCreateShippingAddress",
            builderMethodName = "createShippingAddress",
            buildMethodName = "stub")
    private void _createShippingAddress(final Checkout checkout) {
        mockFindCheckoutById(checkout);
    }

    @Builder(builderClassName = "MockUpdateShippingAddress",
            builderMethodName = "updateShippingAddress",
            buildMethodName = "stub")
    private void _updateShippingAddress(final Checkout checkout) {
        mockFindCheckoutById(checkout);
    }

    @Builder(builderClassName = "MockSetShippingAddress",
            builderMethodName = "setShippingAddress",
            buildMethodName = "stub")
    private void _setShippingAddress(
            final Checkout checkout,
            final UserAddress userAddress
    ) {
        mockFindCheckoutById(checkout);
        mockFindUserAddressById(userAddress);
    }

    @Builder(builderClassName = "MockSetShippingMethod",
            builderMethodName = "setShippingMethod",
            buildMethodName = "stub")
    private void _setShippingMethod(
            final Checkout checkout,
            final ShippingMethod shippingMethod,
            final Payment payment
    ) {
        mockFindCheckoutById(checkout);
        mockFindShippingMethodById(shippingMethod);
        if (checkout != null) {
            if (checkout.hasPayment()) {
                mockFindPaymentById(payment);
            } else {
                mockRequestPayment(payment);
            }
        }
    }

    @Builder(builderClassName = "MockCreateAddress",
            builderMethodName = "createAddress",
            buildMethodName = "stub")
    private void _createAddress(final Checkout checkout) {
        mockFindCheckoutById(checkout);
    }

    @Builder(builderClassName = "MockUpdateAddress",
            builderMethodName = "updateAddress",
            buildMethodName = "stub")
    private void _updateAddress(final Checkout checkout) {
        mockFindCheckoutById(checkout);
    }

    @Builder(builderClassName = "MockSetAddress",
            builderMethodName = "setAddress",
            buildMethodName = "stub")
    private void _setAddress(
            final Checkout checkout,
            final UserAddress userAddress
    ) {
        mockFindCheckoutById(checkout);
        mockFindUserAddressById(userAddress);
    }

    @Builder(builderClassName = "MockUseSingleAddress",
            builderMethodName = "useSingleAddress",
            buildMethodName = "stub")
    private void _useSingleAddress(final Checkout checkout) {
        mockFindCheckoutById(checkout);
    }

    @Builder(builderClassName = "MockComplete",
            builderMethodName = "complete",
            buildMethodName = "stub")
    private void _complete(
            final Checkout checkout,
            final Payment payment
    ) {
        mockFindCheckoutById(checkout);
        mockFindPaymentById(payment);
    }

    @Builder(builderClassName = "MockCancel",
            builderMethodName = "cancel",
            buildMethodName = "stub")
    private void _cancel(
            final Checkout checkout,
            final Payment payment
    ) {
        mockFindCheckoutById(checkout);
        mockFindPaymentById(payment);
    }

    private void mockFindUserById(final User user) {
        if (user != null) {
            when(mockBeanProvider.getUserRepository().findById(user.getId()))
                    .thenReturn(Optional.of(user));
        }
    }

    private void mockFindCheckoutById(final Checkout checkout) {
        if (checkout != null) {
            when(mockBeanProvider.getCheckoutRepository().findById(checkout.getId()))
                    .thenReturn(Optional.of(checkout));
        }
    }

    private void mockCheckoutExistsById(final Checkout checkout) {
        if (checkout != null) {
            when(mockBeanProvider.getCheckoutRepository().existsById(checkout.getId()))
                    .thenReturn(true);
        }
    }

    private void mockGetConfiguration(final Configuration configuration) {
        if (configuration != null) {
            when(mockBeanProvider.getConfigurationRepository().findByProfile(isNotNull()))
                    .thenReturn(Optional.of(configuration));
        }
    }

    private void mockFindUserAddressById(final UserAddress userAddress) {
        if (userAddress != null) {
            when(mockBeanProvider.getUserAddressRepository().findById(userAddress.getId()))
                    .thenReturn(Optional.of(userAddress));
        }
    }

    private void mockFindShippingMethodById(final ShippingMethod shippingMethod) {
        if (shippingMethod != null) {
            when(mockBeanProvider.getShippingMethodRepository().findById(shippingMethod.getId()))
                    .thenReturn(Optional.of(shippingMethod));
        }
    }

    private void mockRequestPayment(final Payment payment) {
        if (payment != null) {
            when(mockBeanProvider.getPaymentGateway().request(any(Long.class), any(CurrencyCode.class), anyString()))
                    .thenReturn(payment);
        }
    }

    private void mockFindPaymentById(final Payment payment) {
        if (payment != null) {
            when(mockBeanProvider.getPaymentGateway().findById(payment.getId()))
                    .thenReturn(Optional.of(payment));
        }
    }

    private void mockFindAllLinesById(final List<ProductVariant> variants) {
        if (variants != null) {
            when(mockBeanProvider.getProductVariantRepository().findAllById(anySet()))
                    .thenReturn(variants);
        }
    }

}
