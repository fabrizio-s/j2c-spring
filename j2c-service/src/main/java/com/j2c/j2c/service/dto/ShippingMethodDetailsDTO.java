package com.j2c.j2c.service.dto;

import com.j2c.j2c.domain.enums.ShippingMethodType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@ToString(onlyExplicitlyIncluded = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class ShippingMethodDetailsDTO {

    @ToString.Include
    @Schema(example = "FedOx Standard Shipping")
    private final String name;

    @Schema(example = "799")
    private final Long amount;

    @Schema(example = "Weight")
    private final ShippingMethodType type;

}
