package com.j2c.j2c.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserDTO {

    @ToString.Include
    @EqualsAndHashCode.Include
    @Schema(example = "15")
    private final Long id;

    @Schema(example = "john.cena@example.com")
    private final String email;

    @Schema(hidden = true)
    private final String password;

    @Schema(example = "true")
    private final boolean enabled;

    @Schema(example = "true")
    private final boolean verified;

    private final LocalDateTime verifiedAt;

    @Schema(hidden = true)
    private final RoleDTO role;

    public static class UserDTOBuilder implements DTOBuilder<UserDTO> {}

}
