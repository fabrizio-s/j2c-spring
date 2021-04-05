package com.j2c.j2c.service.mapper;

import com.google.common.collect.Sets;
import com.j2c.j2c.domain.entity.*;
import com.j2c.j2c.service.domain.shipping.CreateShippingZoneResult;
import com.j2c.j2c.service.domain.shipping.UpdateShippingZoneResult;
import com.j2c.j2c.service.dto.*;
import com.j2c.j2c.service.dto.ShippingZoneDTO.ShippingZoneDTOBuilder;
import org.springframework.data.domain.Page;
import com.neovisionaries.i18n.CountryCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class ShippingServiceMapper {

    private final ShippingZoneDTOMapper zoneDTOMapper;
    private final ShippingMethodDTOMapper methodDTOMapper;
    private final ShippingCountryDTOMapper countryDTOMapper;

    public ShippingZoneDTO toZoneDTO(@NonNull final ShippingZone zone) {
        return zoneDTOMapper.fromEntity(zone).build();
    }

    public Page<ShippingZoneDTO> toZoneDTO(final Page<ShippingZone> zones) {
        return zoneDTOMapper.fromEntities(zones);
    }

    public ShippingZoneDTO toZoneDTO(@NonNull final CreateShippingZoneResult result) {
        final ShippingZoneDTOBuilder builder = zoneDTOMapper.fromEntity(result.getCreatedShippingZone());
        final Set<CountryCode> countryCodes = getCodes(result.getCountries());
        builder.countries(countryCodes);
        return builder.build();
    }

    public ShippingZoneDTO toZoneDTO(@NonNull final UpdateShippingZoneResult result) {
        final ShippingZoneDTOBuilder builder = zoneDTOMapper.fromEntity(result.getUpdatedZone());
        final Set<CountryCode> addedCountryCodes = getCodes(result.getAddedCountries());
        final Set<CountryCode> removedCountryCodes = getCodes(result.getRemovedCountries());
        builder.addedCountries(addedCountryCodes);
        builder.removedCountries(removedCountryCodes);
        return builder.build();
    }

    public Page<CountryCode> toCountryCodes(final Page<ShippingCountry> countries) {
        return countries.map(ShippingCountry::getCode);
    }

    public ShippingMethodDTO toMethodDTO(@NonNull final ShippingMethod method) {
        return methodDTOMapper.fromEntity(method).build();
    }

    public Page<ShippingMethodDTO> toMethodDTO(final Page<ShippingMethod> methods) {
        return methodDTOMapper.fromEntities(methods);
    }

    public ShippingCountryDTO toCountryDTO(@NonNull final ShippingCountry country) {
        return countryDTOMapper.fromEntity(country).build();
    }

    public Page<ShippingCountryDTO> toCountryDTO(final Page<ShippingCountry> countries) {
        return countryDTOMapper.fromEntities(countries);
    }

    private Set<CountryCode> getCodes(final Collection<ShippingCountry> countries) {
        if (countries == null || countries.isEmpty()) {
            return Collections.emptySet();
        }
        return countries.stream()
                .filter(Objects::nonNull)
                .map(ShippingCountry::getCode)
                .collect(Sets.toImmutableEnumSet());
    }

}
