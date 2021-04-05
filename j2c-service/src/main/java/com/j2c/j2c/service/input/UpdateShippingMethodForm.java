package com.j2c.j2c.service.input;

import com.j2c.j2c.service.validation.NullOrNotBlank;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

import static com.j2c.j2c.domain.entity.MaxLengths.SHIPPINGMETHOD_NAME_MAXLENGTH;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UpdateShippingMethodForm {

    @NullOrNotBlank
    @Size(max = SHIPPINGMETHOD_NAME_MAXLENGTH)
    @Schema(example = "FedOx Standard Shipping")
    private final String name;

    @PositiveOrZero
    @Schema(example = "3000")
    private final Long min;

    @PositiveOrZero
    @Schema(example = "6000")
    private final Long max;

    @PositiveOrZero
    @Schema(example = "599")
    private final Long rate;

}
