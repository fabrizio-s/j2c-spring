package com.j2c.j2c.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.UUID;

@Getter
@Builder
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserVerificationTokenDTO {

    @ToString.Include
    @EqualsAndHashCode.Include
    @Schema(example = "c7dffaf8-e35a-48c7-9d4c-166125b51e47")
    private final UUID id;

    @Schema(hidden = true)
    private final Long userId;

    public static class UserVerificationTokenDTOBuilder implements DTOBuilder<UserVerificationTokenDTO> {}

}
