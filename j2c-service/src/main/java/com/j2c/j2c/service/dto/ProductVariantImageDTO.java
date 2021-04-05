package com.j2c.j2c.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProductVariantImageDTO {

    @ToString.Include
    @EqualsAndHashCode.Include
    @Schema(example = "835")
    private final Long id;

    @Schema(example = "361")
    private final Long variantId;

    @Schema(example = "ac79a4b0f44609ac267bf1b19e7b861e")
    private final String filename;

    public static class ProductVariantImageDTOBuilder implements DTOBuilder<ProductVariantImageDTO> {}

}
