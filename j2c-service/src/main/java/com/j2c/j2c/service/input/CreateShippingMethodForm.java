package com.j2c.j2c.service.input;

import com.j2c.j2c.domain.enums.ShippingMethodType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

import static com.j2c.j2c.domain.entity.MaxLengths.SHIPPINGMETHOD_NAME_MAXLENGTH;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateShippingMethodForm {

    @NotBlank
    @Size(max = SHIPPINGMETHOD_NAME_MAXLENGTH)
    @Schema(example = "JLS Fast Shipping")
    private final String name;

    @NotNull
    private final ShippingMethodType type;

    @NotNull
    @PositiveOrZero
    @Schema(example = "3000")
    private final Long min;

    @NotNull
    @PositiveOrZero
    @Schema(example = "6000")
    private final Long max;

    @NotNull
    @PositiveOrZero
    @Schema(example = "599")
    private final Long rate;

}
