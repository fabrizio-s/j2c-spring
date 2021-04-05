package com.j2c.j2c.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class OrderLineDTO {

    @ToString.Include
    @EqualsAndHashCode.Include
    @Schema(example = "90")
    private final Long id;

    @Schema(example = "52")
    private final Long orderId;

    @Schema(example = "Sleeveless T-shirt")
    private final String productName;

    @Schema(example = "S")
    private final String variantName;

    @Schema(example = "43")
    private final Long productId;

    @Schema(example = "1099")
    private final Long unitPriceAmount;

    @Schema(example = "3")
    private final int quantity;

    @Schema(example = "1")
    private final int fulfilledQuantity;

    @Schema(example = "2")
    private final int reservedQuantity;

    @Schema(example = "true")
    private final boolean shippingRequired;

    public static class OrderLineDTOBuilder implements DTOBuilder<OrderLineDTO> {}

}
