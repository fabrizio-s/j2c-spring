package com.j2c.j2c.web.controller;

import com.j2c.j2c.service.application.CheckoutService;
import com.j2c.j2c.service.dto.CheckoutDTO;
import com.j2c.j2c.service.dto.CheckoutLineDTO;
import com.j2c.j2c.service.dto.OrderDTO;
import com.j2c.j2c.service.input.*;
import com.j2c.j2c.service.input.SetCheckoutShippingAddressForm;
import com.j2c.j2c.service.input.SetCheckoutShippingMethodForm;
import com.j2c.j2c.web.security.annotation.HasReadAccess;
import com.j2c.j2c.web.security.annotation.IsCheckoutOwnerOrHasWriteAccess;
import com.j2c.j2c.web.security.annotation.IsCheckoutOwnerOrHasReadAccess;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import static com.j2c.j2c.domain.enums.Authorities.READ_ACCESS;
import static com.j2c.j2c.domain.enums.Authorities.WRITE_CHECKOUT;
import static com.j2c.j2c.web.security.util.AuthenticationUtils.getUserId;

@RestController
@RequiredArgsConstructor
@Tag(name = "Checkout", description = "Endpoints related to checkout sessions")
public class CheckoutController {

    private final CheckoutService checkoutService;

    @GetMapping(value = "/api/checkouts",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @HasReadAccess
    @Operation(security = @SecurityRequirement(name = "JWT"),
            summary = "Retrieves all the currently active checkout sessions",
            description = "Requires " + READ_ACCESS + " authority (Viewer, Staff, Admin).")
    public Page<CheckoutDTO> getAll(@ParameterObject final Pageable pageable) {
        return checkoutService.findAll(pageable);
    }

    @GetMapping(value = "/api/checkouts/{checkoutId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @IsCheckoutOwnerOrHasReadAccess
    @Operation(security = @SecurityRequirement(name = "JWT"),
            summary = "Retrieves a checkout session by its id",
            description = "Requires ownership of the resource or " + READ_ACCESS + " authority (Viewer, Staff, Admin).")
    public CheckoutDTO get(@PathVariable final Long checkoutId) {
        return checkoutService.find(checkoutId);
    }

    @GetMapping(value = "/api/checkouts/{checkoutId}/lines",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @IsCheckoutOwnerOrHasReadAccess
    @Operation(security = @SecurityRequirement(name = "JWT"),
            summary = "Retrieves the lines of the checkout session with the supplied id",
            description = "Requires ownership of the resource or " + READ_ACCESS + " authority (Viewer, Staff, Admin).")
    public Page<CheckoutLineDTO> getLines(
            @PathVariable final Long checkoutId,
            @ParameterObject final Pageable pageable
    ) {
        return checkoutService.findLines(checkoutId, pageable);
    }

    @GetMapping(value = "/api/checkouts/{checkoutId}/lines/{lineId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @IsCheckoutOwnerOrHasReadAccess
    @Operation(security = @SecurityRequirement(name = "JWT"),
            summary = "Retrieves a single line by id of the specified checkout session",
            description = "Requires ownership of the resource or " + READ_ACCESS + " authority (Viewer, Staff, Admin).")
    public CheckoutLineDTO getLine(
            @PathVariable final Long checkoutId,
            @PathVariable final Long lineId
    ) {
        return checkoutService.findLine(checkoutId, lineId);
    }

    @PostMapping(value = "/api/checkouts",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("authenticated")
    @Operation(security = @SecurityRequirement(name = "JWT"),
            summary = "Creates a new checkout session for the authenticated user",
            description = "Returns the created checkout session and its lines. " +
                    "Must be authenticated to perform, and a user may not have more than one active checkout session at a time.")
    public CheckoutDTO checkout(
            final HttpServletRequest request,
            @RequestBody final CreateCheckoutForm payload
    ) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final Long userId = getUserId(authentication);
        return checkoutService.checkout(userId, request.getRemoteAddr(), payload);
    }

    @PostMapping(value = "/api/checkouts/{checkoutId}/shipping-address",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @IsCheckoutOwnerOrHasWriteAccess
    @Operation(security = @SecurityRequirement(name = "JWT"),
            summary = "Adds a shipping address to the currently authenticated user's checkout session",
            description = "Returns the updated checkout session. " +
                    "Requires ownership of the resource or " + WRITE_CHECKOUT + " authority (Admin).")
    public CheckoutDTO createShippingAddress(
            @PathVariable final Long checkoutId,
            @RequestBody final CreateCheckoutShippingAddressForm payload
    ) {
        return checkoutService.createShippingAddress(checkoutId, payload);
    }

    @PatchMapping(value = "/api/checkouts/{checkoutId}/shipping-address",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @IsCheckoutOwnerOrHasWriteAccess
    @Operation(security = @SecurityRequirement(name = "JWT"),
            summary = "Updates the shipping address of the currently authenticated user's checkout session",
            description = "Returns the updated checkout session. " +
                    "The checkout session must already have an existing shipping address. " +
                    "Requires ownership of the resource or " + WRITE_CHECKOUT + " authority (Admin).")
    public CheckoutDTO updateShippingAddress(
            @PathVariable final Long checkoutId,
            @RequestBody final UpdateCheckoutShippingAddressForm payload
    ) {
        return checkoutService.updateShippingAddress(checkoutId, payload);
    }

    @PutMapping(value = "/api/checkouts/{checkoutId}/shipping-address",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @IsCheckoutOwnerOrHasWriteAccess
    @Operation(security = @SecurityRequirement(name = "JWT"),
            summary = "Set the shipping address of the currently authenticated user's checkout session to an existing user address",
            description = "Returns the updated checkout session. " +
                    "Requires ownership of the resource or " + WRITE_CHECKOUT + " authority (Admin).")
    public CheckoutDTO setShippingAddress(
            @PathVariable final Long checkoutId,
            @RequestBody final SetCheckoutShippingAddressForm payload
    ) {
        return checkoutService.setShippingAddress(checkoutId, payload);
    }

    @PutMapping(value = "/api/checkouts/{checkoutId}/shipping-method",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @IsCheckoutOwnerOrHasWriteAccess
    @Operation(security = @SecurityRequirement(name = "JWT"),
            summary = "Set the shipping method of the currently authenticated user's checkout session",
            description = "Returns the updated checkout session. " +
                    "Requires ownership of the resource or " + WRITE_CHECKOUT + " authority (Admin).")
    public CheckoutDTO setShippingMethod(
            @PathVariable final Long checkoutId,
            @RequestBody final SetCheckoutShippingMethodForm payload
    ) {
        return checkoutService.setShippingMethod(checkoutId, payload);
    }

    @PostMapping(value = "/api/checkouts/{checkoutId}/address",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @IsCheckoutOwnerOrHasWriteAccess
    @Operation(security = @SecurityRequirement(name = "JWT"),
            summary = "Add an address to the currently authenticated user's checkout session",
            description = "Returns the updated checkout session. " +
                    "Requires ownership of the resource or " + WRITE_CHECKOUT + " authority (Admin).")
    public CheckoutDTO createAddress(
            @PathVariable final Long checkoutId,
            @RequestBody final CreateCheckoutAddressForm payload
    ) {
        return checkoutService.createAddress(checkoutId, payload);
    }

    @PatchMapping(value = "/api/checkouts/{checkoutId}/address",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @IsCheckoutOwnerOrHasWriteAccess
    @Operation(security = @SecurityRequirement(name = "JWT"),
            summary = "Update the address of the currently authenticated user's checkout session",
            description = "Returns the updated checkout session. " +
                    "The checkout session must already have an existing address. " +
                    "Requires ownership of the resource or " + WRITE_CHECKOUT + " authority (Admin).")
    public CheckoutDTO updateAddress(
            @PathVariable final Long checkoutId,
            @RequestBody final UpdateCheckoutAddressForm payload
    ) {
        return checkoutService.updateAddress(checkoutId, payload);
    }

    @PutMapping(value = "/api/checkouts/{checkoutId}/address",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @IsCheckoutOwnerOrHasWriteAccess
    @Operation(security = @SecurityRequirement(name = "JWT"),
            summary = "Set the address of the currently authenticated user's checkout session to an existing user address",
            description = "Returns the updated checkout session. " +
                    "Requires ownership of the resource or " + WRITE_CHECKOUT + " authority (Admin).")
    public CheckoutDTO setAddress(
            @PathVariable final Long checkoutId,
            @RequestBody final SetCheckoutAddressForm payload
    ) {
        return checkoutService.setAddress(checkoutId, payload);
    }

    @PostMapping(value = "/api/checkouts/{checkoutId}/single-address",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @IsCheckoutOwnerOrHasWriteAccess
    @Operation(security = @SecurityRequirement(name = "JWT"),
            summary = "Set the currently authenticated user's checkout session to use the same address for both billing and shipping",
            description = "Returns the updated checkout session. " +
                    "Requires ownership of the resource or " + WRITE_CHECKOUT + " authority (Admin).")
    public CheckoutDTO useSingleAddress(
            @PathVariable final Long checkoutId,
            @RequestBody final UseSingleAddressForm payload
    ) {
        return checkoutService.useSingleAddress(checkoutId, payload);
    }

    @PostMapping(value = "/api/checkouts/{checkoutId}/complete",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @IsCheckoutOwnerOrHasWriteAccess
    @Operation(security = @SecurityRequirement(name = "JWT"),
            summary = "Completes a checkout session by capturing the required amount",
            description = "Returns the created order and its lines. " +
                    "The checkout session must have all required information " +
                    "(i.e. shipping method (if required), shipping address (if required), billing address and payment method). " +
                    "Requires ownership of the resource or " + WRITE_CHECKOUT + " authority (Admin).")
    public OrderDTO complete(@PathVariable final Long checkoutId) {
        return checkoutService.complete(checkoutId);
    }

    @DeleteMapping("/api/checkouts/{checkoutId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @IsCheckoutOwnerOrHasWriteAccess
    @Operation(security = @SecurityRequirement(name = "JWT"),
            summary = "Cancel the currently authenticated user's checkout session",
            description = "Requires ownership of the resource or " + WRITE_CHECKOUT + " authority (Admin).")
    public void cancel(@PathVariable final Long checkoutId) {
        checkoutService.cancel(checkoutId);
    }

}
