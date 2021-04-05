package com.j2c.j2c.it;

import com.fasterxml.jackson.databind.JsonNode;
import com.j2c.j2c.domain.enums.ShippingMethodType;
import com.j2c.j2c.service.dto.ShippingCountryDTO;
import com.j2c.j2c.service.dto.ShippingMethodDTO;
import com.j2c.j2c.service.dto.ShippingZoneDTO;
import com.j2c.j2c.service.input.CreateShippingMethodForm;
import com.j2c.j2c.service.input.CreateShippingZoneForm;
import com.j2c.j2c.service.input.UpdateShippingMethodForm;
import com.j2c.j2c.service.input.UpdateShippingZoneForm;
import com.j2c.j2c.it.util.BaseIT;
import com.neovisionaries.i18n.CountryCode;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class ShippingIT extends BaseIT {

    @Test
    void getAllZones() {
        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/shipping-zones",
                HttpMethod.GET,
                null,
                null
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertPageOfTypeIsNotEmpty(response.getBody(), ShippingZoneDTO.class);
    }

    @Test
    void getZone() {
        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/shipping-zones/1",
                HttpMethod.GET,
                null,
                null
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertBodyIsOfType(response.getBody(), ShippingZoneDTO.class);
    }

    @Test
    void getZoneCountries() {
        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/shipping-zones/1/countries",
                HttpMethod.GET,
                null,
                null
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertPageOfTypeIsNotEmpty(response.getBody(), CountryCode.class);
    }

    @Test
    void getMethods() {
        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/shipping-zones/1/methods",
                HttpMethod.GET,
                null,
                null
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertPageOfTypeIsNotEmpty(response.getBody(), ShippingMethodDTO.class);
    }

    @Test
    void getMethod() {
        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/shipping-zones/1/methods/1",
                HttpMethod.GET,
                null,
                null
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertBodyIsOfType(response.getBody(), ShippingMethodDTO.class);
    }

    @Test
    void getAllCountries() {
        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/shipping-countries",
                HttpMethod.GET,
                null,
                null
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertPageOfTypeIsNotEmpty(response.getBody(), ShippingCountryDTO.class);
    }

    @Test
    void getCountry() {
        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/shipping-countries/US",
                HttpMethod.GET,
                null,
                null
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertBodyIsOfType(response.getBody(), ShippingCountryDTO.class);
    }

    @Test
    void createZone() {
        final CreateShippingZoneForm body = CreateShippingZoneForm.builder()
                .name("Test Shipping Zone")
                .build();

        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/shipping-zones",
                HttpMethod.POST,
                body,
                adminToken
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertBodyIsOfType(response.getBody(), ShippingZoneDTO.class);
    }

    @Test
    void updateZone() {
        final ShippingZoneDTO shippingZone = testDataCreator.createShippingZone();

        final UpdateShippingZoneForm body = UpdateShippingZoneForm.builder()
                .name("New Test Shipping Zone Name")
                .build();

        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/shipping-zones/" + shippingZone.getId(),
                HttpMethod.PATCH,
                body,
                adminToken
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertBodyIsOfType(response.getBody(), ShippingZoneDTO.class);
    }

    @Test
    void deleteZone() {
        final ShippingZoneDTO shippingZone = testDataCreator.createShippingZone();

        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/shipping-zones/" + shippingZone.getId(),
                HttpMethod.DELETE,
                null,
                adminToken
        );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void createMethod() {
        final ShippingZoneDTO shippingZone = testDataCreator.createShippingZone();

        final CreateShippingMethodForm body = CreateShippingMethodForm.builder()
                .name("Test Shipping Method")
                .type(ShippingMethodType.Price)
                .min(0L)
                .max(1000L)
                .rate(50L)
                .build();

        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/shipping-zones/" + shippingZone.getId() + "/methods",
                HttpMethod.POST,
                body,
                adminToken
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertBodyIsOfType(response.getBody(), ShippingMethodDTO.class);
    }

    @Test
    void updateMethod() {
        final ShippingZoneDTO shippingZone = testDataCreator.createShippingZone();
        final ShippingMethodDTO method = testDataCreator.createShippingMethodForZone(shippingZone);

        final UpdateShippingMethodForm body = UpdateShippingMethodForm.builder()
                .name("New Test Shipping Method Name")
                .build();

        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/shipping-zones/" + shippingZone.getId() + "/methods/" + method.getId(),
                HttpMethod.PATCH,
                body,
                adminToken
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertBodyIsOfType(response.getBody(), ShippingMethodDTO.class);
    }

    @Test
    void deleteMethod() {
        final ShippingZoneDTO shippingZone = testDataCreator.createShippingZone();
        final ShippingMethodDTO method = testDataCreator.createShippingMethodForZone(shippingZone);

        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/shipping-zones/" + shippingZone.getId() + "/methods/" + method.getId(),
                HttpMethod.DELETE,
                null,
                adminToken
        );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

}
