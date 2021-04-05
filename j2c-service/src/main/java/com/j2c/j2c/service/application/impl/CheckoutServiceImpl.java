package com.j2c.j2c.service.application.impl;

import com.j2c.j2c.domain.entity.Checkout;
import com.j2c.j2c.domain.entity.CheckoutLine;
import com.j2c.j2c.domain.repository.CheckoutLineRepository;
import com.j2c.j2c.domain.repository.CheckoutRepository;
import com.j2c.j2c.service.application.CheckoutService;
import com.j2c.j2c.service.domain.checkout.CreateCheckoutResult;
import com.j2c.j2c.service.domain.checkout.CompleteCheckoutResult;
import com.j2c.j2c.service.domain.checkout.DomainCheckoutService;
import com.j2c.j2c.service.dto.CheckoutDTO;
import com.j2c.j2c.service.dto.CheckoutLineDTO;
import com.j2c.j2c.service.dto.OrderDTO;
import com.j2c.j2c.service.input.*;
import com.j2c.j2c.service.mapper.CheckoutServiceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Service
@Validated
@RequiredArgsConstructor
public class CheckoutServiceImpl implements CheckoutService {

    private final CheckoutServiceMapper mapper;
    private final DomainCheckoutService domainService;
    private final CheckoutRepository checkoutRepository;
    private final CheckoutLineRepository checkoutLineRepository;

    @Override
    public CheckoutDTO find(@NotNull final Long checkoutId) {
        final Checkout checkout = checkoutRepository.findById(checkoutId);
        return mapper.toCheckoutDTO(checkout);
    }

    @Override
    public Page<CheckoutDTO> findAll(@NotNull final Pageable pageable) {
        final Page<Checkout> checkouts = checkoutRepository.findAll(pageable);
        return mapper.toCheckoutDTO(checkouts);
    }

    @Override
    public Page<CheckoutLineDTO> findLines(@NotNull final Long checkoutId, @NotNull final Pageable pageable) {
        checkoutRepository.verifyExistsById(checkoutId);
        final Page<CheckoutLine> lines = checkoutLineRepository.findAllByCheckoutId(checkoutId, pageable);
        return mapper.toLineDTO(lines);
    }

    @Override
    public CheckoutLineDTO findLine(@NotNull final Long checkoutId, @NotNull final Long lineId) {
        final Checkout checkout = checkoutRepository.findById(checkoutId);
        final CheckoutLine line = checkoutLineRepository.findById(lineId)
                .verifyBelongsToCheckout(checkout);
        return mapper.toLineDTO(line);
    }

    @Override
    public CheckoutDTO checkout(final Long customerId, final String ipAddress, final CreateCheckoutForm form) {
        final CreateCheckoutResult result = domainService.checkout(customerId, ipAddress, form);
        return mapper.toCheckoutDTO(result);
    }

    @Override
    public CheckoutDTO createShippingAddress(final Long checkoutId, final CreateCheckoutShippingAddressForm form) {
        final Checkout updatedCheckout = domainService.createShippingAddress(checkoutId, form);
        return mapper.toCheckoutDTO(updatedCheckout);
    }

    @Override
    public CheckoutDTO updateShippingAddress(final Long checkoutId, final UpdateCheckoutShippingAddressForm form) {
        final Checkout updatedCheckout = domainService.updateShippingAddress(checkoutId, form);
        return mapper.toCheckoutDTO(updatedCheckout);
    }

    @Override
    public CheckoutDTO setShippingAddress(final Long checkoutId, final SetCheckoutShippingAddressForm form) {
        final Checkout updatedCheckout = domainService.setShippingAddress(checkoutId, form);
        return mapper.toCheckoutDTO(updatedCheckout);
    }

    @Override
    public CheckoutDTO setShippingMethod(final Long checkoutId, final SetCheckoutShippingMethodForm form) {
        final Checkout updatedCheckout = domainService.setShippingMethod(checkoutId, form);
        return mapper.toCheckoutDTO(updatedCheckout);
    }

    @Override
    public CheckoutDTO createAddress(final Long checkoutId, final CreateCheckoutAddressForm form) {
        final Checkout updatedCheckout = domainService.createAddress(checkoutId, form);
        return mapper.toCheckoutDTO(updatedCheckout);
    }

    @Override
    public CheckoutDTO updateAddress(final Long checkoutId, final UpdateCheckoutAddressForm form) {
        final Checkout updatedCheckout = domainService.updateAddress(checkoutId, form);
        return mapper.toCheckoutDTO(updatedCheckout);
    }

    @Override
    public CheckoutDTO setAddress(final Long checkoutId, final SetCheckoutAddressForm form) {
        final Checkout updatedCheckout = domainService.setAddress(checkoutId, form);
        return mapper.toCheckoutDTO(updatedCheckout);
    }

    @Override
    public CheckoutDTO useSingleAddress(final Long checkoutId, final UseSingleAddressForm form) {
        final Checkout updatedCheckout = domainService.useSingleAddress(checkoutId, form);
        return mapper.toCheckoutDTO(updatedCheckout);
    }

    @Override
    public OrderDTO complete(final Long checkoutId) {
        final CompleteCheckoutResult result = domainService.complete(checkoutId);
        return mapper.toOrderDTO(result);
    }

    @Override
    public void cancel(final Long checkoutId) {
        domainService.cancel(checkoutId);
    }

}
