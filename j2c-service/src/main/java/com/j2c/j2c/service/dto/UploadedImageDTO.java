package com.j2c.j2c.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.UUID;

@Getter
@Builder
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class UploadedImageDTO {

    @ToString.Include
    @EqualsAndHashCode.Include
    @Schema(example = "c7dffaf8-e35a-48c7-9d4c-166125b51e47")
    private final UUID id;

    @Schema(example = "ac79a4b0f44609ac267bf1b19e7b861e")
    private final String filename;

    public static class UploadedImageDTOBuilder implements DTOBuilder<UploadedImageDTO> {}

}
