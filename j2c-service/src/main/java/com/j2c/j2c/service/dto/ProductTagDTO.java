package com.j2c.j2c.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProductTagDTO {

    @ToString.Include
    @EqualsAndHashCode.Include
    @Schema(example = "71")
    private final Long id;

    @Schema(example = "Dogs")
    private final String name;

    public static class ProductTagDTOBuilder implements DTOBuilder<ProductTagDTO> {}

}
