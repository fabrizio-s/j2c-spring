package com.j2c.j2c.service.input;

import com.j2c.j2c.domain.enums.MassUnit;
import com.neovisionaries.i18n.CurrencyCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfigurationForm {

    @Schema(example = "USD")
    private final CurrencyCode currency;

    @Schema(example = "g")
    private final MassUnit massUnit;

}
