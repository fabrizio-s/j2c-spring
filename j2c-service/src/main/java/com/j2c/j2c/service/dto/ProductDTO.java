package com.j2c.j2c.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Builder
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProductDTO {

    @ToString.Include
    @EqualsAndHashCode.Include
    @Schema(example = "3")
    private final Long id;

    @Schema(example = "Gaming PC")
    private final String name;

    @Schema(example = "ac79a4b0f44609ac267bf1b19e7b861e")
    private final String imageFilename;

    @Schema(example = "It's shiny")
    private final String description;

    @Schema(example = "false")
    private final boolean digital;

    @Schema(example = "true")
    private final boolean published;

    private final LocalDateTime lastPublished;

    private final LocalDateTime lastUnpublished;

    @Schema(example = "3")
    private final Long defaultVariantId;

    @Schema(example = "49900")
    private final Long defaultPrice;

    @Schema(example = "43")
    private final Long categoryId;

    @Schema(hidden = true)
    private final ProductVariantDTO defaultVariant;

    @Schema(hidden = true)
    private final Set<Long> tags;

    @Schema(hidden = true)
    private final Set<Long> addedTags;

    @Schema(hidden = true)
    private final Set<Long> removedTags;

    public static class ProductDTOBuilder implements DTOBuilder<ProductDTO> {}

}
