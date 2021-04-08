package com.j2c.j2c.service.input;

import com.j2c.j2c.service.validation.NullOrNotBlank;
import com.neovisionaries.i18n.CountryCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static com.j2c.j2c.domain.entity.MaxLengths.*;
import static com.j2c.j2c.service.input.RegExp.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UpdateAddressForm {

    @NullOrNotBlank
    @Pattern(regexp = ADDRESS_NAME_PATTERN)
    @Size(max = ADDRESS_FIRSTNAME_MAXLENGTH)
    @Schema(example = "Peter")
    private final String firstName;

    @NullOrNotBlank
    @Pattern(regexp = ADDRESS_NAME_PATTERN)
    @Size(max = ADDRESS_LASTNAME_MAXLENGTH)
    @Schema(example = "Porker")
    private final String lastName;

    @NullOrNotBlank
    @Size(max = ADDRESS_STREETADDRESS_MAXLENGTH)
    @Schema(example = "Bowman Street 219")
    private final String streetAddress1;

    @NullOrNotBlank
    @Size(max = ADDRESS_STREETADDRESS_MAXLENGTH)
    private final String streetAddress2;

    @Schema(example = "BE")
    private final CountryCode country;

    @NullOrNotBlank
    @Size(max = ADDRESS_COUNTRYAREA_MAXLENGTH)
    @Schema(example = "Lazio")
    private final String countryArea;

    @NullOrNotBlank
    @Size(max = ADDRESS_CITY_MAXLENGTH)
    @Schema(example = "New York")
    private final String city;

    @NullOrNotBlank
    @Size(max = ADDRESS_CITYAREA_MAXLENGTH)
    @Schema(example = "Brooklyn")
    private final String cityArea;

    @NullOrNotBlank
    @Pattern(regexp = ADDRESS_POSTALCODE_PATTERN)
    @Size(max = ADDRESS_POSTALCODE_MAXLENGTH)
    @Schema(example = "11201")
    private final String postalCode;

    @NullOrNotBlank
    @Pattern(regexp = ADDRESS_PHONE_PATTERN)
    @Size(max = ADDRESS_PHONE_MAXLENGTH)
    @Schema(example = "1234567890")
    private final String phone1;

    @NullOrNotBlank
    @Pattern(regexp = ADDRESS_PHONE_PATTERN)
    @Size(max = ADDRESS_PHONE_MAXLENGTH)
    @Schema(example = "0987654321")
    private final String phone2;

}
