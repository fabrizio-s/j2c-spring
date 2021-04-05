package com.j2c.j2c.service.domain.shipping;

import com.j2c.j2c.service.input.CreateShippingMethodForm;
import com.j2c.j2c.service.input.CreateShippingZoneForm;
import com.j2c.j2c.service.input.UpdateShippingMethodForm;
import com.j2c.j2c.service.input.UpdateShippingZoneForm;
import com.j2c.j2c.domain.entity.ShippingCountry;
import com.j2c.j2c.domain.entity.ShippingMethod;
import com.j2c.j2c.domain.entity.ShippingZone;
import com.j2c.j2c.domain.repository.ShippingCountryRepository;
import com.j2c.j2c.domain.repository.ShippingMethodRepository;
import com.j2c.j2c.domain.repository.ShippingZoneRepository;
import com.neovisionaries.i18n.CountryCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.*;

import static com.j2c.j2c.domain.util.J2cUtils.optional;

@Service
@Validated
@Transactional
@RequiredArgsConstructor
public class DomainShippingService {

    private final ShippingZoneRepository zoneRepository;
    private final ShippingCountryRepository countryRepository;
    private final ShippingMethodRepository methodRepository;

    public CreateShippingZoneResult createZone(@NotNull @Valid final CreateShippingZoneForm form) {
        final ShippingZone zone = zoneRepository.save(
                ShippingZone.builder()
                        .name(form.getName())
                        .build()
        );

        final List<ShippingCountry> countries = addCountries(zone, form.getCountries());

        return CreateShippingZoneResult.builder()
                .createdShippingZone(zone)
                .countries(countries)
                .build();
    }

    public UpdateShippingZoneResult updateZone(
            @NotNull final Long zoneId,
            @NotNull @Valid final UpdateShippingZoneForm form
    ) {
        final ShippingZone zone = zoneRepository.findById(zoneId);

        optional(form.getName()).ifPresent(zone::setName);

        final List<ShippingCountry> addedCountries = addCountries(zone, form.getCountriesToAdd());

        final List<ShippingCountry> removedCountries = removeCountries(zone, form.getCountriesToRemove());

        return UpdateShippingZoneResult.builder()
                .updatedZone(zone)
                .addedCountries(addedCountries)
                .removedCountries(removedCountries)
                .build();
    }

    public void deleteZone(@NotNull final Long zoneId) {
        final ShippingZone zone = zoneRepository.findById(zoneId);

        zoneRepository.remove(zone);
    }

    public ShippingMethod createMethod(
            @NotNull final Long zoneId,
            @NotNull @Valid final CreateShippingMethodForm form
    ) {
        final ShippingZone zone = zoneRepository.findById(zoneId);

        return methodRepository.save(
                ShippingMethod.builder()
                        .name(form.getName())
                        .type(form.getType())
                        .min(form.getMin())
                        .max(form.getMax())
                        .rate(form.getRate())
                        .zone(zone)
                        .build()
        );
    }

    public ShippingMethod updateMethod(
            @NotNull final Long zoneId,
            @NotNull final Long methodId,
            @NotNull @Valid final UpdateShippingMethodForm form
    ) {
        final ShippingZone zone = zoneRepository.findById(zoneId);

        final ShippingMethod method = methodRepository.findById(methodId)
                .verifyBelongsToZone(zone);

        optional(form.getName()).ifPresent(method::setName);
        optional(form.getMin()).ifPresent(method::setMin);
        optional(form.getMax()).ifPresent(method::setMax);
        optional(form.getRate()).ifPresent(method::setRate);

        return method;
    }

    public void deleteMethod(@NotNull final Long zoneId, @NotNull final Long methodId) {
        final ShippingZone zone = zoneRepository.findById(zoneId);

        final ShippingMethod method = methodRepository.findById(methodId)
                .verifyBelongsToZone(zone);

        methodRepository.remove(method);
    }

    private List<ShippingCountry> addCountries(final ShippingZone zone, final Set<CountryCode> codes) {
        if (codes == null) {
            return Collections.emptyList();
        }
        final List<ShippingCountry> countries = countryRepository.findByCountryCodes(codes);
        return zone.addCountries(countries);
    }

    private List<ShippingCountry> removeCountries(final ShippingZone zone, final Set<CountryCode> codes) {
        if (codes == null) {
            return Collections.emptyList();
        }
        final List<ShippingCountry> countries = countryRepository.findByCountryCodes(codes);
        return zone.removeCountries(countries);
    }

}
