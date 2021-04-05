package com.j2c.j2c.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserAddressDTO {

    @ToString.Include
    @EqualsAndHashCode.Include
    @Schema(example = "927")
    private final Long id;

    @Schema(example = "102")
    private final Long userId;

    private final AddressDTO address;

    public static class UserAddressDTOBuilder implements DTOBuilder<UserAddressDTO> {}

}
