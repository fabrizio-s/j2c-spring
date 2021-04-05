package com.j2c.j2c.service.application.impl;

import com.j2c.j2c.service.application.ShippingService;
import com.j2c.j2c.service.domain.shipping.*;
import com.j2c.j2c.service.dto.ShippingCountryDTO;
import com.j2c.j2c.service.dto.ShippingMethodDTO;
import com.j2c.j2c.service.dto.ShippingZoneDTO;
import com.j2c.j2c.service.exception.ServiceException;
import com.j2c.j2c.service.input.CreateShippingMethodForm;
import com.j2c.j2c.service.input.CreateShippingZoneForm;
import com.j2c.j2c.service.input.UpdateShippingMethodForm;
import com.j2c.j2c.service.input.UpdateShippingZoneForm;
import com.j2c.j2c.domain.entity.*;
import com.j2c.j2c.domain.repository.CheckoutRepository;
import com.j2c.j2c.domain.repository.ShippingCountryRepository;
import com.j2c.j2c.domain.repository.ShippingMethodRepository;
import com.j2c.j2c.domain.repository.ShippingZoneRepository;
import com.j2c.j2c.service.mapper.ShippingServiceMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.neovisionaries.i18n.CountryCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

import static com.j2c.j2c.domain.exception.DomainErrorMessages.CHECKOUT_NO_SHIPPING_REQUIRED;

@Service
@Validated
@RequiredArgsConstructor
public class ShippingServiceImpl implements ShippingService {

    private final ShippingServiceMapper mapper;
    private final DomainShippingService domainService;
    private final CheckoutRepository checkoutRepository;
    private final ShippingZoneRepository zoneRepository;
    private final ShippingMethodRepository methodRepository;
    private final ShippingCountryRepository countryRepository;

    @Override
    public ShippingZoneDTO findZone(@NotNull final Long zoneId) {
        final ShippingZone zone = zoneRepository.findById(zoneId);
        return mapper.toZoneDTO(zone);
    }

    @Override
    public Page<ShippingZoneDTO> findAllZones(@NotNull final Pageable pageable) {
        final Page<ShippingZone> zones = zoneRepository.findAll(pageable);
        return mapper.toZoneDTO(zones);
    }

    @Override
    public Page<CountryCode> findZoneCountries(@NotNull final Long zoneId, @NotNull final Pageable pageable) {
        zoneRepository.verifyExistsById(zoneId);
        final Page<ShippingCountry> countries = countryRepository.findAllByZoneId(zoneId, pageable);
        return mapper.toCountryCodes(countries);
    }

    @Override
    public Page<ShippingMethodDTO> findMethods(@NotNull final Long zoneId, @NotNull final Pageable pageable) {
        zoneRepository.verifyExistsById(zoneId);
        final Page<ShippingMethod> page = methodRepository.findAllByZoneId(zoneId, pageable);
        return mapper.toMethodDTO(page);
    }

    @Override
    public Page<ShippingMethodDTO> findMethodsForCheckout(
            @NotNull final Long zoneId,
            @NotNull final Long checkoutId,
            @NotNull final Pageable pageable
    ) {
        zoneRepository.verifyExistsById(zoneId);
        final Checkout checkout = checkoutRepository.findById(checkoutId);
        if (!checkout.isShippingRequired()) {
            throw new ServiceException(String.format(CHECKOUT_NO_SHIPPING_REQUIRED, checkoutId));
        }
        final Page<ShippingMethod> methods = methodRepository.findAllByZoneIdAndPriceOrTotalMassWithinRange()
                .zoneId(zoneId)
                .price(checkout.getPrice())
                .totalMass(checkout.getTotalMass())
                .pageable(pageable)
                .find();
        return mapper.toMethodDTO(methods);
    }

    @Override
    public ShippingMethodDTO findMethod(@NotNull final Long zoneId, @NotNull final Long methodId) {
        final ShippingZone zone = zoneRepository.findById(zoneId);
        final ShippingMethod method = methodRepository.findById(methodId).verifyBelongsToZone(zone);
        return mapper.toMethodDTO(method);
    }

    @Override
    public Page<ShippingCountryDTO> findAllCountries(@NotNull final Pageable pageable) {
        final Page<ShippingCountry> countries = countryRepository.findAll(pageable);
        return mapper.toCountryDTO(countries);
    }

    @Override
    public Page<ShippingCountryDTO> findAllUnusedCountries(@NotNull final Pageable pageable) {
        final Page<ShippingCountry> countries = countryRepository.findAllUnused(pageable);
        return mapper.toCountryDTO(countries);
    }

    @Override
    public ShippingCountryDTO findCountry(@NotNull final CountryCode code) {
        final ShippingCountry country = countryRepository.findByCode(code);
        return mapper.toCountryDTO(country);
    }

    @Override
    public ShippingZoneDTO createZone(final CreateShippingZoneForm form) {
        final CreateShippingZoneResult result = domainService.createZone(form);
        return mapper.toZoneDTO(result);
    }

    @Override
    public ShippingZoneDTO updateZone(final Long zoneId, final UpdateShippingZoneForm form) {
        final UpdateShippingZoneResult result = domainService.updateZone(zoneId, form);
        return mapper.toZoneDTO(result);
    }

    @Override
    public void deleteZone(final Long zoneId) {
        domainService.deleteZone(zoneId);
    }

    @Override
    public ShippingMethodDTO createMethod(final Long zoneId, final CreateShippingMethodForm form) {
        final ShippingMethod createdMethod = domainService.createMethod(zoneId, form);
        return mapper.toMethodDTO(createdMethod);
    }

    @Override
    public ShippingMethodDTO updateMethod(final Long zoneId, final Long methodId, final UpdateShippingMethodForm form) {
        final ShippingMethod updatedMethod = domainService.updateMethod(zoneId, methodId, form);
        return mapper.toMethodDTO(updatedMethod);
    }

    @Override
    public void deleteMethod(final Long zoneId, final Long methodId) {
        domainService.deleteMethod(zoneId, methodId);
    }

}
