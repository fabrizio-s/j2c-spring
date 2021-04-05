package com.j2c.j2c.service.application;

import com.j2c.j2c.service.dto.ShippingCountryDTO;
import com.j2c.j2c.service.dto.ShippingMethodDTO;
import com.j2c.j2c.service.dto.ShippingZoneDTO;
import com.j2c.j2c.service.input.CreateShippingMethodForm;
import com.j2c.j2c.service.input.CreateShippingZoneForm;
import com.j2c.j2c.service.input.UpdateShippingMethodForm;
import com.j2c.j2c.service.input.UpdateShippingZoneForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.neovisionaries.i18n.CountryCode;

import javax.validation.constraints.NotNull;

public interface ShippingService {

    ShippingZoneDTO findZone(@NotNull Long zoneId);

    Page<ShippingZoneDTO> findAllZones(@NotNull Pageable pageable);

    Page<CountryCode> findZoneCountries(
            @NotNull Long zoneId,
            @NotNull Pageable pageable
    );

    Page<ShippingMethodDTO> findMethods(
            @NotNull Long zoneId,
            @NotNull Pageable pageable
    );

    Page<ShippingMethodDTO> findMethodsForCheckout(
            @NotNull Long zoneId,
            @NotNull Long checkoutId,
            @NotNull Pageable pageable
    );

    ShippingMethodDTO findMethod(@NotNull Long zoneId, @NotNull Long methodId);

    Page<ShippingCountryDTO> findAllCountries(@NotNull Pageable pageable);

    Page<ShippingCountryDTO> findAllUnusedCountries(@NotNull Pageable pageable);

    ShippingCountryDTO findCountry(@NotNull CountryCode code);

    ShippingZoneDTO createZone(CreateShippingZoneForm form);

    ShippingZoneDTO updateZone(
            Long zoneId,
            UpdateShippingZoneForm form
    );

    void deleteZone(Long zoneId);

    ShippingMethodDTO createMethod(
            Long zoneId,
            CreateShippingMethodForm form
    );

    ShippingMethodDTO updateMethod(
            Long zoneId,
            Long methodId,
            UpdateShippingMethodForm form
    );

    void deleteMethod(Long zoneId, Long methodId);

}
