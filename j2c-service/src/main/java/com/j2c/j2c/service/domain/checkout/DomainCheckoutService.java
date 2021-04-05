package com.j2c.j2c.service.domain.checkout;

import com.j2c.j2c.domain.entity.*;
import com.j2c.j2c.domain.entity.Checkout.PreCheckoutLine;
import com.j2c.j2c.domain.exception.EntityDoesNotExistException;
import com.j2c.j2c.domain.repository.*;
import com.j2c.j2c.service.exception.ResourceAlreadyExistsException;
import com.j2c.j2c.service.gateway.PaymentGateway;
import com.j2c.j2c.service.input.*;
import com.j2c.j2c.service.mapper.AddressVOMapper;
import com.j2c.j2c.service.util.MergedLines;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.j2c.j2c.domain.exception.DomainErrorMessages.CHECKOUT_ALREADY_EXISTS;
import static com.j2c.j2c.domain.util.J2cUtils.optional;

@Service
@Validated
@Transactional
@RequiredArgsConstructor
public class DomainCheckoutService {

    private final PaymentGateway paymentGateway;
    private final UserRepository userRepository;
    private final UserAddressRepository userAddressRepository;
    private final CheckoutRepository checkoutRepository;
    private final CheckoutLineRepository checkoutLineRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository variantRepository;
    private final ConfigurationRepository configurationRepository;
    private final ShippingMethodRepository shippingMethodRepository;
    private final OrderRepository orderRepository;
    private final AddressVOMapper addressMapper;

    public CreateCheckoutResult checkout(
            @NotNull final Long customerId,
            @NotBlank final String ipAddress,
            @NotNull @Valid final CreateCheckoutForm form
    ) {
        final User customer = findCustomer(customerId);

        final List<PreCheckoutLine> lines = getPreCheckoutLines(form.getLines());

        final Configuration configuration = configurationRepository.getConfiguration();

        final Checkout checkout = checkoutRepository.save(
                Checkout.builder()
                        .customer(customer)
                        .lines(lines)
                        .email(form.getEmail())
                        .currency(configuration.getCurrency())
                        .ipAddress(ipAddress)
                        .massUnit(configuration.getMassUnit())
                        .build()
        );

        if (!checkout.isShippingRequired()) {
            paymentRequest(checkout);
        }

        return CreateCheckoutResult.builder()
                .createdCheckout(checkout)
                .createdLines(checkoutLineRepository.saveAll(checkout.getLines()))
                .build();
    }

    public Checkout createShippingAddress(
            @NotNull final Long checkoutId,
            @NotNull @Valid final CreateCheckoutShippingAddressForm form
    ) {
        final Checkout checkout = checkoutRepository.findById(checkoutId);

        final Address shippingAddress = addressMapper.fromCreateForm(form.getAddress());

        checkout.addShippingAddress(shippingAddress);

        optional(form.getSaveCustomerAddresses()).ifPresent(checkout::setSaveCustomerAddresses);

        return checkout;
    }

    public Checkout updateShippingAddress(
            @NotNull final Long checkoutId,
            @NotNull @Valid final UpdateCheckoutShippingAddressForm form
    ) {
        final Checkout checkout = checkoutRepository.findById(checkoutId);

        final Address shippingAddress = optional(form.getAddress())
                .map(addressForm -> addressMapper.fromUpdateForm(checkout.getShippingAddress(), addressForm))
                .orElse(null);

        checkout.setShippingAddress(shippingAddress);

        optional(form.getSaveCustomerAddresses()).ifPresent(checkout::setSaveCustomerAddresses);

        return checkout;
    }

    public Checkout setShippingAddress(
            @NotNull final Long checkoutId,
            @NotNull @Valid final SetCheckoutShippingAddressForm form
    ) {
        final Checkout checkout = checkoutRepository.findById(checkoutId);

        final UserAddress userAddress = userAddressRepository.findById(form.getAddressId())
                .verifyBelongsToUser(checkout.getCustomer());

        checkout.setShippingAddress(userAddress.getAddress());

        return checkout;
    }

    public Checkout setShippingMethod(
            @NotNull final Long checkoutId,
            @NotNull @Valid final SetCheckoutShippingMethodForm form
    ) {
        final Checkout checkout = checkoutRepository.findById(checkoutId);

        final ShippingMethod shippingMethod = shippingMethodRepository.findById(form.getShippingMethodId());

        checkout.setShippingMethod(shippingMethod);

        if (checkout.hasPayment()) {
            final Optional<Payment> payment = paymentGateway.findById(checkout.getPaymentId());
            payment.ifPresent(p -> p.update(checkout));
        } else {
            paymentRequest(checkout);
        }

        return checkout;
    }

    public Checkout createAddress(
            @NotNull final Long checkoutId,
            @NotNull @Valid final CreateCheckoutAddressForm form
    ) {
        final Checkout checkout = checkoutRepository.findById(checkoutId);

        final Address address = addressMapper.fromCreateForm(form.getAddress());

        checkout.addAddress(address);

        optional(form.getSavePaymentMethodAsDefault()).ifPresent(checkout::setSavePaymentMethodAsDefault);
        optional(form.getSaveCustomerAddresses()).ifPresent(checkout::setSaveCustomerAddresses);

        return checkout;
    }

    public Checkout updateAddress(
            @NotNull final Long checkoutId,
            @NotNull @Valid final UpdateCheckoutAddressForm form
    ) {
        final Checkout checkout = checkoutRepository.findById(checkoutId);

        final Address address = optional(form.getAddress())
                .map(addressForm -> addressMapper.fromUpdateForm(checkout.getAddress(), addressForm))
                .orElse(null);

        checkout.setAddress(address);

        optional(form.getSavePaymentMethodAsDefault()).ifPresent(checkout::setSavePaymentMethodAsDefault);
        optional(form.getSaveCustomerAddresses()).ifPresent(checkout::setSaveCustomerAddresses);

        return checkout;
    }

    public Checkout setAddress(
            @NotNull final Long checkoutId,
            @NotNull @Valid final SetCheckoutAddressForm form
    ) {
        final Checkout checkout = checkoutRepository.findById(checkoutId);

        final UserAddress userAddress = userAddressRepository.findById(form.getAddressId())
                .verifyBelongsToUser(checkout.getCustomer());

        checkout.setAddress(userAddress.getAddress());

        optional(form.getSavePaymentMethodAsDefault()).ifPresent(checkout::setSavePaymentMethodAsDefault);

        return checkout;
    }

    public Checkout useSingleAddress(
            @NotNull final Long checkoutId,
            @NotNull @Valid final UseSingleAddressForm form
    ) {
        final Checkout checkout = checkoutRepository.findById(checkoutId);

        optional(form.getUseSingleAddress()).ifPresent(checkout::useSingleAddress);
        optional(form.getSavePaymentMethodAsDefault()).ifPresent(checkout::setSavePaymentMethodAsDefault);

        return checkout;
    }

    public CompleteCheckoutResult complete(@NotNull final Long checkoutId) {
        final Checkout checkout = checkoutRepository.findById(checkoutId);

        final String paymentId = checkout.getPaymentId();

        final Payment payment = paymentGateway.findById(paymentId)
                .orElseThrow(() -> new EntityDoesNotExistException(Payment.class, paymentId));

        final Order order = orderRepository.save(checkout.complete(payment));

        checkoutRepository.remove(checkout);

        return CompleteCheckoutResult.builder()
                .createdOrder(order)
                .createdLines(order.getLines())
                .build();
    }

    public void cancel(@NotNull final Long checkoutId) {
        final Checkout checkout = checkoutRepository.findById(checkoutId);

        paymentGateway.findById(checkout.getPaymentId())
                .ifPresent(Payment::cancel);

        checkoutRepository.remove(checkout);
    }

    private void preLoadProducts(final List<ProductVariant> variants) {
        final Set<Long> ids = variants.stream()
                .map(ProductVariant::getProduct)
                .map(Product::getId)
                .collect(Collectors.toSet());
        productRepository.findAllByIdDoNotThrow(ids);
    }

    private User findCustomer(final Long customerId) {
        final User customer = userRepository.findById(customerId);
        if (checkoutRepository.existsById(customerId)) {
            throw new ResourceAlreadyExistsException(String.format(CHECKOUT_ALREADY_EXISTS, customerId));
        }
        return customer;
    }

    private List<PreCheckoutLine> getPreCheckoutLines(final List<Line> lines) {
        final MergedLines mergedLines = MergedLines.merge(lines);

        final List<ProductVariant> variants = variantRepository.findAllById(mergedLines.getIds());

        preLoadProducts(variants);

        return variants.stream()
                .map(v -> PreCheckoutLine.builder()
                        .variant(v)
                        .quantity(mergedLines.getQuantity(v.getId()))
                        .build())
                .collect(Collectors.toList());
    }

    private void paymentRequest(final Checkout checkout) {
        final User customer = checkout.getCustomer();
        String customerId = customer.getExternalId();
        if (customerId == null) {
            customerId = paymentGateway.createCustomer();
            customer.setExternalId(customerId);
        }
        final Payment payment = paymentGateway.request(checkout.getTotalPrice(), checkout.getCurrency(), customerId);
        checkout.setPayment(payment);
    }

}
