package com.j2c.j2c.service.dto;

import com.j2c.j2c.domain.enums.MassUnit;
import com.neovisionaries.i18n.CurrencyCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Builder
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class CheckoutDTO {

    @ToString.Include
    @EqualsAndHashCode.Include
    @Schema(example = "172")
    private final Long id;

    @Schema(example = "461")
    private final Long customerId;

    @Schema(example = "larry@example.com")
    private final String email;

    @Schema(example = "USD")
    private final CurrencyCode currency;

    @Schema(example = "1499")
    private final Long price;

    @Schema(example = "ABC123")
    private final String paymentToken;

    @Schema(example = "127.0.0.1")
    private final String ipAddress;

    @Schema(example = "5678")
    private final Integer totalMass;

    @Schema(example = "g")
    private final MassUnit massUnit;

    private final AddressDTO address;

    @Schema(example = "true")
    private final boolean shippingRequired;

    private final ShippingMethodDetailsDTO shippingMethodDetails;

    @Schema(example = "true")
    private final Boolean usesSingleAddress;

    @Schema(example = "true")
    private final Boolean savePaymentMethodAsDefault;

    @Schema(example = "true")
    private final Boolean saveCustomerAddresses;

    private final AddressDTO shippingAddress;

    @Schema(hidden = true)
    private final List<CheckoutLineDTO> lines;

    public static class CheckoutDTOBuilder implements DTOBuilder<CheckoutDTO> {}

}
