package com.j2c.j2c.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class OrderFulfillmentLineDTO {

    @ToString.Include
    @EqualsAndHashCode.Include
    @Schema(example = "89")
    private final Long id;

    @Schema(example = "65")
    private final Long fulfillmentId;

    @Schema(example = "49")
    private final Long orderLineId;

    @Schema(example = "2")
    private final int quantity;

    public static class OrderFulfillmentLineDTOBuilder implements DTOBuilder<OrderFulfillmentLineDTO> {}

}
