package com.j2c.j2c.it;

import com.fasterxml.jackson.databind.JsonNode;
import com.j2c.j2c.domain.enums.RoleType;
import com.j2c.j2c.service.dto.*;
import com.j2c.j2c.service.input.*;
import com.j2c.j2c.service.input.VerifyUserForm;
import com.j2c.j2c.it.util.BaseIT;
import com.neovisionaries.i18n.CountryCode;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.web.util.UriComponentsBuilder.fromHttpUrl;

class UserIT extends BaseIT {

    @Test
    void getAll() {
        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/users",
                HttpMethod.GET,
                null,
                adminToken
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertPageOfTypeIsNotEmpty(response.getBody(), UserDTO.class);
    }

    @Test
    void emailExists() {
        final ResponseEntity<JsonNode> response = httpRequest(
                fromHttpUrl(baseUrl + "/api/users")
                        .queryParam("email", "admin@j2c.com")
                        .build()
                        .toUriString(),
                HttpMethod.HEAD,
                null,
                null
        );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void get() {
        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/users/1",
                HttpMethod.GET,
                null,
                adminToken
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertBodyIsOfType(response.getBody(), UserDTO.class);
    }

    @Test
    void getAddresses() {
        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/users/1/addresses",
                HttpMethod.GET,
                null,
                adminToken
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertPageOfTypeIsNotEmpty(response.getBody(), UserAddressDTO.class);
    }

    @Test
    void getAddress() {
        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/users/1/addresses/1",
                HttpMethod.GET,
                null,
                adminToken
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertBodyIsOfType(response.getBody(), UserAddressDTO.class);
    }

    @Test
    void getOrders() {
        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/users/1/orders",
                HttpMethod.GET,
                null,
                adminToken
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertPageOfTypeIsNotEmpty(response.getBody(), OrderDTO.class);
    }

    @Test
    void getOrderLines() {
        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/users/1/orders/1/lines",
                HttpMethod.GET,
                null,
                adminToken
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertPageOfTypeIsNotEmpty(response.getBody(), OrderLineDTO.class);
    }

    @Test
    void getPaymentMethods() {
        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/users/1/payment-methods",
                HttpMethod.GET,
                null,
                adminToken
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isArray());
    }

    @Test
    void create() {
        final CreateUserForm body = CreateUserForm.builder()
                .role(RoleType.Customer)
                .build();

        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/users",
                HttpMethod.POST,
                body,
                adminToken
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertBodyIsOfType(response.getBody(), UserDTO.class);
    }

    @Test
    void update() {
        final UserDTO user = testDataCreator.createUserWithUniqueEmail();

        final UpdateUserForm body = UpdateUserForm.builder()
                .email(testDataCreator.newRandomEmail())
                .build();

        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/users/" + user.getId(),
                HttpMethod.PATCH,
                body,
                adminToken
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertBodyIsOfType(response.getBody(), UserDTO.class);
    }

    @Test
    void delete() {
        final UserDTO user = testDataCreator.createUserWithUniqueEmail();

        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/users/" + user.getId(),
                HttpMethod.DELETE,
                null,
                adminToken
        );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void signUp() {
        final SignUpForm body = SignUpForm.builder()
                .email(testDataCreator.newRandomEmail())
                .password("password")
                .build();

        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/signup",
                HttpMethod.POST,
                body,
                null
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertBodyIsOfType(response.getBody(), UserDTO.class);
    }

    @Test
    void verify() {
        final UserDTO unverifiedUser = userService.signUp(
                SignUpForm.builder()
                        .email(testDataCreator.newRandomEmail())
                        .password("password")
                        .build()
        );
        final UserVerificationTokenDTO verificationToken = userService.findUserVerificationToken(unverifiedUser.getId());

        final VerifyUserForm body = VerifyUserForm.builder()
                .tokenId(verificationToken.getId())
                .build();

        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/verify",
                HttpMethod.POST,
                body,
                null
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertBodyIsOfType(response.getBody(), UserDTO.class);
    }

    @Test
    void changeEmail() {
        final UserDTO user = userService.create(
                CreateUserForm.builder()
                        .role(RoleType.Customer)
                        .password("password")
                        .build()
        );
        final String token = tokenProvider.create(user.getId(), Collections.emptySet());

        final ChangeUserEmailForm body = ChangeUserEmailForm.builder()
                .email(testDataCreator.newRandomEmail())
                .build();

        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/users/" + user.getId() + "/email",
                HttpMethod.PATCH,
                body,
                token
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertBodyIsOfType(response.getBody(), UserDTO.class);
    }

    @Test
    void changePassword() {
        final UserDTO user = testDataCreator.createUserWithUniqueEmail();

        final ChangeUserPasswordForm body = ChangeUserPasswordForm.builder()
                .password("new_p4ssw0rd")
                .build();

        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/users/" + user.getId() + "/password",
                HttpMethod.PATCH,
                body,
                authenticate(user.getEmail(), "password")
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertBodyIsOfType(response.getBody(), UserDTO.class);
    }

    @Test
    void createAddress() {
        final UserDTO user = testDataCreator.createUserWithUniqueEmail();

        final CreateAddressForm body = CreateAddressForm.builder()
                .firstName("First Name")
                .lastName("Last Name")
                .streetAddress1("Street Address 1")
                .country(CountryCode.IT)
                .countryArea("Country Area")
                .city("City")
                .postalCode("ABC123")
                .phone1("123456789")
                .build();

        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/users/" + user.getId() + "/addresses",
                HttpMethod.POST,
                body,
                authenticate(user.getEmail(), "password")
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertBodyIsOfType(response.getBody(), UserAddressDTO.class);
    }

    @Test
    void updateAddress() {
        final UserDTO user = testDataCreator.createUserWithUniqueEmail();
        final UserAddressDTO address = testDataCreator.createAddressForUser(user);

        final UpdateAddressForm body = UpdateAddressForm.builder()
                .firstName("New First Name")
                .build();

        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/users/" + user.getId() + "/addresses/" + address.getId(),
                HttpMethod.PATCH,
                body,
                authenticate(user.getEmail(), "password")
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertBodyIsOfType(response.getBody(), UserAddressDTO.class);
    }

    @Test
    void deleteAddress() {
        final UserDTO user = testDataCreator.createUserWithUniqueEmail();
        final UserAddressDTO address = testDataCreator.createAddressForUser(user);

        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/users/" + user.getId() + "/addresses/" + address.getId(),
                HttpMethod.DELETE,
                null,
                authenticate(user.getEmail(), "password")
        );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

}
