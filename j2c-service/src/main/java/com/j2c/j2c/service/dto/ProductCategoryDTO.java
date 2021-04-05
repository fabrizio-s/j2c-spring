package com.j2c.j2c.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProductCategoryDTO {

    @ToString.Include
    @EqualsAndHashCode.Include
    @Schema(example = "231")
    private final Long id;

    @Schema(example = "Computers")
    private final String name;

    @Schema(example = "Desktops, laptops, hardware, etc")
    private final String description;

    @Schema(example = "ac79a4b0f44609ac267bf1b19e7b861e")
    private final String imageFilename;

    @Schema(example = "87")
    private final Long rootId;

    @Schema(example = "53")
    private final Long parentId;

    public static class ProductCategoryDTOBuilder implements DTOBuilder<ProductCategoryDTO> {}

}
