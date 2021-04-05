package com.j2c.j2c.service.input;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.Valid;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UpdateCheckoutShippingAddressForm {

    @Schema(example = "true",
            description = "Add the address(es) used during checkout to the user's addresses")
    private final Boolean saveCustomerAddresses;

    @Valid
    private final UpdateAddressForm address;

}
