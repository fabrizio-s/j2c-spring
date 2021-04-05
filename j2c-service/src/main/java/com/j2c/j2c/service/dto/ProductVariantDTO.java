package com.j2c.j2c.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Builder
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProductVariantDTO {

    @ToString.Include
    @EqualsAndHashCode.Include
    @Schema(example = "50")
    private final Long id;

    @Schema(example = "XL")
    private final String name;

    @Schema(example = "29")
    private final Long productId;

    @Schema(example = "330")
    private final Integer mass;

    @Schema(example = "799")
    private final Long price;

    @Schema(hidden = true)
    private final List<ProductVariantImageDTO> images;

    @Schema(hidden = true)
    private final List<ProductVariantImageDTO> addedImages;

    public static class ProductVariantDTOBuilder implements DTOBuilder<ProductVariantDTO> {}

}
