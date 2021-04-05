package com.j2c.j2c.service.dto;

import com.neovisionaries.i18n.CountryCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class ShippingCountryDTO {

    @ToString.Include
    @EqualsAndHashCode.Include
    @Schema(example = "US")
    private final CountryCode code;

    @Schema(example = "312")
    private final Long zoneId;

    public static class ShippingCountryDTOBuilder implements DTOBuilder<ShippingCountryDTO> {}

}
