package com.j2c.j2c.service.input;

import com.j2c.j2c.service.validation.NullOrNotBlank;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.*;
import java.util.Set;
import java.util.UUID;

import static com.j2c.j2c.domain.entity.MaxLengths.PRODUCT_DESCRIPTION_MAXLENGTH;
import static com.j2c.j2c.domain.entity.MaxLengths.PRODUCT_NAME_MAXLENGTH;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateProductForm {

    @NotBlank
    @Size(max = PRODUCT_NAME_MAXLENGTH)
    @Schema(example = "Cool T-shirt")
    private final String name;

    @NullOrNotBlank
    @Size(max = PRODUCT_DESCRIPTION_MAXLENGTH)
    @Schema(example = "The coolest in town.")
    private final String description;

    @Schema(description = "The id returned after successfully uploading an image")
    private final UUID imageId;

    @NotNull
    @Schema(example = "false")
    private final Boolean digital;

    @Positive
    @Schema(example = "400")
    private final Integer mass;

    @NotNull
    @PositiveOrZero
    @Schema(example = "400")
    private final Long price;

    @Schema(example = "11")
    private final Long categoryId;

    @Schema(description = "Ids of the tags with which to tag the product to be created")
    private final Set<@NotNull Long> tagIds;

}
