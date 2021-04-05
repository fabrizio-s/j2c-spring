package com.j2c.j2c.service.test;

import com.j2c.j2c.domain.entity.ShippingCountry;
import com.j2c.j2c.domain.entity.ShippingMethod;
import com.j2c.j2c.domain.entity.ShippingZone;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestComponent;
import java.util.List;
import java.util.Optional;

import static com.j2c.j2c.service.test.TestUtils.nullOrEmpty;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.when;

@TestComponent
@RequiredArgsConstructor
public class ShippingServiceStubber {

    private final MockBeanProvider mockBeanProvider;

    @Builder(builderClassName = "MockCreateZone",
            builderMethodName = "createZone",
            buildMethodName = "stub")
    private void _createZone(final List<ShippingCountry> countries) {
        mockFindCountriesByCode(countries);
    }

    @Builder(builderClassName = "MockUpdateZone",
            builderMethodName = "updateZone",
            buildMethodName = "stub")
    private void _updateZone(
            final ShippingZone zoneToUpdate,
            final List<ShippingCountry> countriesToAdd,
            final List<ShippingCountry> countriesToRemove
    ) {
        mockFindZoneById(zoneToUpdate);
        mockFindCountriesByCode(countriesToAdd, countriesToRemove);
    }

    @Builder(builderClassName = "MockDeleteZone",
            builderMethodName = "deleteZone",
            buildMethodName = "stub")
    private void _deleteZone(final ShippingZone zoneToDelete) {
        mockFindZoneById(zoneToDelete);
    }

    @Builder(builderClassName = "MockCreateMethod",
            builderMethodName = "createMethod",
            buildMethodName = "stub")
    private void _createMethod(final ShippingZone parentZone) {
        mockFindZoneById(parentZone);
    }

    @Builder(builderClassName = "MockUpdateMethod",
            builderMethodName = "updateMethod",
            buildMethodName = "stub")
    private void _updateMethod(
            final ShippingZone parentZone,
            final ShippingMethod methodToUpdate
    ) {
        mockFindZoneById(parentZone);
        mockFindMethodById(methodToUpdate);
    }

    @Builder(builderClassName = "MockDeleteMethod",
            builderMethodName = "deleteMethod",
            buildMethodName = "stub")
    private void _deleteMethod(
            final ShippingZone parentZone,
            final ShippingMethod methodToDelete
    ) {
        mockFindZoneById(parentZone);
        mockFindMethodById(methodToDelete);
    }

    private void mockFindZoneById(final ShippingZone zone) {
        if (zone != null) {
            when(mockBeanProvider.getShippingZoneRepository().findById(zone.getId()))
                    .thenReturn(Optional.of(zone));
        }
    }

    private void mockFindMethodById(final ShippingMethod method) {
        if (method != null) {
            when(mockBeanProvider.getShippingMethodRepository().findById(method.getId()))
                    .thenReturn(Optional.of(method));
        }
    }

    private void mockFindCountriesByCode(final List<ShippingCountry> countries) {
        mockFindCountriesByCode(countries, null);
    }

    private void mockFindCountriesByCode(final List<ShippingCountry> countriesToAdd, final List<ShippingCountry> countriesToRemove) {
        if (!(nullOrEmpty(countriesToAdd) || nullOrEmpty(countriesToRemove))) {
            when(mockBeanProvider.getShippingCountryRepository().findAllByCodeIn(anySet()))
                    .thenReturn(countriesToAdd)
                    .thenReturn(countriesToRemove);
        } else if (!nullOrEmpty(countriesToAdd)) {
            when(mockBeanProvider.getShippingCountryRepository().findAllByCodeIn(anySet()))
                    .thenReturn(countriesToAdd);
        } else if (!nullOrEmpty(countriesToRemove)) {
            when(mockBeanProvider.getShippingCountryRepository().findAllByCodeIn(anySet()))
                    .thenReturn(countriesToRemove);
        }
    }

}
