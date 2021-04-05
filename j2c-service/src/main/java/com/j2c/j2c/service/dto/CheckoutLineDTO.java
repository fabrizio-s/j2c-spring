package com.j2c.j2c.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class CheckoutLineDTO {

    @ToString.Include
    @EqualsAndHashCode.Include
    @Schema(example = "97")
    private final Long id;

    @Schema(example = "16")
    private final Long checkoutId;

    @Schema(example = "Cool T-Shirt")
    private final String productName;

    @Schema(example = "XL")
    private final String variantName;

    @Schema(example = "73")
    private final Long productId;

    @Schema(example = "1799")
    private final Long unitPriceAmount;

    @Schema(example = "1")
    private final int quantity;

    @Schema(example = "true")
    private final boolean shippingRequired;

    public static class CheckoutLineDTOBuilder implements DTOBuilder<CheckoutLineDTO> {}

}
