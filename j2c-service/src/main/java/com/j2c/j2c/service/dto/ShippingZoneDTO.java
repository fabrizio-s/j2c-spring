package com.j2c.j2c.service.dto;

import com.neovisionaries.i18n.CountryCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Set;

@Getter
@Builder
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class ShippingZoneDTO {

    @ToString.Include
    @EqualsAndHashCode.Include
    @Schema(example = "5")
    private final Long id;

    @Schema(example = "Europe")
    private final String name;

    @Schema(hidden = true)
    private final Set<CountryCode> countries;

    @Schema(hidden = true)
    private final Set<CountryCode> addedCountries;

    @Schema(hidden = true)
    private final Set<CountryCode> removedCountries;

    public static class ShippingZoneDTOBuilder implements DTOBuilder<ShippingZoneDTO> {}

}
