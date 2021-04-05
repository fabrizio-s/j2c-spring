package com.j2c.j2c.service.dto;

import com.j2c.j2c.domain.enums.MassUnit;
import com.neovisionaries.i18n.CurrencyCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConfigurationDTO {

    @Schema(example = "USD")
    private final CurrencyCode currency;

    @Schema(example = "g")
    private final MassUnit massUnit;

    public static class ConfigurationDTOBuilder implements DTOBuilder<ConfigurationDTO> {}

}
