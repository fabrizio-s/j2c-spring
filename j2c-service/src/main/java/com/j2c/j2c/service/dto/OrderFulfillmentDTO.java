package com.j2c.j2c.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Builder
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class OrderFulfillmentDTO {

    @ToString.Include
    @EqualsAndHashCode.Include
    @Schema(example = "67")
    private final Long id;

    @Schema(example = "DEF456")
    private final String trackingNumber;

    @Schema(example = "837")
    private final Long orderId;

    @Schema(example = "false")
    private final boolean completed;

    @Schema(hidden = true)
    private final List<OrderFulfillmentLineDTO> lines;

    @Schema(hidden = true)
    private final List<OrderFulfillmentLineDTO> addedLines;

    @Schema(hidden = true)
    private final List<OrderFulfillmentLineDTO> updatedLines;

    public static class OrderFulfillmentDTOBuilder implements DTOBuilder<OrderFulfillmentDTO> {}

}
