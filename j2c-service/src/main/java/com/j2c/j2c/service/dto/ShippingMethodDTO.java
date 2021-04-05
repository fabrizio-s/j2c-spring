package com.j2c.j2c.service.dto;

import com.j2c.j2c.domain.enums.ShippingMethodType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class ShippingMethodDTO {

    @ToString.Include
    @EqualsAndHashCode.Include
    @Schema(example = "17")
    private final Long id;

    @Schema(example = "OPS Royalty Shipping")
    private final String name;

    @Schema(example = "2")
    private final Long zoneId;

    @Schema(example = "Price")
    private final ShippingMethodType type;

    @Schema(example = "0")
    private final Long min;

    @Schema(example = "5000")
    private final Long max;

    @Schema(example = "499")
    private final Long rate;

    public static class ShippingMethodDTOBuilder implements DTOBuilder<ShippingMethodDTO> {}

}
