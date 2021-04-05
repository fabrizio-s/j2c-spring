package com.j2c.j2c.service.dto;

import com.j2c.j2c.domain.enums.RoleType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Set;

@Getter
@Builder
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class RoleDTO {

    @ToString.Include
    @EqualsAndHashCode.Include
    @Schema(example = "Customer")
    private final RoleType type;

    @Schema(hidden = true)
    private final Set<String> authorities;

    public static class RoleDTOBuilder implements DTOBuilder<RoleDTO> {}

}
