package com.j2c.j2c.service.input;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SetCheckoutAddressForm {

    @Schema(example = "true")
    private final Boolean savePaymentMethodAsDefault;

    @NotNull
    @Schema(example = "77")
    private final Long addressId;

}
