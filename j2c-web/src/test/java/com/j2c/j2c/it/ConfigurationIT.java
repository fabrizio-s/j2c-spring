package com.j2c.j2c.it;

import com.fasterxml.jackson.databind.JsonNode;
import com.j2c.j2c.service.dto.ConfigurationDTO;
import com.j2c.j2c.service.input.ConfigurationForm;
import com.j2c.j2c.it.util.BaseIT;
import com.neovisionaries.i18n.CurrencyCode;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class ConfigurationIT extends BaseIT {

    @Test
    void get() {
        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/configuration",
                HttpMethod.GET,
                null,
                null
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertBodyIsOfType(response.getBody(), ConfigurationDTO.class);
    }

    @Test
    void configure() {
        final ConfigurationForm body = ConfigurationForm.builder()
                .currency(CurrencyCode.GBP)
                .build();

        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/configuration",
                HttpMethod.PATCH,
                body,
                adminToken
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertBodyIsOfType(response.getBody(), ConfigurationDTO.class);
    }

}
