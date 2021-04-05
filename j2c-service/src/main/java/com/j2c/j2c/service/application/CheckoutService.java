package com.j2c.j2c.service.application;

import com.j2c.j2c.service.dto.CheckoutDTO;
import com.j2c.j2c.service.dto.CheckoutLineDTO;
import com.j2c.j2c.service.dto.OrderDTO;
import com.j2c.j2c.service.input.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.validation.constraints.*;

public interface CheckoutService {

    CheckoutDTO find(@NotNull Long checkoutId);

    Page<CheckoutDTO> findAll(@NotNull Pageable pageable);

    Page<CheckoutLineDTO> findLines(@NotNull Long checkoutId, @NotNull Pageable pageable);

    CheckoutLineDTO findLine(@NotNull Long checkoutId, @NotNull Long lineId);

    CheckoutDTO checkout(Long customerId, String ipAddress, CreateCheckoutForm form);

    CheckoutDTO createShippingAddress(Long checkoutId, CreateCheckoutShippingAddressForm form);

    CheckoutDTO updateShippingAddress(Long checkoutId, UpdateCheckoutShippingAddressForm form);

    CheckoutDTO setShippingAddress(Long checkoutId, SetCheckoutShippingAddressForm form);

    CheckoutDTO setShippingMethod(Long checkoutId, SetCheckoutShippingMethodForm form);

    CheckoutDTO createAddress(Long checkoutId, CreateCheckoutAddressForm form);

    CheckoutDTO updateAddress(Long checkoutId, UpdateCheckoutAddressForm form);

    CheckoutDTO setAddress(Long checkoutId, SetCheckoutAddressForm form);

    CheckoutDTO useSingleAddress(Long checkoutId, UseSingleAddressForm form);

    OrderDTO complete(Long checkoutId);

    void cancel(Long checkoutId);

}
