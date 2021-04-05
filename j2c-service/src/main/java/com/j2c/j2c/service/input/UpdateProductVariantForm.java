package com.j2c.j2c.service.input;

import com.j2c.j2c.service.validation.NullOrNotBlank;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.util.Set;
import java.util.UUID;

import static com.j2c.j2c.domain.entity.MaxLengths.PRODUCTVARIANT_NAME_MAXLENGTH;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UpdateProductVariantForm {

    @NullOrNotBlank
    @Size(max = PRODUCTVARIANT_NAME_MAXLENGTH)
    @Schema(example = "XL")
    private final String name;

    @Positive
    @Schema(example = "200")
    private final Integer mass;

    @PositiveOrZero
    @Schema(example = "799",
            description = "If provided, this value will be used instead of the parent product's price to calculate the total amount during checkout")
    private final Long price;

    @Schema(description = "A list uploaded image ids")
    private final Set<@NotNull UUID> imagesToAddIds;

    @Schema(description = "A list variant image ids")
    private final Set<@NotNull Long> imagesToRemoveIds;

    // TODO: add boolean to remove price (if true, set price to null)

}
