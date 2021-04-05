package com.j2c.j2c.it;

import com.fasterxml.jackson.databind.JsonNode;
import com.j2c.j2c.service.dto.*;
import com.j2c.j2c.service.input.*;
import com.j2c.j2c.service.input.SetCheckoutShippingAddressForm;
import com.j2c.j2c.service.input.SetCheckoutShippingMethodForm;
import com.j2c.j2c.it.util.BaseIT;
import com.neovisionaries.i18n.CountryCode;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CheckoutIT extends BaseIT {

    @Test
    void getAll() {
        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/checkouts",
                HttpMethod.GET,
                null,
                adminToken
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertPageOfTypeIsNotEmpty(response.getBody(), CheckoutDTO.class);
    }

    @Test
    void get() {
        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/checkouts/1",
                HttpMethod.GET,
                null,
                adminToken
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertBodyIsOfType(response.getBody(), CheckoutDTO.class);
    }

    @Test
    void getLines() {
        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/checkouts/1/lines",
                HttpMethod.GET,
                null,
                adminToken
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertPageOfTypeIsNotEmpty(response.getBody(), CheckoutLineDTO.class);
    }

    @Test
    void getLine() {
        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/checkouts/1/lines/2",
                HttpMethod.GET,
                null,
                adminToken
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertBodyIsOfType(response.getBody(), CheckoutLineDTO.class);
    }

    @Test
    void checkout() {
        final UserDTO customer = testDataCreator.createUserWithUniqueEmail();

        final CreateCheckoutForm body = CreateCheckoutForm.builder()
                .email("admin@j2c.com")
                .lines(
                        List.of(
                                Line.builder().id(1L).quantity(1).build()
                        )
                )
                .build();

        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/checkouts",
                HttpMethod.POST,
                body,
                authenticate(customer.getEmail(), "password")
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertBodyIsOfType(response.getBody(), CheckoutDTO.class);
    }

    @Test
    void createShippingAddress() {
        final UserDTO customer = testDataCreator.createUserWithUniqueEmail();
        final CheckoutDTO checkout = testDataCreator.createCheckoutForCustomer(customer);

        final CreateCheckoutShippingAddressForm body = CreateCheckoutShippingAddressForm.builder()
                .saveCustomerAddresses(true)
                .address(
                        CreateAddressForm.builder()
                                .firstName("First Name")
                                .lastName("Last Name")
                                .streetAddress1("Street Address 1")
                                .country(CountryCode.IT)
                                .countryArea("Country Area")
                                .city("City")
                                .postalCode("ABC123")
                                .phone1("123456789")
                                .build()
                )
                .build();

        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/checkouts/" + checkout.getId() + "/shipping-address",
                HttpMethod.POST,
                body,
                authenticate(customer.getEmail(), "password")
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertBodyIsOfType(response.getBody(), CheckoutDTO.class);
    }

    @Test
    void updateShippingAddress() {
        final UserDTO customer = testDataCreator.createUserWithUniqueEmail();
        final UserAddressDTO userAddress = testDataCreator.createAddressForUser(customer);
        final CheckoutDTO checkout = testDataCreator.createCheckoutForCustomer(customer);
        checkoutService.setShippingAddress(checkout.getId(), SetCheckoutShippingAddressForm.builder().addressId(userAddress.getId()).build());

        final UpdateCheckoutShippingAddressForm body = UpdateCheckoutShippingAddressForm.builder()
                .saveCustomerAddresses(true)
                .address(
                        UpdateAddressForm.builder()
                                .firstName("New Name")
                                .build()
                )
                .build();

        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/checkouts/" + checkout.getId() + "/shipping-address",
                HttpMethod.PATCH,
                body,
                authenticate(customer.getEmail(), "password")
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertBodyIsOfType(response.getBody(), CheckoutDTO.class);
    }

    @Test
    void setShippingAddress() {
        final UserDTO customer = testDataCreator.createUserWithUniqueEmail();
        final UserAddressDTO userAddress = testDataCreator.createAddressForUser(customer);
        final CheckoutDTO checkout = testDataCreator.createCheckoutForCustomer(customer);

        final SetCheckoutShippingAddressForm body = SetCheckoutShippingAddressForm.builder()
                .addressId(userAddress.getId())
                .build();

        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/checkouts/" + checkout.getId() + "/shipping-address",
                HttpMethod.PUT,
                body,
                authenticate(customer.getEmail(), "password")
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertBodyIsOfType(response.getBody(), CheckoutDTO.class);
    }

    @Test
    void setShippingMethod() {
        final UserDTO customer = testDataCreator.createUserWithUniqueEmail();
        final UserAddressDTO userAddress = testDataCreator.createAddressForUser(customer);
        final CheckoutDTO checkout = testDataCreator.createCheckoutForCustomer(customer);
        checkoutService.setShippingAddress(checkout.getId(), SetCheckoutShippingAddressForm.builder().addressId(userAddress.getId()).build());
        final ShippingMethodDTO shippingMethod = testDataCreator.createValidShippingMethodForCheckout(userAddress.getAddress().getCountry(), checkout);

        final SetCheckoutShippingMethodForm body = SetCheckoutShippingMethodForm.builder()
                .shippingMethodId(shippingMethod.getId())
                .build();

        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/checkouts/" + checkout.getId() + "/shipping-method",
                HttpMethod.PUT,
                body,
                authenticate(customer.getEmail(), "password")
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertBodyIsOfType(response.getBody(), CheckoutDTO.class);
    }

    @Test
    void createAddress() {
        final UserDTO customer = testDataCreator.createUserWithUniqueEmail();
        final CheckoutDTO checkout = testDataCreator.createCheckoutForCustomer(customer);

        final CreateCheckoutAddressForm body = CreateCheckoutAddressForm.builder()
                .savePaymentMethodAsDefault(true)
                .address(
                        CreateAddressForm.builder()
                                .firstName("First Name")
                                .lastName("Last Name")
                                .streetAddress1("Street Address 1")
                                .country(CountryCode.IT)
                                .countryArea("Country Area")
                                .city("City")
                                .postalCode("ABC123")
                                .phone1("123456789")
                                .build()
                )
                .build();

        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/checkouts/" + checkout.getId() + "/address",
                HttpMethod.POST,
                body,
                authenticate(customer.getEmail(), "password")
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertBodyIsOfType(response.getBody(), CheckoutDTO.class);
    }

    @Test
    void updateAddress() {
        final UserDTO customer = testDataCreator.createUserWithUniqueEmail();
        final UserAddressDTO userAddress = testDataCreator.createAddressForUser(customer);
        final CheckoutDTO checkout = testDataCreator.createCheckoutForCustomer(customer);
        checkoutService.setAddress(checkout.getId(), SetCheckoutAddressForm.builder().addressId(userAddress.getId()).build());

        final UpdateCheckoutAddressForm body = UpdateCheckoutAddressForm.builder()
                .address(
                        UpdateAddressForm.builder()
                                .city("New City")
                                .build()
                )
                .build();

        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/checkouts/" + checkout.getId() + "/address",
                HttpMethod.PATCH,
                body,
                authenticate(customer.getEmail(), "password")
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertBodyIsOfType(response.getBody(), CheckoutDTO.class);
    }

    @Test
    void setAddress() {
        final UserDTO customer = testDataCreator.createUserWithUniqueEmail();
        final UserAddressDTO userAddress = testDataCreator.createAddressForUser(customer);
        final CheckoutDTO checkout = testDataCreator.createCheckoutForCustomer(customer);

        final SetCheckoutAddressForm body = SetCheckoutAddressForm.builder()
                .addressId(userAddress.getId())
                .build();

        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/checkouts/" + checkout.getId() + "/address",
                HttpMethod.PUT,
                body,
                authenticate(customer.getEmail(), "password")
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertBodyIsOfType(response.getBody(), CheckoutDTO.class);
    }

    @Test
    void useSingleAddress() {
        final UserDTO customer = testDataCreator.createUserWithUniqueEmail();
        final CheckoutDTO checkout = testDataCreator.createCheckoutForCustomer(customer);

        final UseSingleAddressForm body = UseSingleAddressForm.builder()
                .useSingleAddress(true)
                .build();

        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/checkouts/" + checkout.getId() + "/single-address",
                HttpMethod.POST,
                body,
                authenticate(customer.getEmail(), "password")
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertBodyIsOfType(response.getBody(), CheckoutDTO.class);
    }

    @Test
    void complete() {
        final UserDTO customer = testDataCreator.createUserWithUniqueEmail();
        final CheckoutDTO checkout = testDataCreator.createCheckoutReadyForPurchase(customer);

        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/checkouts/" + checkout.getId() + "/complete",
                HttpMethod.POST,
                null,
                authenticate(customer.getEmail(), "password")
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertBodyIsOfType(response.getBody(), OrderDTO.class);
    }

    @Test
    void cancel() {
        final UserDTO customer = testDataCreator.createUserWithUniqueEmail();
        final CheckoutDTO checkout = testDataCreator.createCheckoutForCustomer(customer);

        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/checkouts/" + checkout.getId(),
                HttpMethod.DELETE,
                null,
                authenticate(customer.getEmail(), "password")
        );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

}
