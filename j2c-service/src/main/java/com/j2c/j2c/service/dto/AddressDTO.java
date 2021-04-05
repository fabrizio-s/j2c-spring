package com.j2c.j2c.service.dto;

import com.neovisionaries.i18n.CountryCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class AddressDTO {

    @Schema(example = "Larry")
    private final String firstName;

    @Schema(example = "Patterson")
    private final String lastName;

    @Schema(example = "Bowman Street 219")
    private final String streetAddress1;

    private final String streetAddress2;

    @Schema(example = "BE")
    private final CountryCode country;

    @Schema(example = "Lazio")
    private final String countryArea;

    @Schema(example = "New York")
    private final String city;

    @Schema(example = "Brooklyn")
    private final String cityArea;

    @Schema(example = "11201")
    private final String postalCode;

    @Schema(example = "1234567890")
    private final String phone1;

    @Schema(example = "0987654321")
    private final String phone2;

}
