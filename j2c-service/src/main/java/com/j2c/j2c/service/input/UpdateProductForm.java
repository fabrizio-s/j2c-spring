package com.j2c.j2c.service.input;

import com.j2c.j2c.service.validation.NullOrNotBlank;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.util.Set;
import java.util.UUID;

import static com.j2c.j2c.domain.entity.MaxLengths.PRODUCT_DESCRIPTION_MAXLENGTH;
import static com.j2c.j2c.domain.entity.MaxLengths.PRODUCT_NAME_MAXLENGTH;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UpdateProductForm {

    @NullOrNotBlank
    @Size(max = PRODUCT_NAME_MAXLENGTH)
    @Schema(example = "Cool T-shirt")
    private final String name;

    @NullOrNotBlank
    @Size(max = PRODUCT_DESCRIPTION_MAXLENGTH)
    @Schema(example = "The coolest in town.")
    private final String description;

    @PositiveOrZero
    @Schema(example = "400")
    private final Long price;

    @Schema(description = "The id returned after successfully uploading an image")
    private final UUID newImageId;

    @Schema(example = "22")
    private final Long defaultVariantId;

    @Schema(example = "4")
    private final Long categoryId;

    @Schema(description = "List of ids of the tags to tag the product with")
    private final Set<@NotNull Long> tagsToAdd;

    @Schema(description = "List of ids of the tags to remove from this product")
    private final Set<@NotNull Long> tagsToRemove;

}
