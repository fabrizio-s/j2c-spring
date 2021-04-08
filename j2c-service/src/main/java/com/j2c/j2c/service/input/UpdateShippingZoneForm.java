package com.j2c.j2c.service.input;

import com.j2c.j2c.service.validation.NullOrNotBlank;
import com.neovisionaries.i18n.CountryCode;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

import static com.j2c.j2c.domain.entity.MaxLengths.SHIPPINGZONE_NAME_MAXLENGTH;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UpdateShippingZoneForm {

    @NullOrNotBlank
    @Size(max = SHIPPINGZONE_NAME_MAXLENGTH)
    @Schema(example = "Atlantis")
    private final String name;

    @ArraySchema(schema = @Schema(example = "US",
            description = "List of country codes to assign to the shipping zone"))
    private final Set<@NotNull CountryCode> countriesToAdd;

    @ArraySchema(schema = @Schema(example = "IT",
            description = "List of country codes to remove from the shipping zone"))
    private final Set<@NotNull CountryCode> countriesToRemove;

}
