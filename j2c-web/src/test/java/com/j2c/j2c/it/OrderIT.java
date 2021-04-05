package com.j2c.j2c.it;

import com.fasterxml.jackson.databind.JsonNode;
import com.j2c.j2c.service.dto.*;
import com.j2c.j2c.service.input.CompleteOrderFulfillmentForm;
import com.j2c.j2c.service.input.Line;
import com.j2c.j2c.service.input.UpdateOrderFulfillmentTrackingNumberForm;
import com.j2c.j2c.it.util.BaseIT;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class OrderIT extends BaseIT {

    @Test
    void getAll() {
        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/orders",
                HttpMethod.GET,
                null,
                adminToken
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertPageOfTypeIsNotEmpty(response.getBody(), OrderDTO.class);
    }

    @Test
    void get() {
        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/orders/1",
                HttpMethod.GET,
                null,
                adminToken
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertBodyIsOfType(response.getBody(), OrderDTO.class);
    }

    @Test
    void getLines() {
        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/orders/1/lines",
                HttpMethod.GET,
                null,
                adminToken
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertPageOfTypeIsNotEmpty(response.getBody(), OrderLineDTO.class);
    }

    @Test
    void getLine() {
        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/orders/1/lines/1",
                HttpMethod.GET,
                null,
                adminToken
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertBodyIsOfType(response.getBody(), OrderLineDTO.class);
    }

    @Test
    void getFulfillments() {
        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/orders/1/fulfillments",
                HttpMethod.GET,
                null,
                adminToken
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertPageOfTypeIsNotEmpty(response.getBody(), OrderFulfillmentDTO.class);
    }

    @Test
    void getFulfillment() {
        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/orders/1/fulfillments/1",
                HttpMethod.GET,
                null,
                adminToken
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertBodyIsOfType(response.getBody(), OrderFulfillmentDTO.class);
    }

    @Test
    void getFulfillmentLines() {
        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/orders/1/fulfillments/1/lines",
                HttpMethod.GET,
                null,
                adminToken
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertPageOfTypeIsNotEmpty(response.getBody(), OrderFulfillmentLineDTO.class);
    }

    @Test
    void getFulfillmentLine() {
        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/orders/1/fulfillments/1/lines/1",
                HttpMethod.GET,
                null,
                adminToken
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertBodyIsOfType(response.getBody(), OrderFulfillmentLineDTO.class);
    }

    @Test
    void confirm() {
        final UserDTO customer = testDataCreator.createUserWithUniqueEmail();
        final OrderDTO order = testDataCreator.createUnconfirmedOrderForCustomer(customer);

        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/orders/" + order.getId() + "/confirm",
                HttpMethod.POST,
                null,
                adminToken
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertBodyIsOfType(response.getBody(), OrderDTO.class);
    }

    @Test
    void createFulfillment() {
        final UserDTO customer = testDataCreator.createUserWithUniqueEmail();
        final OrderDTO order = testDataCreator.createUnconfirmedOrderForCustomer(customer);
        orderService.confirm(order.getId());

        final List<Line> body = List.of(
                Line.builder().id(order.getLines().get(0).getId()).quantity(1).build()
        );

        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/orders/" + order.getId() + "/fulfillments",
                HttpMethod.POST,
                body,
                adminToken
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertBodyIsOfType(response.getBody(), OrderDTO.class);
    }

    @Test
    void addFulfillmentLines() {
        final UserDTO customer = testDataCreator.createUserWithUniqueEmail();
        final OrderDTO order = testDataCreator.createUnconfirmedOrderForCustomer(customer);
        orderService.confirm(order.getId());
        final OrderLineDTO orderLine = order.getLines().get(0);
        final OrderFulfillmentDTO fulfillment = testDataCreator.createSingleFinalizingFulfillmentForOrder(order);

        final List<Line> body = List.of(
                Line.builder().id(orderLine.getId()).quantity(1).build()
        );

        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/orders/" + order.getId() + "/fulfillments/" + fulfillment.getId() + "/lines",
                HttpMethod.POST,
                body,
                adminToken
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertBodyIsOfType(response.getBody(), OrderDTO.class);
    }

    @Test
    void updateFulfillmentLineQuantities() {
        final UserDTO customer = testDataCreator.createUserWithUniqueEmail();
        final OrderDTO order = testDataCreator.createUnconfirmedOrderForCustomer(customer);
        orderService.confirm(order.getId());
        final OrderFulfillmentDTO fulfillment = testDataCreator.createSingleFinalizingFulfillmentForOrder(order);
        final OrderFulfillmentLineDTO fulfillmentLine = fulfillment.getLines().get(0);

        final List<Line> body = List.of(
                Line.builder().id(fulfillmentLine.getId()).quantity(1).build()
        );

        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/orders/" + order.getId() + "/fulfillments/" + fulfillment.getId() + "/lines",
                HttpMethod.PATCH,
                body,
                adminToken
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertBodyIsOfType(response.getBody(), OrderDTO.class);
    }

    @Test
    void deleteFulfillmentLines() {
        final UserDTO customer = testDataCreator.createUserWithUniqueEmail();
        final OrderDTO order = testDataCreator.createUnconfirmedOrderForCustomer(customer);
        orderService.confirm(order.getId());
        final OrderFulfillmentDTO fulfillment = testDataCreator.createSingleFinalizingFulfillmentForOrder(order);
        final OrderFulfillmentLineDTO fulfillmentLine = fulfillment.getLines().get(0);

        final Set<Long> body = Set.of(fulfillmentLine.getId());

        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/orders/" + order.getId() + "/fulfillments/" + fulfillment.getId() + "/lines",
                HttpMethod.DELETE,
                body,
                adminToken
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertBodyIsOfType(response.getBody(), OrderDTO.class);
    }

    @Test
    void completeFulfillment() {
        final UserDTO customer = testDataCreator.createUserWithUniqueEmail();
        final OrderDTO order = testDataCreator.createUnconfirmedOrderForCustomer(customer);
        orderService.confirm(order.getId());
        final OrderFulfillmentDTO fulfillment = testDataCreator.createSingleFinalizingFulfillmentForOrder(order);

        final CompleteOrderFulfillmentForm body = CompleteOrderFulfillmentForm.builder()
                .trackingNumber("ABC_123")
                .build();

        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/orders/" + order.getId() + "/fulfillments/" + fulfillment.getId() + "/complete",
                HttpMethod.POST,
                body,
                adminToken
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertBodyIsOfType(response.getBody(), OrderDTO.class);
    }

    @Test
    void updateTrackingNumber() {
        final UserDTO customer = testDataCreator.createUserWithUniqueEmail();
        final OrderDTO order = testDataCreator.createUnconfirmedOrderForCustomer(customer);
        orderService.confirm(order.getId());
        final OrderFulfillmentDTO fulfillment = testDataCreator.createSingleFinalizingFulfillmentForOrder(order);
        orderService.completeFulfillment(order.getId(), fulfillment.getId(), CompleteOrderFulfillmentForm.builder().build());

        final UpdateOrderFulfillmentTrackingNumberForm body = UpdateOrderFulfillmentTrackingNumberForm.builder()
                .trackingNumber("ABC123")
                .build();

        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/orders/" + order.getId() + "/fulfillments/" + fulfillment.getId() + "/tracking-number",
                HttpMethod.PATCH,
                body,
                adminToken
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertBodyIsOfType(response.getBody(), OrderFulfillmentDTO.class);
    }

    @Test
    void deleteFulfillment() {
        final UserDTO customer = testDataCreator.createUserWithUniqueEmail();
        final OrderDTO order = testDataCreator.createUnconfirmedOrderForCustomer(customer);
        orderService.confirm(order.getId());
        final OrderFulfillmentDTO fulfillment = testDataCreator.createSingleFinalizingFulfillmentForOrder(order);

        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/orders/" + order.getId() + "/fulfillments/" + fulfillment.getId(),
                HttpMethod.DELETE,
                null,
                adminToken
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertBodyIsOfType(response.getBody(), OrderDTO.class);
    }

    @Test
    void fulfill() {
        final UserDTO customer = testDataCreator.createUserWithUniqueEmail();
        final OrderDTO order = testDataCreator.createUnconfirmedOrderForCustomer(customer);
        orderService.confirm(order.getId());
        final OrderFulfillmentDTO fulfillment = testDataCreator.createSingleFinalizingFulfillmentForOrder(order);
        orderService.completeFulfillment(order.getId(), fulfillment.getId(), CompleteOrderFulfillmentForm.builder().build());

        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/orders/" + order.getId() + "/fulfill",
                HttpMethod.POST,
                null,
                adminToken
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertBodyIsOfType(response.getBody(), OrderDTO.class);
    }

    @Test
    void undoFulfill() {
        final UserDTO customer = testDataCreator.createUserWithUniqueEmail();
        final OrderDTO order = testDataCreator.createUnconfirmedOrderForCustomer(customer);
        orderService.confirm(order.getId());
        final OrderFulfillmentDTO fulfillment = testDataCreator.createSingleFinalizingFulfillmentForOrder(order);
        orderService.completeFulfillment(order.getId(), fulfillment.getId(), CompleteOrderFulfillmentForm.builder().build());
        orderService.fulfill(order.getId());

        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/orders/" + order.getId() + "/undo-fulfill",
                HttpMethod.POST,
                null,
                adminToken
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertBodyIsOfType(response.getBody(), OrderDTO.class);
    }

    @Test
    void cancel() {
        final UserDTO customer = testDataCreator.createUserWithUniqueEmail();
        final OrderDTO order = testDataCreator.createUnconfirmedOrderForCustomer(customer);

        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/orders/" + order.getId() + "/cancel",
                HttpMethod.POST,
                null,
                adminToken
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertBodyIsOfType(response.getBody(), OrderDTO.class);
    }

    @Test
    void reinstate() {
        final UserDTO customer = testDataCreator.createUserWithUniqueEmail();
        final OrderDTO order = testDataCreator.createUnconfirmedOrderForCustomer(customer);
        orderService.cancel(order.getId());

        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/orders/" + order.getId() + "/reinstate",
                HttpMethod.POST,
                null,
                adminToken
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertBodyIsOfType(response.getBody(), OrderDTO.class);
    }

}
