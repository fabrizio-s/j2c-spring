package com.j2c.j2c.service.application.impl;

import com.google.common.collect.Sets;
import com.j2c.j2c.domain.entity.*;
import com.j2c.j2c.service.input.*;
import com.j2c.j2c.service.input.CreateShippingMethodForm.CreateShippingMethodFormBuilder;
import com.j2c.j2c.service.input.CreateShippingZoneForm.CreateShippingZoneFormBuilder;
import com.j2c.j2c.service.input.UpdateShippingMethodForm.UpdateShippingMethodFormBuilder;
import com.j2c.j2c.service.input.UpdateShippingZoneForm.UpdateShippingZoneFormBuilder;
import com.j2c.j2c.service.dto.ShippingMethodDTO;
import com.j2c.j2c.service.dto.ShippingZoneDTO;
import com.j2c.j2c.service.exception.InvalidInputException;
import com.j2c.j2c.service.exception.ResourceNotFoundException;
import com.j2c.j2c.service.exception.ServiceException;
import com.j2c.j2c.service.test.*;
import com.neovisionaries.i18n.CountryCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

import static com.j2c.j2c.domain.exception.DomainErrorMessages.*;
import static com.j2c.j2c.service.exception.J2cServiceErrorMessages.RESOURCE_NOT_FOUND;
import static com.j2c.j2c.domain.enums.ShippingMethodType.Weight;
import static com.j2c.j2c.service.test.MockEntity.*;
import static com.j2c.j2c.service.test.TestUtils.nullable;
import static com.neovisionaries.i18n.CountryCode.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.util.ReflectionTestUtils.setField;

class ShippingServiceImplTest extends BaseServiceTest {

    @Autowired
    private ShippingServiceImpl service;

    @Autowired
    private ShippingServiceStubber stubber;

    @Autowired
    private MockBeanProvider mockBeanProvider;

    @Test
    public void createZone_NullForm_ShouldThrowInvalidInputException() {
        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createZone(null)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("form must not be null"))
        );
    }

    @Test
    public void createZone_AnyCountryIsNull_ShouldThrowInvalidInputException() {
        final CreateShippingZoneForm form = hpCreateShippingZoneForm()
                .countries(new HashSet<>(Arrays.asList(CountryCode.US, null)))
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createZone(form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("countries must not contain null elements"))
        );
    }

    @Test
    public void createZone_NullName_ShouldThrowInvalidInputException() {
        final CreateShippingZoneForm form = hpCreateShippingZoneForm()
                .name(null)
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createZone(form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("name must not be blank"))
        );
    }

    @Test
    public void createZone_BlankName_ShouldThrowInvalidInputException() {
        final CreateShippingZoneForm form = hpCreateShippingZoneForm()
                .name("     ")
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createZone(form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("name must not be blank"))
        );
    }

    @Test
    public void createZone_AnyCountryAlreadyBelongsToSomeZone_ShouldThrowServiceException() {
        final CreateShippingZoneForm form = hpCreateShippingZoneForm().build();

        final List<ShippingCountry> countries = countries(form.getCountries());
        setCountryZone(countries.get(0), shippingZone().build());
        stubber.createZone()
                .countries(countries)
                .stub();

        final ServiceException exception = assertThrows(
                ServiceException.class,
                () -> service.createZone(form)
        );
        assertTrue(exception.getMessage().matches(String.format(COUNTRY_ALREADY_BELONGS_TO_ZONE, "[A-Z]{2}", "[0-9]+")));
    }

    @Test
    public void createZone_HappyPath_ShouldReturnCreatedZone() {
        final CreateShippingZoneForm form = hpCreateShippingZoneForm().build();

        stubber.createZone()
                .countries(countries(form.getCountries()))
                .stub();

        final ShippingZoneDTO zoneDTO = service.createZone(form);

        assertNotNull(zoneDTO);
        assertEquals(form.getName(), zoneDTO.getName());
        assertEquals(form.getCountries().size(), zoneDTO.getCountries().size());

        verify(mockBeanProvider.getShippingZoneRepository(), times(1))
                .save(any(ShippingZone.class));
    }

    @Test
    public void createZone_NullCountries_ShouldReturnEmptyCountries() {
        final CreateShippingZoneForm form = hpCreateShippingZoneForm()
                .countries(null)
                .build();

        stubber.createZone()
                .countries(countries(form.getCountries()))
                .stub();

        final ShippingZoneDTO zoneDTO = service.createZone(form);

        assertTrue(zoneDTO.getCountries().isEmpty());
    }

    @Test
    public void updateZone_NullZoneId_ShouldThrowInvalidInputException() {
        final UpdateShippingZoneForm form = hpUpdateShippingZoneForm().build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateZone(null, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("zone id must not be null"))
        );
    }

    @Test
    public void updateZone_NullForm_ShouldThrowInvalidInputException() {
        final Long zoneId = 1L;

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateZone(zoneId, null)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("form must not be null"))
        );
    }

    @Test
    public void updateZone_BlankName_ShouldThrowInvalidInputException() {
        final Long zoneId = 1L;
        final UpdateShippingZoneForm form = hpUpdateShippingZoneForm()
                .name("     ")
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateZone(zoneId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("name must not be blank"))
        );
    }

    @Test
    public void updateZone_AnyCountryToAddIsNull_ShouldThrowInvalidInputException() {
        final Long zoneId = 1L;
        final UpdateShippingZoneForm form = hpUpdateShippingZoneForm()
                .countriesToAdd(new HashSet<>(Arrays.asList(IT, IR, null)))
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateZone(zoneId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("countries to add must not contain null elements"))
        );
    }

    @Test
    public void updateZone_AnyCountryToRemoveIsNull_ShouldThrowInvalidInputException() {
        final Long zoneId = 1L;
        final UpdateShippingZoneForm form = hpUpdateShippingZoneForm()
                .countriesToRemove(new HashSet<>(Arrays.asList(IT, IR, null)))
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateZone(zoneId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("countries to remove must not contain null elements"))
        );
    }

    @Test
    public void updateZone_ZoneDoesNotExist_ShouldThrowResourceNotFoundException() {
        final Long zoneId = 1L;
        final UpdateShippingZoneForm form = hpUpdateShippingZoneForm().build();

        stubber.updateZone()
                .countriesToAdd(countries(form.getCountriesToAdd()))
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.updateZone(zoneId, form)
        );
        assertTrue(exception.getMessage().contains(String.format(RESOURCE_NOT_FOUND, "shipping zone", zoneId)));
    }

    @Test
    public void updateZone_CountryToAddAlreadyBelongsToSomeZone_ShouldThrowServiceException() {
        final Long zoneId = 1L;
        final UpdateShippingZoneForm form = hpUpdateShippingZoneForm().build();

        final ShippingZone zoneToUpdate = zoneWithId(zoneId);
        final List<ShippingCountry> countries = countries(form.getCountriesToAdd());
        setCountryZone(countries.get(0), shippingZone().build());
        stubber.updateZone()
                .zoneToUpdate(zoneToUpdate)
                .countriesToAdd(countries)
                .countriesToRemove(countriesForZone(form.getCountriesToRemove(), zoneToUpdate))
                .stub();

        final ServiceException exception = assertThrows(
                ServiceException.class,
                () -> service.updateZone(zoneId, form)
        );
        assertTrue(exception.getMessage().matches(String.format(COUNTRY_ALREADY_BELONGS_TO_ZONE, "[A-Z]{2}", "[0-9]+")));
    }

    @Test
    public void updateZone_HappyPath_ShouldReturnUpdatedZone() {
        final Long zoneId = 1L;
        final UpdateShippingZoneForm form = hpUpdateShippingZoneForm().build();

        final ShippingZone zoneToUpdate = zoneWithId(zoneId);
        stubber.updateZone()
                .zoneToUpdate(zoneToUpdate)
                .countriesToAdd(countries(form.getCountriesToAdd()))
                .countriesToRemove(countriesForZone(form.getCountriesToRemove(), zoneToUpdate))
                .stub();

        final ShippingZoneDTO zoneDTO = service.updateZone(zoneId, form);

        assertNotNull(zoneDTO);
        assertEquals(form.getName(), zoneDTO.getName());
        assertEquals(form.getCountriesToAdd().size(), zoneDTO.getAddedCountries().size());
        assertEquals(form.getCountriesToRemove().size(), zoneDTO.getRemovedCountries().size());
    }

    @Test
    public void updateZone_NullName_ShouldNotUpdateZoneName() {
        final Long zoneId = 1L;
        final UpdateShippingZoneForm form = hpUpdateShippingZoneForm()
                .name(null)
                .build();

        final ShippingZone zoneToUpdate = zoneWithId(zoneId);
        stubber.updateZone()
                .zoneToUpdate(zoneToUpdate)
                .countriesToAdd(countries(form.getCountriesToAdd()))
                .countriesToRemove(countriesForZone(form.getCountriesToRemove(), zoneToUpdate))
                .stub();

        final ShippingZoneDTO zoneDTO = service.updateZone(zoneId, form);

        assertNotNull(zoneDTO.getName());
    }

    @Test
    public void updateZone_NullCountriesToAdd_ShouldReturnEmptyAddedCountries() {
        final Long zoneId = 1L;
        final UpdateShippingZoneForm form = hpUpdateShippingZoneForm()
                .countriesToAdd(null)
                .build();

        final ShippingZone zoneToUpdate = zoneWithId(zoneId);
        stubber.updateZone()
                .zoneToUpdate(zoneToUpdate)
                .countriesToAdd(countries(form.getCountriesToAdd()))
                .countriesToRemove(countriesForZone(form.getCountriesToRemove(), zoneToUpdate))
                .stub();

        final ShippingZoneDTO zoneDTO = service.updateZone(zoneId, form);

        assertTrue(zoneDTO.getAddedCountries().isEmpty());
    }

    @Test
    public void updateZone_NullCountriesToRemove_ShouldReturnEmptyRemovedCountries() {
        final Long zoneId = 1L;
        final UpdateShippingZoneForm form = hpUpdateShippingZoneForm()
                .countriesToRemove(null)
                .build();

        final ShippingZone zoneToUpdate = zoneWithId(zoneId);
        stubber.updateZone()
                .zoneToUpdate(zoneToUpdate)
                .countriesToAdd(countries(form.getCountriesToAdd()))
                .countriesToRemove(countriesForZone(form.getCountriesToRemove(), zoneToUpdate))
                .stub();

        final ShippingZoneDTO zoneDTO = service.updateZone(zoneId, form);

        assertTrue(zoneDTO.getRemovedCountries().isEmpty());
    }

    @Test
    public void updateZone_AnyCountryToRemoveDoesNotBelongToZone_ShouldNotAddCountry() {
        final Long zoneId = 1L;
        final UpdateShippingZoneForm form = hpUpdateShippingZoneForm().build();

        final ShippingZone zoneToUpdate = zoneWithId(zoneId);
        final List<ShippingCountry> countriesToRemove = countriesForZone(form.getCountriesToRemove(), zoneToUpdate);
        setCountryZone(countriesToRemove.get(0), shippingZone().build());
        stubber.updateZone()
                .zoneToUpdate(zoneToUpdate)
                .countriesToAdd(countries(form.getCountriesToAdd()))
                .countriesToRemove(countriesToRemove)
                .stub();

        final ShippingZoneDTO zoneDTO = service.updateZone(zoneId, form);

        assertEquals(form.getCountriesToRemove().size() - 1, zoneDTO.getRemovedCountries().size());
    }

    @Test
    public void updateZone_AnyCountryToRemoveDoesNotBelongToZone_ShouldIgnore() {
        final Long zoneId = 1L;
        final UpdateShippingZoneForm form = hpUpdateShippingZoneForm().build();

        final ShippingZone zoneToUpdate = zoneWithId(zoneId);
        final List<ShippingCountry> countriesToRemove = countriesForZone(form.getCountriesToRemove(), zoneToUpdate);
        setCountryZone(countriesToRemove.get(0), shippingZone().build());
        stubber.updateZone()
                .zoneToUpdate(zoneToUpdate)
                .countriesToAdd(countries(form.getCountriesToAdd()))
                .countriesToRemove(countriesToRemove)
                .stub();

        final ShippingZoneDTO zoneDTO = service.updateZone(zoneId, form);

        assertEquals(form.getCountriesToRemove().size() - 1, zoneDTO.getRemovedCountries().size());
    }

    @Test
    public void deleteZone_NullZoneId_ShouldThrowInvalidInputException() {
        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.deleteZone(null)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("zone id must not be null"))
        );
    }

    @Test
    public void deleteZone_ZoneIdDoesNotExist_ShouldThrowResourceNotFoundException() {
        final Long zoneId = 1L;

        stubber.deleteZone()
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.deleteZone(zoneId)
        );
        assertTrue(exception.getMessage().contains(String.format(RESOURCE_NOT_FOUND, "shipping zone", zoneId)));
    }

    @Test
    public void deleteZone_HappyPath_ShouldDeleteZoneAndMethodsAndDisassociateCountries() {
        final Long zoneId = 1L;

        stubber.deleteZone()
                .zoneToDelete(zoneWithId(zoneId))
                .stub();

        service.deleteZone(zoneId);

        verify(mockBeanProvider.getShippingZoneRepository(), times(1))
                .delete(any(ShippingZone.class));
    }

    @Test
    public void createMethod_NullZoneId_ShouldThrowInvalidInputException() {
        final CreateShippingMethodForm form = hpCreateShippingMethodForm().build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createMethod(null, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("zone id must not be null"))
        );
    }

    @Test
    public void createMethod_NullForm_ShouldThrowInvalidInputException() {
        final Long zoneId = 1L;

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createMethod(zoneId, null)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("form must not be null"))
        );
    }

    @Test
    public void createMethod_NullName_ShouldThrowInvalidInputException() {
        final Long zoneId = 1L;
        final CreateShippingMethodForm form = hpCreateShippingMethodForm()
                .name(null)
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createMethod(zoneId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("name must not be blank"))
        );
    }

    @Test
    public void createMethod_BlankName_ShouldThrowInvalidInputException() {
        final Long zoneId = 1L;
        final CreateShippingMethodForm form = hpCreateShippingMethodForm()
                .name("     ")
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createMethod(zoneId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("name must not be blank"))
        );
    }

    @Test
    public void createMethod_NullType_ShouldThrowInvalidInputException() {
        final Long zoneId = 1L;
        final CreateShippingMethodForm form = hpCreateShippingMethodForm()
                .type(null)
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createMethod(zoneId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("type must not be null"))
        );
    }

    @Test
    public void createMethod_NullMin_ShouldThrowInvalidInputException() {
        final Long zoneId = 1L;
        final CreateShippingMethodForm form = hpCreateShippingMethodForm()
                .min(null)
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createMethod(zoneId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("min must not be null"))
        );
    }

    @Test
    public void createMethod_NegativeMin_ShouldThrowInvalidInputException() {
        final Long zoneId = 1L;
        final CreateShippingMethodForm form = hpCreateShippingMethodForm()
                .min(-1L)
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createMethod(zoneId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("min must be greater than or equal to 0"))
        );
    }

    @Test
    public void createMethod_NullMax_ShouldThrowInvalidInputException() {
        final Long zoneId = 1L;
        final CreateShippingMethodForm form = hpCreateShippingMethodForm()
                .max(null)
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createMethod(zoneId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("max must not be null"))
        );
    }

    @Test
    public void createMethod_NegativeMax_ShouldThrowInvalidInputException() {
        final Long zoneId = 1L;
        final CreateShippingMethodForm form = hpCreateShippingMethodForm()
                .max(-1L)
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createMethod(zoneId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("max must be greater than or equal to 0"))
        );
    }

    @Test
    public void createMethod_NullRate_ShouldThrowInvalidInputException() {
        final Long zoneId = 1L;
        final CreateShippingMethodForm form = hpCreateShippingMethodForm()
                .rate(null)
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createMethod(zoneId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("rate must not be null"))
        );
    }

    @Test
    public void createMethod_NegativeRate_ShouldThrowInvalidInputException() {
        final Long zoneId = 1L;
        final CreateShippingMethodForm form = hpCreateShippingMethodForm()
                .rate(-1L)
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createMethod(zoneId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("rate must be greater than or equal to 0"))
        );
    }

    @Test
    public void createMethod_ZoneDoesNotExist_ShouldThrowResourceNotFoundException() {
        final Long zoneId = 1L;
        final CreateShippingMethodForm form = hpCreateShippingMethodForm().build();

        stubber.createMethod()
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.createMethod(zoneId, form)
        );
        assertTrue(exception.getMessage().contains(String.format(RESOURCE_NOT_FOUND, "shipping zone", zoneId)));
    }

    @Test
    public void createMethod_MinIsGreaterThanMax_ShouldThrowServiceException() {
        final Long zoneId = 1L;
        final CreateShippingMethodForm form = hpCreateShippingMethodForm()
                .min(15L)
                .max(5L)
                .build();

        stubber.createMethod()
                .parentZone(zoneWithId(zoneId))
                .stub();

        final ServiceException exception = assertThrows(
                ServiceException.class,
                () -> service.createMethod(zoneId, form)
        );
        assertEquals(SHIPPING_METHOD_MIN_MUST_BE_LESS_THAN_MAX, exception.getMessage());
    }

    @Test
    public void createMethod_HappyPath_ShouldReturnSavedMethod() {
        final Long zoneId = 1L;
        final CreateShippingMethodForm form = hpCreateShippingMethodForm().build();

        stubber.createMethod()
                .parentZone(zoneWithId(zoneId))
                .stub();

        final ShippingMethodDTO methodDTO = service.createMethod(zoneId, form);

        assertNotNull(methodDTO);
        assertEquals(form.getName(), methodDTO.getName());
        assertEquals(form.getType(), methodDTO.getType());
        assertEquals(form.getMin(), methodDTO.getMin());
        assertEquals(form.getMax(), methodDTO.getMax());
        assertEquals(form.getRate(), methodDTO.getRate());

        verify(mockBeanProvider.getShippingMethodRepository(), times(1))
                .save(any(ShippingMethod.class));
    }

    @Test
    public void updateMethod_NullZoneId_ShouldThrowInvalidInputException() {
        final Long methodId = 1L;
        final UpdateShippingMethodForm form = hpUpdateShippingMethodForm().build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateMethod(null, methodId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("zone id must not be null"))
        );
    }

    @Test
    public void updateMethod_NullMethodId_ShouldThrowInvalidInputException() {
        final Long zoneId = 1L;
        final UpdateShippingMethodForm form = hpUpdateShippingMethodForm().build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateMethod(zoneId, null, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("method id must not be null"))
        );
    }

    @Test
    public void updateMethod_NullForm_ShouldThrowInvalidInputException() {
        final Long zoneId = 1L;
        final Long methodId = 1L;

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateMethod(zoneId, methodId, null)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("form must not be null"))
        );
    }

    @Test
    public void updateMethod_BlankName_ShouldThrowInvalidInputException() {
        final Long zoneId = 1L;
        final Long methodId = 1L;
        final UpdateShippingMethodForm form = hpUpdateShippingMethodForm()
                .name("   ")
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateMethod(zoneId, methodId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("name must not be blank"))
        );
    }

    @Test
    public void updateMethod_NegativeMin_ShouldThrowInvalidInputException() {
        final Long zoneId = 1L;
        final Long methodId = 1L;
        final UpdateShippingMethodForm form = hpUpdateShippingMethodForm()
                .min(-1L)
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateMethod(zoneId, methodId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("min must be greater than or equal to 0"))
        );
    }

    @Test
    public void updateMethod_NegativeMax_ShouldThrowInvalidInputException() {
        final Long zoneId = 1L;
        final Long methodId = 1L;
        final UpdateShippingMethodForm form = hpUpdateShippingMethodForm()
                .max(-1L)
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateMethod(zoneId, methodId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("max must be greater than or equal to 0"))
        );
    }

    @Test
    public void updateMethod_NegativeRate_ShouldThrowInvalidInputException() {
        final Long zoneId = 1L;
        final Long methodId = 1L;
        final UpdateShippingMethodForm form = hpUpdateShippingMethodForm()
                .rate(-1L)
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateMethod(zoneId, methodId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("rate must be greater than or equal to 0"))
        );
    }

    @Test
    public void updateMethod_ZoneDoesNotExist_ShouldThrowResourceNotFoundException() {
        final Long zoneId = 1L;
        final Long methodId = 1L;
        final UpdateShippingMethodForm form = hpUpdateShippingMethodForm().build();

        stubber.updateMethod()
                .methodToUpdate(methodWithIdForZone(methodId, shippingZone().build()))
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.updateMethod(zoneId, methodId, form)
        );
        assertTrue(exception.getMessage().contains(String.format(RESOURCE_NOT_FOUND, "shipping zone", zoneId)));
    }

    @Test
    public void updateMethod_MethodDoesNotExist_ShouldThrowResourceNotFoundException() {
        final Long zoneId = 1L;
        final Long methodId = 1L;
        final UpdateShippingMethodForm form = hpUpdateShippingMethodForm().build();

        stubber.updateMethod()
                .parentZone(zoneWithId(zoneId))
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.updateMethod(zoneId, methodId, form)
        );
        assertTrue(exception.getMessage().contains(String.format(RESOURCE_NOT_FOUND, "shipping method", methodId)));
    }

    @Test
    public void updateMethod_MethodDoesNotBelongToZone_ShouldThrowServiceException() {
        final Long zoneId = 1L;
        final Long methodId = 1L;
        final UpdateShippingMethodForm form = hpUpdateShippingMethodForm().build();

        stubber.updateMethod()
                .parentZone(zoneWithId(zoneId))
                .methodToUpdate(methodWithIdForZone(methodId, shippingZone().build()))
                .stub();

        final ServiceException exception = assertThrows(
                ServiceException.class,
                () -> service.updateMethod(zoneId, methodId, form)
        );
        assertTrue(exception.getMessage().matches(String.format(METHOD_DOES_NOT_BELONG_TO_ZONE, methodId, zoneId)));
    }

    @Test
    public void updateMethod_HappyPath_ShouldReturnUpdatedMethod() {
        final Long zoneId = 1L;
        final Long methodId = 1L;
        final UpdateShippingMethodForm form = hpUpdateShippingMethodForm().build();

        final ShippingZone zone = zoneWithId(zoneId);
        stubber.updateMethod()
                .parentZone(zone)
                .methodToUpdate(methodWithIdForZone(methodId, zone))
                .stub();

        final ShippingMethodDTO methodDTO = service.updateMethod(zoneId, methodId, form);

        assertNotNull(methodDTO);
        assertEquals(form.getName(), methodDTO.getName());
        assertEquals(form.getMin(), methodDTO.getMin());
        assertEquals(form.getMax(), methodDTO.getMax());
        assertEquals(form.getRate(), methodDTO.getRate());
    }

    @Test
    public void updateMethod_NullName_ShouldNotUpdateField() {
        final Long zoneId = 1L;
        final Long methodId = 1L;
        final UpdateShippingMethodForm form = hpUpdateShippingMethodForm()
                .name(null)
                .build();

        final ShippingZone zone = zoneWithId(zoneId);
        stubber.updateMethod()
                .parentZone(zone)
                .methodToUpdate(methodWithIdForZone(methodId, zone))
                .stub();

        final ShippingMethodDTO methodDTO = service.updateMethod(zoneId, methodId, form);

        assertNotNull(methodDTO.getName());
    }

    @Test
    public void updateMethod_NullMin_ShouldNotUpdateField() {
        final Long zoneId = 1L;
        final Long methodId = 1L;
        final UpdateShippingMethodForm form = hpUpdateShippingMethodForm()
                .min(null)
                .build();

        final ShippingZone zone = zoneWithId(zoneId);
        stubber.updateMethod()
                .parentZone(zone)
                .methodToUpdate(methodWithIdForZone(methodId, zone))
                .stub();

        final ShippingMethodDTO methodDTO = service.updateMethod(zoneId, methodId, form);

        assertNotNull(methodDTO.getMin());
    }

    @Test
    public void updateMethod_NullMax_ShouldNotUpdateField() {
        final Long zoneId = 1L;
        final Long methodId = 1L;
        final UpdateShippingMethodForm form = hpUpdateShippingMethodForm()
                .max(null)
                .build();

        final ShippingZone zone = zoneWithId(zoneId);
        stubber.updateMethod()
                .parentZone(zone)
                .methodToUpdate(methodWithIdForZone(methodId, zone))
                .stub();

        final ShippingMethodDTO methodDTO = service.updateMethod(zoneId, methodId, form);

        assertNotNull(methodDTO.getMax());
    }

    @Test
    public void updateMethod_NullRate_ShouldNotUpdateMethodRate() {
        final Long zoneId = 1L;
        final Long methodId = 1L;
        final UpdateShippingMethodForm form = hpUpdateShippingMethodForm()
                .rate(null)
                .build();

        final ShippingZone zone = zoneWithId(zoneId);
        stubber.updateMethod()
                .parentZone(zone)
                .methodToUpdate(methodWithIdForZone(methodId, zone))
                .stub();

        final ShippingMethodDTO methodDTO = service.updateMethod(zoneId, methodId, form);

        assertNotNull(methodDTO.getRate());
    }

    @Test
    public void deleteMethod_NullZoneId_ShouldThrowInvalidInputException() {
        final Long methodId = 1L;

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.deleteMethod(null, methodId)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("zone id must not be null"))
        );
    }

    @Test
    public void deleteMethod_NullMethodId_ShouldThrowInvalidInputException() {
        final Long zoneId = 1L;

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.deleteMethod(zoneId, null)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("method id must not be null"))
        );
    }

    @Test
    public void deleteMethod_ZoneDoesNotExist_ShouldThrowResourceNotFoundException() {
        final Long zoneId = 1L;
        final Long methodId = 1L;

        stubber.deleteMethod()
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.deleteMethod(zoneId, methodId)
        );
        assertTrue(exception.getMessage().contains(String.format(RESOURCE_NOT_FOUND, "shipping zone", zoneId)));
    }

    @Test
    public void deleteMethod_MethodDoesNotExist_ShouldThrowResourceNotFoundException() {
        final Long zoneId = 1L;
        final Long methodId = 1L;

        stubber.deleteMethod()
                .parentZone(zoneWithId(zoneId))
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.deleteMethod(zoneId, methodId)
        );
        assertTrue(exception.getMessage().contains(String.format(RESOURCE_NOT_FOUND, "shipping method", methodId)));
    }

    @Test
    public void deleteMethod_MethodDoesNotBelongToZone_ShouldThrowServiceException() {
        final Long zoneId = 1L;
        final Long methodId = 1L;

        stubber.deleteMethod()
                .parentZone(zoneWithId(zoneId))
                .methodToDelete(methodWithIdForZone(methodId, shippingZone().build()))
                .stub();

        final ServiceException exception = assertThrows(
                ServiceException.class,
                () -> service.deleteMethod(zoneId, methodId)
        );
        assertTrue(exception.getMessage().matches(String.format(METHOD_DOES_NOT_BELONG_TO_ZONE, methodId, zoneId)));
    }

    @Test
    public void deleteMethod_HappyPath_ShouldDeleteMethod() {
        final Long zoneId = 1L;
        final Long methodId = 1L;

        final ShippingZone zone = zoneWithId(zoneId);
        stubber.deleteMethod()
                .parentZone(zone)
                .methodToDelete(methodWithIdForZone(methodId, zone))
                .stub();

        service.deleteMethod(zoneId, methodId);

        verify(mockBeanProvider.getShippingMethodRepository(), times(1))
                .delete(any(ShippingMethod.class));
    }

    private static ShippingZone zoneWithId(final Long id) {
        return shippingZone()
                .id(id)
                .build();
    }

    private static ShippingMethod methodWithIdForZone(final Long id, final ShippingZone zone) {
        return shippingMethod()
                .id(id)
                .zone(zone)
                .build();
    }

    private static List<ShippingCountry> countries(final Set<CountryCode> codes) {
        return nullable(codes)
                .map(code -> shippingCountry()
                        .code(code)
                        .nullZone(true)
                        .build())
                .collect(Collectors.toList());
    }

    private static List<ShippingCountry> countriesForZone(final Set<CountryCode> codes, final ShippingZone zone) {
        return nullable(codes)
                .map(code -> shippingCountry()
                        .code(code)
                        .zone(zone)
                        .build())
                .collect(Collectors.toList());
    }

    private static void setCountryZone(final ShippingCountry country, final ShippingZone zone) {
        setField(country, "zone", zone);
    }

    private static UpdateShippingMethodFormBuilder hpUpdateShippingMethodForm() {
        return UpdateShippingMethodForm.builder()
                .name("Shipping Method Name")
                .min(10L)
                .max(30L)
                .rate(125L);
    }

    private static CreateShippingMethodFormBuilder hpCreateShippingMethodForm() {
        return CreateShippingMethodForm.builder()
                .name("Shipping Method Name")
                .type(Weight)
                .min(10L)
                .max(30L)
                .rate(125L);
    }

    private static UpdateShippingZoneFormBuilder hpUpdateShippingZoneForm() {
        return UpdateShippingZoneForm.builder()
                .name("Updated Shipping Zone Name")
                .countriesToAdd(Sets.immutableEnumSet(IT, IR, SC))
                .countriesToRemove(Sets.immutableEnumSet(US, AR, IC));
    }

    private static CreateShippingZoneFormBuilder hpCreateShippingZoneForm() {
        return CreateShippingZoneForm.builder()
                .name("Test Shipping Zone")
                .countries(EnumSet.of(US, AR, IC));
    }

}
