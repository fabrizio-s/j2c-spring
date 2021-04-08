package com.j2c.j2c.it;

import com.fasterxml.jackson.databind.JsonNode;
import com.j2c.j2c.it.util.BaseIT;
import com.j2c.j2c.service.dto.UserDTO;
import com.j2c.j2c.web.input.AuthRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static com.j2c.j2c.web.util.WebConstants.AuthURI;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AuthIT extends BaseIT {

    @Test
    void authenticate() {
        final UserDTO customer = testDataCreator.createUserWithUniqueEmail();

        final AuthRequest body = AuthRequest.builder()
                .email(customer.getEmail())
                .password("password")
                .build();

        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + AuthURI,
                HttpMethod.POST,
                body,
                null
        );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void refresh() {
        final UserDTO customer = testDataCreator.createUserWithUniqueEmail();

        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/refresh",
                HttpMethod.POST,
                null,
                authenticate(customer.getEmail(), "password")
        );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

}
