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
public class UseSingleAddressForm {

    @NotNull
    @Schema(description = "If true, will use the same address for both shipping and billing")
    private final Boolean useSingleAddress;

    @Schema(example = "true",
            description = "Save the payment method used during checkout as the default payment method for future purchases")
    private final Boolean savePaymentMethodAsDefault;

}
