package com.j2c.j2c.web.controller;

import com.j2c.j2c.service.application.ShippingService;
import com.j2c.j2c.service.dto.ShippingCountryDTO;
import com.j2c.j2c.service.dto.ShippingMethodDTO;
import com.j2c.j2c.service.dto.ShippingZoneDTO;
import com.j2c.j2c.service.input.CreateShippingMethodForm;
import com.j2c.j2c.service.input.CreateShippingZoneForm;
import com.j2c.j2c.service.input.UpdateShippingMethodForm;
import com.j2c.j2c.service.input.UpdateShippingZoneForm;
import com.j2c.j2c.web.security.annotation.HasShippingWriteAccess;
import com.neovisionaries.i18n.CountryCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import static com.j2c.j2c.domain.enums.Authorities.WRITE_SHIPPING;

@RestController
@RequiredArgsConstructor
@Tag(name = "Shipping", description = "Endpoints related to shipping")
public class ShippingController {

    private final ShippingService shippingService;

    @GetMapping(value = "/api/shipping-zones",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Retrieves all shipping zones")
    public Page<ShippingZoneDTO> getAllZones(@ParameterObject final Pageable pageable) {
        return shippingService.findAllZones(pageable);
    }

    @GetMapping(value = "/api/shipping-zones/{zoneId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Retrieves a single shipping zone by its id")
    public ShippingZoneDTO getZone(@PathVariable final Long zoneId) {
        return shippingService.findZone(zoneId);
    }

    @GetMapping(value = "/api/shipping-zones/{zoneId}/countries",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Retrieves the countries assigned to the shipping zone with the specified id")
    public Page<CountryCode> getZoneCountries(
            @PathVariable final Long zoneId,
            @ParameterObject final Pageable pageable
    ) {
        return shippingService.findZoneCountries(zoneId, pageable);
    }

    @GetMapping(value = "/api/shipping-zones/{zoneId}/methods",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Retrieves the shipping methods belonging to the specified shipping zone")
    public Page<ShippingMethodDTO> getMethods(
            @PathVariable final Long zoneId,
            @RequestParam(value="checkout", required = false) final Long checkoutId,
            @ParameterObject final Pageable pageable
    ) {
        if (checkoutId != null) {
            return shippingService.findMethodsForCheckout(zoneId, checkoutId, pageable);
        }
        return shippingService.findMethods(zoneId, pageable);
    }

    @GetMapping(value = "/api/shipping-zones/{zoneId}/methods/{methodId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Retrieves a single shipping method belonging to the specified shipping zone")
    public ShippingMethodDTO getMethod(
            @PathVariable final Long zoneId,
            @PathVariable final Long methodId
    ) {
        return shippingService.findMethod(zoneId, methodId);
    }

    @GetMapping(value = "/api/shipping-countries",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Retrieves all supported shipping countries")
    public Page<ShippingCountryDTO> getAllCountries(
            @RequestParam(value="unused", required = false, defaultValue = "false") final boolean unused,
            @ParameterObject final Pageable pageable
    ) {
        if (unused) {
            return shippingService.findAllUnusedCountries(pageable);
        }
        return shippingService.findAllCountries(pageable);
    }

    @GetMapping(value = "/api/shipping-countries/{code}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Retrieves a single shipping country by its country code")
    public ShippingCountryDTO getCountry(@PathVariable final CountryCode code) {
        return shippingService.findCountry(code);
    }

    @PostMapping(value = "/api/shipping-zones",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @HasShippingWriteAccess
    @Operation(security = @SecurityRequirement(name = "JWT"),
            summary = "Creates a shipping zone",
            description = "Returns the created shipping zone. " +
                    "Requires " + WRITE_SHIPPING + " authority (Admin).")
    public ShippingZoneDTO createZone(@RequestBody final CreateShippingZoneForm payload) {
        return shippingService.createZone(payload);
    }

    @PatchMapping(value = "/api/shipping-zones/{zoneId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @HasShippingWriteAccess
    @Operation(security = @SecurityRequirement(name = "JWT"),
            summary = "Updates the shipping zone with the specified id",
            description = "Returns the updated shipping zone. " +
                    "Requires " + WRITE_SHIPPING + " authority (Admin).")
    public ShippingZoneDTO updateZone(
            @PathVariable final Long zoneId,
            @RequestBody final UpdateShippingZoneForm payload
    ) {
        return shippingService.updateZone(zoneId, payload);
    }

    @DeleteMapping("/api/shipping-zones/{zoneId}")
    @HasShippingWriteAccess
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(security = @SecurityRequirement(name = "JWT"),
            summary = "Deletes the shipping zone with the specified id",
            description = "Requires " + WRITE_SHIPPING + " authority (Admin).")
    public void deleteZone(@PathVariable final Long zoneId) {
        shippingService.deleteZone(zoneId);
    }

    @PostMapping(value = "/api/shipping-zones/{zoneId}/methods",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @HasShippingWriteAccess
    @Operation(security = @SecurityRequirement(name = "JWT"),
            summary = "Creates a shipping method for the specified shipping zone",
            description = "Returns the created shipping method. " +
                    "Requires " + WRITE_SHIPPING + " authority (Admin).")
    public ShippingMethodDTO createMethod(
            @PathVariable final Long zoneId,
            @RequestBody final CreateShippingMethodForm payload
    ) {
        return shippingService.createMethod(zoneId, payload);
    }

    @PatchMapping(value = "/api/shipping-zones/{zoneId}/methods/{methodId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @HasShippingWriteAccess
    @Operation(security = @SecurityRequirement(name = "JWT"),
            summary = "Updates the shipping method with the specified id",
            description = "Returns the updated shipping method. " +
                    "Requires " + WRITE_SHIPPING + " authority (Admin).")
    public ShippingMethodDTO updateMethod(
            @PathVariable final Long zoneId,
            @PathVariable final Long methodId,
            @RequestBody final UpdateShippingMethodForm payload
    ) {
        return shippingService.updateMethod(zoneId, methodId, payload);
    }

    @DeleteMapping("/api/shipping-zones/{zoneId}/methods/{methodId}")
    @HasShippingWriteAccess
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(security = @SecurityRequirement(name = "JWT"),
            summary = "Deletes the shipping method with the specified id",
            description = "Requires " + WRITE_SHIPPING + " authority (Admin).")
    public void deleteMethod(
            @PathVariable final Long zoneId,
            @PathVariable final Long methodId
    ) {
        shippingService.deleteMethod(zoneId, methodId);
    }

}
