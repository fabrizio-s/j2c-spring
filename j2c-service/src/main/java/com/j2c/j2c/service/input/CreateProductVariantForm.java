package com.j2c.j2c.service.input;

import com.j2c.j2c.service.validation.NullOrNotBlank;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.*;
import java.util.Set;
import java.util.UUID;

import static com.j2c.j2c.domain.entity.MaxLengths.PRODUCTVARIANT_NAME_MAXLENGTH;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateProductVariantForm {

    @NullOrNotBlank
    @Size(max = PRODUCTVARIANT_NAME_MAXLENGTH)
    @Schema(example = "M",
            description = "The name to assign to the product's current default variant. " +
                    "Required only if the product's current default variant has no name.")
    private final String defaultVariantName;

    @NotBlank
    @Size(max = PRODUCTVARIANT_NAME_MAXLENGTH)
    @Schema(example = "XL")
    private final String name;

    @Schema(description = "A list uploaded image ids")
    private final Set<@NotNull UUID> imageIds;

    @Positive
    @Schema(example = "200")
    private final Integer mass;

    @PositiveOrZero
    @Schema(example = "799",
            description = "If provided, this value will be used instead of the parent product's price to calculate the total amount during checkout")
    private final Long price;

}
