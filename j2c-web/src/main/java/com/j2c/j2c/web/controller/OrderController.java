package com.j2c.j2c.web.controller;

import com.j2c.j2c.service.application.OrderService;
import com.j2c.j2c.service.dto.*;
import com.j2c.j2c.service.input.CompleteOrderFulfillmentForm;
import com.j2c.j2c.service.input.Line;
import com.j2c.j2c.service.input.UpdateOrderFulfillmentTrackingNumberForm;
import com.j2c.j2c.web.security.annotation.CanProcessOrders;
import com.j2c.j2c.web.security.annotation.HasReadAccess;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

import static com.j2c.j2c.domain.enums.Authorities.PROCESS_ORDERS;
import static com.j2c.j2c.domain.enums.Authorities.READ_ACCESS;

@RestController
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Endpoints related to orders")
public class OrderController {

    private final OrderService orderService;

    @GetMapping(value = "/api/orders",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @HasReadAccess
    @Operation(security = @SecurityRequirement(name = "JWT"),
            summary = "Retrieves all orders",
            description = "Requires " + READ_ACCESS + " authority (Viewer, Staff, Admin).")
    public Page<OrderDTO> getAll(@ParameterObject final Pageable pageable) {
        return orderService.findAll(pageable);
    }

    @GetMapping(value = "/api/orders/{orderId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @HasReadAccess
    @Operation(security = @SecurityRequirement(name = "JWT"),
            summary = "Retrieves a single order by its id",
            description = "Requires " + READ_ACCESS + " authority (Viewer, Staff, Admin).")
    public OrderDTO get(@PathVariable final Long orderId) {
        return orderService.find(orderId);
    }

    @GetMapping(value = "/api/orders/{orderId}/lines",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @HasReadAccess
    @Operation(security = @SecurityRequirement(name = "JWT"),
            summary = "Retrieves the lines of the specified order",
            description = "Requires " + READ_ACCESS + " authority (Viewer, Staff, Admin).")
    public Page<OrderLineDTO> getLines(
            @PathVariable final Long orderId,
            @ParameterObject final Pageable pageable
    ) {
        return orderService.findLines(orderId, pageable);
    }

    @GetMapping(value = "/api/orders/{orderId}/lines/{lineId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @HasReadAccess
    @Operation(security = @SecurityRequirement(name = "JWT"),
            summary = "Retrieves a single line by id of the specified order",
            description = "Requires " + READ_ACCESS + " authority (Viewer, Staff, Admin).")
    public OrderLineDTO getLine(
            @PathVariable final Long orderId,
            @PathVariable final Long lineId
    ) {
        return orderService.findLine(orderId, lineId);
    }

    @GetMapping(value = "/api/orders/{orderId}/fulfillments",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @HasReadAccess
    @Operation(security = @SecurityRequirement(name = "JWT"),
            summary = "Retrieves the fulfillments of the specified order",
            description = "Requires " + READ_ACCESS + " authority (Viewer, Staff, Admin).")
    public Page<OrderFulfillmentDTO> getFulfillments(
            @PathVariable final Long orderId,
            @ParameterObject final Pageable pageable
    ) {
        return orderService.findFulfillments(orderId, pageable);
    }

    @GetMapping(value = "/api/orders/{orderId}/fulfillments/{fulfillmentId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @HasReadAccess
    @Operation(security = @SecurityRequirement(name = "JWT"),
            summary = "Retrieves a single fulfillment by id belonging to the specified order",
            description = "Requires " + READ_ACCESS + " authority (Viewer, Staff, Admin).")
    public OrderFulfillmentDTO getFulfillment(
            @PathVariable final Long orderId,
            @PathVariable final Long fulfillmentId
    ) {
        return orderService.findFulfillment(orderId, fulfillmentId);
    }

    @GetMapping(value = "/api/orders/{orderId}/fulfillments/{fulfillmentId}/lines",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @HasReadAccess
    @Operation(security = @SecurityRequirement(name = "JWT"),
            summary = "Retrieves the lines of the specified fulfillment",
            description = "Requires " + READ_ACCESS + " authority (Viewer, Staff, Admin).")
    public Page<OrderFulfillmentLineDTO> getFulfillmentLines(
            @PathVariable final Long orderId,
            @PathVariable final Long fulfillmentId,
            @ParameterObject final Pageable pageable
    ) {
        return orderService.findFulfillmentLines(orderId, fulfillmentId, pageable);
    }

    @GetMapping(value = "/api/orders/{orderId}/fulfillments/{fulfillmentId}/lines/{fulfillmentLineId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @HasReadAccess
    @Operation(security = @SecurityRequirement(name = "JWT"),
            summary = "Retrieves a single line by id of the specified fulfillment",
            description = "Requires " + READ_ACCESS + " authority (Viewer, Staff, Admin).")
    public OrderFulfillmentLineDTO getFulfillmentLine(
            @PathVariable final Long orderId,
            @PathVariable final Long fulfillmentId,
            @PathVariable final Long fulfillmentLineId
    ) {
        return orderService.findFulfillmentLine(orderId, fulfillmentId, fulfillmentLineId);
    }

    @PostMapping(value = "/api/orders/{orderId}/confirm",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @CanProcessOrders
    @Operation(security = @SecurityRequirement(name = "JWT"),
            summary = "Confirms an order",
            description = "Returns the updated order. " +
                    "The order must have status CREATED. " +
                    "Requires " + PROCESS_ORDERS + " authority (Staff, Admin).")
    public OrderDTO confirm(@PathVariable final Long orderId) {
        return orderService.confirm(orderId);
    }

    @PostMapping(value = "/api/orders/{orderId}/fulfillments",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @CanProcessOrders
    @Operation(security = @SecurityRequirement(name = "JWT"),
            summary = "Creates a fulfillment for the order with the given id",
            description = "Returns the updated order and order lines, and the created fulfillment and its lines. " +
                    "The order must have status CONFIRMED, PROCESSING, or PARTIALLY_FULFILLED. " +
                    "Requires " + PROCESS_ORDERS + " authority (Staff, Admin).")
    public OrderDTO createFulfillment(
            @PathVariable final Long orderId,
            @RequestBody final List<Line> payload
    ) {
        return orderService.createFulfillment(orderId, payload);
    }

    @PostMapping(value = "/api/orders/{orderId}/fulfillments/{fulfillmentId}/lines",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @CanProcessOrders
    @Operation(security = @SecurityRequirement(name = "JWT"),
            summary = "Adds lines to the fulfillment with the specified id",
            description = "Returns the updated order and order lines, and the updated fulfillment and its added lines. " +
                    "The order must have status CONFIRMED, PROCESSING, or PARTIALLY_FULFILLED. " +
                    "The fulfillment must not be completed. " +
                    "Requires " + PROCESS_ORDERS + " authority (Staff, Admin).")
    public OrderDTO addFulfillmentLines(
            @PathVariable final Long orderId,
            @PathVariable final Long fulfillmentId,
            @RequestBody final List<Line> payload
    ) {
        return orderService.addFulfillmentLines(orderId, fulfillmentId, payload);
    }

    @PatchMapping(value = "/api/orders/{orderId}/fulfillments/{fulfillmentId}/lines",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @CanProcessOrders
    @Operation(security = @SecurityRequirement(name = "JWT"),
            summary = "Bulk updates the reserved quantities of the fulfillment lines with the given ids",
            description = "Returns the updated order and order lines, and the updated fulfillment and its lines. " +
                    "The order must have status CONFIRMED, PROCESSING, or PARTIALLY_FULFILLED. " +
                    "The fulfillment must not be completed. " +
                    "Requires " + PROCESS_ORDERS + " authority (Staff, Admin).")
    public OrderDTO updateFulfillmentLineQuantities(
            @PathVariable final Long orderId,
            @PathVariable final Long fulfillmentId,
            @RequestBody final List<Line> payload
    ) {
        return orderService.updateFulfillmentLineQuantities(orderId, fulfillmentId, payload);
    }

    @DeleteMapping(value = "/api/orders/{orderId}/fulfillments/{fulfillmentId}/lines",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @CanProcessOrders
    @Operation(security = @SecurityRequirement(name = "JWT"),
            summary = "Bulk deletes the fulfillment lines with the given ids",
            description = "Returns the updated order and order lines. " +
                    "The order must have status CONFIRMED, PROCESSING, or PARTIALLY_FULFILLED. " +
                    "The fulfillment must not be completed. " +
                    "Requires " + PROCESS_ORDERS + " authority (Staff, Admin).")
    public OrderDTO deleteFulfillmentLines(
            @PathVariable final Long orderId,
            @PathVariable final Long fulfillmentId,
            @RequestBody final Set<Long> payload
    ) {
        return orderService.deleteFulfillmentLines(orderId, fulfillmentId, payload);
    }

    @PostMapping(value = "/api/orders/{orderId}/fulfillments/{fulfillmentId}/complete",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @CanProcessOrders
    @Operation(security = @SecurityRequirement(name = "JWT"),
            summary = "Completes the fulfillment with the specified id",
            description = "Returns the updated order and order lines, and the completed fulfillment. " +
                    "The order must have status CONFIRMED, PROCESSING, or PARTIALLY_FULFILLED. " +
                    "The fulfillment must not be completed. " +
                    "Requires " + PROCESS_ORDERS + " authority (Staff, Admin).")
    public OrderDTO completeFulfillment(
            @PathVariable final Long orderId,
            @PathVariable final Long fulfillmentId,
            @RequestBody final CompleteOrderFulfillmentForm payload
    ) {
        return orderService.completeFulfillment(orderId, fulfillmentId, payload);
    }

    @PatchMapping(value = "/api/orders/{orderId}/fulfillments/{fulfillmentId}/tracking-number",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @CanProcessOrders
    @Operation(security = @SecurityRequirement(name = "JWT"),
            summary = "Updates the tracking number of the completed fulfillment with the specified id",
            description = "Returns the updated fulfillment. " +
                    "The order must not have the status CANCELLED. " +
                    "The fulfillment must be completed. " +
                    "Requires " + PROCESS_ORDERS + " authority (Staff, Admin).")
    public OrderFulfillmentDTO updateTrackingNumber(
            @PathVariable final Long orderId,
            @PathVariable final Long fulfillmentId,
            @RequestBody final UpdateOrderFulfillmentTrackingNumberForm payload
    ) {
        return orderService.updateTrackingNumber(orderId, fulfillmentId, payload);
    }

    @DeleteMapping(value = "/api/orders/{orderId}/fulfillments/{fulfillmentId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @CanProcessOrders
    @Operation(security = @SecurityRequirement(name = "JWT"),
            summary = "Deletes the fulfillment with the specified id",
            description = "Returns the updated order and order lines. " +
                    "The order must have status CONFIRMED, PROCESSING, or PARTIALLY_FULFILLED. " +
                    "The fulfillment must not be completed. " +
                    "Requires " + PROCESS_ORDERS + " authority (Staff, Admin).")
    public OrderDTO deleteFulfillment(
            @PathVariable final Long orderId,
            @PathVariable final Long fulfillmentId
    ) {
        return orderService.deleteFulfillment(orderId, fulfillmentId);
    }

    @PostMapping(value = "/api/orders/{orderId}/fulfill",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @CanProcessOrders
    @Operation(security = @SecurityRequirement(name = "JWT"),
            summary = "Fulfills the order with the specified id",
            description = "Returns the updated order. " +
                    "The order must not already be fulfilled, and all of its lines' quantities must be fulfilled. " +
                    "Requires " + PROCESS_ORDERS + " authority (Staff, Admin).")
    public OrderDTO fulfill(@PathVariable final Long orderId) {
        return orderService.fulfill(orderId);
    }

    @PostMapping(value = "/api/orders/{orderId}/undo-fulfill",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @CanProcessOrders
    @Operation(security = @SecurityRequirement(name = "JWT"),
            summary = "Returns the fulfilled order to its previous state",
            description = "Returns the updated order. " +
                    "The order must have the status FULFILLED. " +
                    "Requires " + PROCESS_ORDERS + " authority (Staff, Admin).")
    public OrderDTO undoFulfill(@PathVariable final Long orderId) {
        return orderService.undoFulfill(orderId);
    }

    @PostMapping(value = "/api/orders/{orderId}/cancel",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @CanProcessOrders
    @Operation(security = @SecurityRequirement(name = "JWT"),
            summary = "Cancels the order with the specified id",
            description = "Returns the updated order. " +
                    "The order must not already be cancelled. " +
                    "Requires " + PROCESS_ORDERS + " authority (Staff, Admin).")
    public OrderDTO cancel(@PathVariable final Long orderId) {
        return orderService.cancel(orderId);
    }

    @PostMapping(value = "/api/orders/{orderId}/reinstate",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @CanProcessOrders
    @Operation(security = @SecurityRequirement(name = "JWT"),
            summary = "Returns a cancelled order to its previous state",
            description = "Returns the updated order. " +
                    "The order must have the status CANCELLED. " +
                    "Requires " + PROCESS_ORDERS + " authority (Staff, Admin).")
    public OrderDTO reinstate(@PathVariable final Long orderId) {
        return orderService.reinstate(orderId);
    }

}
