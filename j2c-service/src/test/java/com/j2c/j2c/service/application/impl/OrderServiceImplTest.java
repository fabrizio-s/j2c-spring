package com.j2c.j2c.service.application.impl;

import com.j2c.j2c.domain.entity.Order;
import com.j2c.j2c.domain.entity.OrderFulfillment;
import com.j2c.j2c.domain.entity.OrderFulfillmentLine;
import com.j2c.j2c.domain.entity.OrderLine;
import com.j2c.j2c.domain.enums.OrderStatus;
import com.j2c.j2c.service.dto.OrderDTO;
import com.j2c.j2c.service.dto.OrderFulfillmentDTO;
import com.j2c.j2c.service.dto.OrderFulfillmentLineDTO;
import com.j2c.j2c.service.exception.InvalidInputException;
import com.j2c.j2c.service.exception.ResourceNotFoundException;
import com.j2c.j2c.service.exception.ServiceException;
import com.j2c.j2c.service.input.CompleteOrderFulfillmentForm;
import com.j2c.j2c.service.input.Line;
import com.j2c.j2c.service.input.UpdateOrderFulfillmentTrackingNumberForm;
import com.j2c.j2c.service.test.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

import static com.j2c.j2c.domain.exception.DomainErrorMessages.*;
import static com.j2c.j2c.service.exception.J2cServiceErrorMessages.RESOURCES_NOT_FOUND;
import static com.j2c.j2c.service.exception.J2cServiceErrorMessages.RESOURCE_NOT_FOUND;
import static com.j2c.j2c.service.test.MockEntity.*;
import static com.j2c.j2c.service.test.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

class OrderServiceImplTest extends BaseServiceTest {

    @Autowired
    private OrderServiceImpl service;

    @Autowired
    private OrderServiceStubber stubber;

    @Test
    public void confirm_NullOrderId_ShouldThrowInvalidInputException() {
        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.confirm(null)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("order id must not be null"))
        );
    }

    @Test
    public void confirm_OrderDoesNotExist_ShouldThrowResourceNotFoundException() {
        final Long orderId = 1L;

        stubber.confirm()
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.confirm(orderId)
        );
        assertTrue(exception.getMessage().contains(String.format(RESOURCE_NOT_FOUND, "order", orderId)));
    }

    @Test
    public void confirm_OrderStatusIsNotCREATED_ShouldThrowServiceException() {
        final Long orderId = 1L;

        stubber.confirm()
                .orderToConfirm(orderWithIdAndStatus(orderId, OrderStatus.PROCESSING))
                .stub();

        final ServiceException exception = assertThrows(
                ServiceException.class,
                () -> service.confirm(orderId)
        );
        assertTrue(exception.getMessage().matches(String.format(ORDER_STATUS_MUST_BE_CREATED, orderId)));
    }

    @Test
    public void confirm_HappyPath_ShouldReturnConfirmedOrder() {
        final Long orderId = 1L;

        stubber.confirm()
                .orderToConfirm(orderWithIdAndStatus(orderId, OrderStatus.CREATED))
                .stub();

        final OrderDTO orderDTO = service.confirm(orderId);

        assertNotNull(orderDTO);
        assertEquals(OrderStatus.CONFIRMED, orderDTO.getStatus());
    }

    @Test
    public void createFulfillment_NullOrderId_ShouldThrowInvalidInputException() {
        final List<Line> lines = hpLines();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createFulfillment(null, lines)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("order id must not be null"))
        );
    }

    @Test
    public void createFulfillment_NullLines_ShouldThrowInvalidInputException() {
        final Long orderId = 1L;

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createFulfillment(orderId, null)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("lines must not be empty"))
        );
    }

    @Test
    public void createFulfillment_EmptyLines_ShouldThrowInvalidInputException() {
        final Long orderId = 1L;
        final List<Line> lines = Collections.emptyList();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createFulfillment(orderId, lines)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("lines must not be empty"))
        );
    }

    @Test
    public void createFulfillment_AnyLineIsNull_ShouldThrowInvalidInputException() {
        final Long orderId = 1L;
        final List<Line> lines = hpLines();

        lines.add(null);

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createFulfillment(orderId, lines)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("lines must not contain null elements"))
        );
    }

    @Test
    public void createFulfillment_AnyLineHasNullId_ShouldThrowInvalidInputException() {
        final Long orderId = 1L;
        final List<Line> lines = hpLines();

        lines.add(Line.builder().id(null).quantity(1).build());

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createFulfillment(orderId, lines)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("id must not be null"))
        );
    }

    @Test
    public void createFulfillment_AnyLineHasNonPositiveQuantity_ShouldThrowInvalidInputException() {
        final Long orderId = 1L;
        final List<Line> lines = hpLines();

        lines.add(Line.builder().id(1L).quantity(-1).build());

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createFulfillment(orderId, lines)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("quantity must be greater than 0"))
        );
    }

    @Test
    public void createFulfillment_OrderDoesNotExist_ShouldThrowResourceNotFoundException() {
        final Long orderId = 1L;
        final List<Line> lines = hpLines();

        stubber.createFulfillment()
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.createFulfillment(orderId, lines)
        );
        assertTrue(exception.getMessage().contains(String.format(RESOURCE_NOT_FOUND, "order", orderId)));
    }

    @Test
    public void createFulfillment_AnyOrderLineDoesNotExist_ShouldThrowResourceNotFoundException() {
        final Long orderId = 1L;
        final List<Line> lines = hpLines();

        final Order order = orderWithIdAndStatus(orderId, OrderStatus.PROCESSING);
        stubber.createFulfillment()
                .order(order)
                .lines(removeFirst(orderLinesWithIdsForOrder(lines, order)))
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.createFulfillment(orderId, lines)
        );
        assertTrue(exception.getMessage().matches(String.format(RESOURCES_NOT_FOUND, "order line", "[0-9]+")));
    }

    @Test
    public void createFulfillment_AnyOrderLineDoesNotBelongToOrder_ShouldThrowServiceException() {
        final Long orderId = 1L;
        final List<Line> lines = hpLines();

        final Order order = orderWithIdAndStatus(orderId, OrderStatus.PROCESSING);
        stubber.createFulfillment()
                .order(order)
                .lines(transformFirst(orderLinesWithIdsForOrder(lines, order), this::setToDifferentOrder))
                .stub();

        final ServiceException exception = assertThrows(
                ServiceException.class,
                () -> service.createFulfillment(orderId, lines)
        );
        assertTrue(exception.getMessage().matches(String.format(LINE_DOES_NOT_BELONG_TO_ORDER, "[0-9]+", orderId)));
    }

    @Test
    public void createFulfillment_OrderIsNotProcessable_ShouldThrowServiceException() {
        final Long orderId = 1L;
        final List<Line> lines = hpLines();

        final Order order = orderWithIdAndStatus(orderId, OrderStatus.CREATED);
        stubber.createFulfillment()
                .order(order)
                .lines(orderLinesWithIdsForOrder(lines, order))
                .stub();

        final ServiceException exception = assertThrows(
                ServiceException.class,
                () -> service.createFulfillment(orderId, lines)
        );
        assertTrue(exception.getMessage().matches(String.format(ORDER_NOT_PROCESSABLE, orderId, "[A-Z]+")));
    }

    @Test
    public void createFulfillment_AnyOrderLineDoesNotRequireShipping_ShouldThrowServiceException() {
        final Long orderId = 1L;
        final List<Line> lines = hpLines();

        final Order order = orderWithIdAndStatus(orderId, OrderStatus.PROCESSING);
        stubber.createFulfillment()
                .order(order)
                .lines(transformFirst(orderLinesWithIdsForOrder(lines, order), this::setNoShippingRequired))
                .stub();

        final ServiceException exception = assertThrows(
                ServiceException.class,
                () -> service.createFulfillment(orderId, lines)
        );
        assertTrue(exception.getMessage().matches(String.format(ORDER_LINE_DOES_NOT_REQUIRE_SHIPPING, "[0-9]+")));
    }

    @Test
    public void createFulfillment_AnyLineQuantityIsGreaterThanAssignableQuantity_ShouldThrowServiceException() {
        final Long orderId = 1L;
        final List<Line> lines = hpLines();

        final Order order = orderWithIdAndStatus(orderId, OrderStatus.PROCESSING);
        stubber.createFulfillment()
                .order(order)
                .lines(transformFirst(orderLinesWithIdsForOrder(lines, order), this::setAlreadyFulfilled))
                .stub();

        final ServiceException exception = assertThrows(
                ServiceException.class,
                () -> service.createFulfillment(orderId, lines)
        );
        assertTrue(exception.getMessage().matches(String.format(INSUFFICIENT_ORDER_LINE_ASSIGNABLE_QUANTITY, "[0-9]+", "[0-9]+")));
    }

    @Test
    public void createFulfillment_HappyPath_ShouldReturnUpdatedLinesAndCreatedFulfillment() {
        final Long orderId = 1L;
        final List<Line> lines = hpLines();

        final Order order = orderWithIdAndStatus(orderId, OrderStatus.PROCESSING);
        stubber.createFulfillment()
                .order(order)
                .lines(orderLinesWithIdsForOrder(lines, order))
                .stub();

        final OrderDTO orderDTO = service.createFulfillment(orderId, lines);

        assertNotNull(orderDTO);
        assertEquals(lines.size(), orderDTO.getUpdatedLines().size());
        assertEquals(lines.size(), orderDTO.getFulfillment().getLines().size());
    }

    @Test
    public void createFulfillment_OrderStatusIsCONFIRMED_ShouldTransitionOrderToPROCESSINGStatus() {
        final Long orderId = 1L;
        final List<Line> lines = hpLines();

        final Order order = orderWithIdAndStatus(orderId, OrderStatus.CONFIRMED);
        stubber.createFulfillment()
                .order(order)
                .lines(orderLinesWithIdsForOrder(lines, order))
                .stub();

        final OrderDTO orderDTO = service.createFulfillment(orderId, lines);

        assertEquals(orderDTO.getStatus(), OrderStatus.PROCESSING);
    }

    @Test
    public void addFulfillmentLines_NullOrderId_ShouldThrowInvalidInputException() {
        final Long fulfillmentId = 1L;
        final List<Line> lines = hpLines();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.addFulfillmentLines(null, fulfillmentId, lines)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("order id must not be null"))
        );
    }

    @Test
    public void addFulfillmentLines_NullFulfillmentId_ShouldThrowInvalidInputException() {
        final Long orderId = 1L;
        final List<Line> lines = hpLines();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.addFulfillmentLines(orderId, null, lines)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("fulfillment id must not be null"))
        );
    }

    @Test
    public void addFulfillmentLines_NullLines_ShouldThrowInvalidInputException() {
        final Long orderId = 1L;
        final Long fulfillmentId = 1L;

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.addFulfillmentLines(orderId, fulfillmentId, null)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("lines must not be empty"))
        );
    }

    @Test
    public void addFulfillmentLines_EmptyLines_ShouldThrowInvalidInputException() {
        final Long orderId = 1L;
        final Long fulfillmentId = 1L;
        final List<Line> lines = Collections.emptyList();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.addFulfillmentLines(orderId, fulfillmentId, lines)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("lines must not be empty"))
        );
    }

    @Test
    public void addFulfillmentLines_AnyLineIsNull_ShouldThrowInvalidInputException() {
        final Long orderId = 1L;
        final Long fulfillmentId = 1L;
        final List<Line> lines = hpLines();

        lines.add(null);

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.addFulfillmentLines(orderId, fulfillmentId, lines)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("lines must not contain null elements"))
        );
    }

    @Test
    public void addFulfillmentLines_AnyLineHasNullId_ShouldThrowInvalidInputException() {
        final Long orderId = 1L;
        final Long fulfillmentId = 1L;
        final List<Line> lines = hpLines();

        lines.add(Line.builder().id(null).quantity(1).build());

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.addFulfillmentLines(orderId, fulfillmentId, lines)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("id must not be null"))
        );
    }

    @Test
    public void addFulfillmentLines_AnyLineHasNonPositiveQuantity_ShouldThrowInvalidInputException() {
        final Long orderId = 1L;
        final Long fulfillmentId = 1L;
        final List<Line> lines = hpLines();

        lines.add(Line.builder().id(1L).quantity(-1).build());

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.addFulfillmentLines(orderId, fulfillmentId, lines)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("quantity must be greater than 0"))
        );
    }

    @Test
    public void addFulfillmentLines_OrderDoesNotExist_ShouldThrowResourceNotFoundException() {
        final Long orderId = 1L;
        final Long fulfillmentId = 1L;
        final List<Line> lines = hpLines();

        stubber.addFulfillmentLines()
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.addFulfillmentLines(orderId, fulfillmentId, lines)
        );
        assertTrue(exception.getMessage().contains(String.format(RESOURCE_NOT_FOUND, "order", orderId)));
    }

    @Test
    public void addFulfillmentLines_FulfillmentDoesNotExist_ShouldThrowResourceNotFoundException() {
        final Long orderId = 1L;
        final Long fulfillmentId = 1L;
        final List<Line> lines = hpLines();

        final Order order = orderWithIdAndStatus(orderId, OrderStatus.PROCESSING);
        stubber.addFulfillmentLines()
                .order(order)
                .lines(orderLinesWithIdsForOrder(lines, order))
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.addFulfillmentLines(orderId, fulfillmentId, lines)
        );
        assertTrue(exception.getMessage().contains(String.format(RESOURCE_NOT_FOUND, "order fulfillment", fulfillmentId)));
    }

    @Test
    public void addFulfillmentLines_AnyOrderLineDoesNotExist_ShouldThrowResourceNotFoundException() {
        final Long orderId = 1L;
        final Long fulfillmentId = 1L;
        final List<Line> lines = hpLines();

        final Order order = orderWithIdAndStatus(orderId, OrderStatus.PROCESSING);
        stubber.addFulfillmentLines()
                .order(order)
                .fulfillment(fulfillmentWithIdForOrder(fulfillmentId, order))
                .lines(removeFirst(orderLinesWithIdsForOrder(lines, order)))
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.addFulfillmentLines(orderId, fulfillmentId, lines)
        );
        assertTrue(exception.getMessage().matches(String.format(RESOURCES_NOT_FOUND, "order line", "[0-9]+")));
    }

    @Test
    public void addFulfillmentLines_FulfillmentDoesNotBelongToOrder_ShouldThrowServiceException() {
        final Long orderId = 1L;
        final Long fulfillmentId = 1L;
        final List<Line> lines = hpLines();

        final Order order = orderWithIdAndStatus(orderId, OrderStatus.PROCESSING);
        stubber.addFulfillmentLines()
                .order(order)
                .fulfillment(fulfillmentWithIdForOrder(fulfillmentId, order().build()))
                .lines(orderLinesWithIdsForOrder(lines, order))
                .stub();

        final ServiceException exception = assertThrows(
                ServiceException.class,
                () -> service.addFulfillmentLines(orderId, fulfillmentId, lines)
        );
        assertTrue(exception.getMessage().matches(String.format(FULFILLMENT_DOES_NOT_BELONG_TO_ORDER, fulfillmentId, orderId)));
    }

    @Test
    public void addFulfillmentLines_AnyOrderLineDoesNotBelongToOrder_ShouldThrowServiceException() {
        final Long orderId = 1L;
        final Long fulfillmentId = 1L;
        final List<Line> lines = hpLines();

        final Order order = orderWithIdAndStatus(orderId, OrderStatus.PROCESSING);
        stubber.addFulfillmentLines()
                .order(order)
                .fulfillment(fulfillmentWithIdForOrder(fulfillmentId, order))
                .lines(transformFirst(orderLinesWithIdsForOrder(lines, order), this::setToDifferentOrder))
                .stub();

        final ServiceException exception = assertThrows(
                ServiceException.class,
                () -> service.addFulfillmentLines(orderId, fulfillmentId, lines)
        );
        assertTrue(exception.getMessage().matches(String.format(LINE_DOES_NOT_BELONG_TO_ORDER, "[0-9]+", orderId)));
    }

    @Test
    public void addFulfillmentLines_OrderIsNotProcessable_ShouldThrowServiceException() {
        final Long orderId = 1L;
        final Long fulfillmentId = 1L;
        final List<Line> lines = hpLines();

        final Order order = orderWithIdAndStatus(orderId, OrderStatus.FULFILLED);
        stubber.addFulfillmentLines()
                .order(order)
                .fulfillment(fulfillmentWithIdForOrder(fulfillmentId, order))
                .lines(orderLinesWithIdsForOrder(lines, order))
                .stub();

        final ServiceException exception = assertThrows(
                ServiceException.class,
                () -> service.addFulfillmentLines(orderId, fulfillmentId, lines)
        );
        assertTrue(exception.getMessage().matches(String.format(ORDER_NOT_PROCESSABLE, orderId, "[A-Z]+")));
    }

    @Test
    public void addFulfillmentLines_FulfillmentIsAlreadyCompleted_ShouldThrowServiceException() {
        final Long orderId = 1L;
        final Long fulfillmentId = 1L;
        final List<Line> lines = hpLines();

        final Order order = orderWithIdAndStatus(orderId, OrderStatus.PROCESSING);
        stubber.addFulfillmentLines()
                .order(order)
                .fulfillment(
                        orderFulfillment()
                                .id(fulfillmentId)
                                .order(order)
                                .completed(true)
                                .build()
                )
                .lines(orderLinesWithIdsForOrder(lines, order))
                .stub();

        final ServiceException exception = assertThrows(
                ServiceException.class,
                () -> service.addFulfillmentLines(orderId, fulfillmentId, lines)
        );
        assertTrue(exception.getMessage().matches(String.format(FULFILLMENT_ALREADY_COMPLETED, fulfillmentId)));
    }

    @Test
    public void addFulfillmentLines_AnyOrderLineDoesNotRequireShipping_ShouldThrowServiceException() {
        final Long orderId = 1L;
        final Long fulfillmentId = 1L;
        final List<Line> lines = hpLines();

        final Order order = orderWithIdAndStatus(orderId, OrderStatus.PROCESSING);
        stubber.addFulfillmentLines()
                .order(order)
                .fulfillment(fulfillmentWithIdForOrder(fulfillmentId, order))
                .lines(transformFirst(orderLinesWithIdsForOrder(lines, order), this::setNoShippingRequired))
                .stub();

        final ServiceException exception = assertThrows(
                ServiceException.class,
                () -> service.addFulfillmentLines(orderId, fulfillmentId, lines)
        );
        assertTrue(exception.getMessage().matches(String.format(ORDER_LINE_DOES_NOT_REQUIRE_SHIPPING, "[0-9]+")));
    }

    @Test
    public void addFulfillmentLines_AnyLineQuantityIsGreaterThanAssignableQuantity_ShouldThrowServiceException() {
        final Long orderId = 1L;
        final Long fulfillmentId = 1L;
        final List<Line> lines = hpLines();

        final Order order = orderWithIdAndStatus(orderId, OrderStatus.PROCESSING);
        stubber.addFulfillmentLines()
                .order(order)
                .fulfillment(fulfillmentWithIdForOrder(fulfillmentId, order))
                .lines(transformFirst(orderLinesWithIdsForOrder(lines, order), this::setAlreadyFulfilled))
                .stub();

        final ServiceException exception = assertThrows(
                ServiceException.class,
                () -> service.addFulfillmentLines(orderId, fulfillmentId, lines)
        );
        assertTrue(exception.getMessage().matches(String.format(INSUFFICIENT_ORDER_LINE_ASSIGNABLE_QUANTITY, "[0-9]+", "[0-9]+")));
    }

    @Test
    public void addFulfillmentLines_HappyPath_ShouldReturnUpdatedOrderLinesAndAddedFulfillmentLines() {
        final Long orderId = 1L;
        final Long fulfillmentId = 1L;
        final List<Line> lines = hpLines();

        final Order order = orderWithIdAndStatus(orderId, OrderStatus.PROCESSING);
        stubber.addFulfillmentLines()
                .order(order)
                .fulfillment(fulfillmentWithIdForOrder(fulfillmentId, order))
                .lines(orderLinesWithIdsForOrder(lines, order))
                .stub();

        final OrderDTO orderDTO = service.addFulfillmentLines(orderId, fulfillmentId, lines);

        assertNotNull(orderDTO);
        assertEquals(lines.size(), orderDTO.getUpdatedLines().size());
        assertEquals(lines.size(), orderDTO.getFulfillment().getAddedLines().size());
    }

    @Test
    public void addFulfillmentLines_FulfillmentLineAlreadyExistsForOrderLine_ShouldNotAddNewFulfillmentLine() {
        final Long orderId = 1L;
        final Long fulfillmentId = 1L;
        final List<Line> lines = hpLines();

        final Order order = orderWithIdAndStatus(orderId, OrderStatus.PROCESSING);
        final OrderFulfillment fulfillment = fulfillmentWithIdForOrder(fulfillmentId, order);
        final List<OrderLine> orderLines = orderLinesWithIdsForOrder(lines, order);
        orderFulfillmentLine()
                .id(fulfillmentId)
                .fulfillment(fulfillment)
                .orderLine(orderLines.get(0))
                .build();
        stubber.addFulfillmentLines()
                .order(order)
                .fulfillment(fulfillment)
                .lines(orderLines)
                .stub();

        final OrderDTO orderDTO = service.addFulfillmentLines(orderId, fulfillmentId, lines);

        final List<OrderFulfillmentLineDTO> addedFulfillmentLinesDTO = orderDTO.getFulfillment().getAddedLines();

        assertEquals(lines.size() - 1, addedFulfillmentLinesDTO.size());
    }

    @Test
    public void updateFulfillmentLineQuantities_NullOrderId_ShouldThrowInvalidInputException() {
        final Long fulfillmentId = 1L;
        final List<Line> lines = hpLines();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateFulfillmentLineQuantities(null, fulfillmentId, lines)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("order id must not be null"))
        );
    }

    @Test
    public void updateFulfillmentLineQuantities_NullFulfillmentId_ShouldThrowInvalidInputException() {
        final Long orderId = 1L;
        final List<Line> lines = hpLines();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateFulfillmentLineQuantities(orderId, null, lines)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("fulfillment id must not be null"))
        );
    }

    @Test
    public void updateFulfillmentLineQuantities_NullLines_ShouldThrowInvalidInputException() {
        final Long orderId = 1L;
        final Long fulfillmentId = 1L;

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateFulfillmentLineQuantities(orderId, fulfillmentId, null)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("lines must not be empty"))
        );
    }

    @Test
    public void updateFulfillmentLineQuantities_EmptyLines_ShouldThrowInvalidInputException() {
        final Long orderId = 1L;
        final Long fulfillmentId = 1L;
        final List<Line> lines = Collections.emptyList();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateFulfillmentLineQuantities(orderId, fulfillmentId, lines)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("lines must not be empty"))
        );
    }

    @Test
    public void updateFulfillmentLineQuantities_AnyLineIsNull_ShouldThrowInvalidInputException() {
        final Long orderId = 1L;
        final Long fulfillmentId = 1L;
        final List<Line> lines = hpLines();

        lines.add(null);

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateFulfillmentLineQuantities(orderId, fulfillmentId, lines)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("lines must not contain null elements"))
        );
    }

    @Test
    public void updateFulfillmentLineQuantities_AnyLineHasNullId_ShouldThrowInvalidInputException() {
        final Long orderId = 1L;
        final Long fulfillmentId = 1L;
        final List<Line> lines = hpLines();

        lines.add(Line.builder().id(null).quantity(1).build());

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateFulfillmentLineQuantities(orderId, fulfillmentId, lines)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("id must not be null"))
        );
    }

    @Test
    public void updateFulfillmentLineQuantities_AnyLineHasNonPositiveQuantity_ShouldThrowInvalidInputException() {
        final Long orderId = 1L;
        final Long fulfillmentId = 1L;
        final List<Line> lines = hpLines();

        lines.add(Line.builder().id(1L).quantity(-1).build());

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateFulfillmentLineQuantities(orderId, fulfillmentId, lines)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("quantity must be greater than 0"))
        );
    }

    @Test
    public void updateFulfillmentLineQuantities_OrderDoesNotExist_ShouldThrowResourceNotFoundException() {
        final Long orderId = 1L;
        final Long fulfillmentId = 1L;
        final List<Line> lines = hpLines();

        stubber.updateFulfillmentLineQuantities()
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.updateFulfillmentLineQuantities(orderId, fulfillmentId, lines)
        );
        assertTrue(exception.getMessage().contains(String.format(RESOURCE_NOT_FOUND, "order", orderId)));
    }

    @Test
    public void updateFulfillmentLineQuantities_FulfillmentDoesNotExist_ShouldThrowResourceNotFoundException() {
        final Long orderId = 1L;
        final Long fulfillmentId = 1L;
        final List<Line> lines = hpLines();

        final Order order = orderWithIdAndStatus(orderId, OrderStatus.PROCESSING);
        stubber.updateFulfillmentLineQuantities()
                .order(order)
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.updateFulfillmentLineQuantities(orderId, fulfillmentId, lines)
        );
        assertTrue(exception.getMessage().contains(String.format(RESOURCE_NOT_FOUND, "order fulfillment", fulfillmentId)));
    }

    @Test
    public void updateFulfillmentLineQuantities_AnyFulfillmentLineDoesNotExist_ShouldThrowResourceNotFoundException() {
        final Long orderId = 1L;
        final Long fulfillmentId = 1L;
        final List<Line> lines = hpLines();

        final Order order = orderWithIdAndStatus(orderId, OrderStatus.PROCESSING);
        final OrderFulfillment fulfillment = fulfillmentWithIdForOrder(fulfillmentId, order);
        stubber.updateFulfillmentLineQuantities()
                .order(order)
                .fulfillment(fulfillment)
                .fulfillmentLinesToUpdate(removeFirst(fulfillmentLinesWithIdsForFulfillment(lines, fulfillment)))
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.updateFulfillmentLineQuantities(orderId, fulfillmentId, lines)
        );
        assertTrue(exception.getMessage().matches(String.format(RESOURCES_NOT_FOUND, "order fulfillment line", "[0-9]+")));
    }

    @Test
    public void updateFulfillmentLineQuantities_FulfillmentDoesNotBelongToOrder_ShouldThrowServiceException() {
        final Long orderId = 1L;
        final Long fulfillmentId = 1L;
        final List<Line> lines = hpLines();

        final Order order = orderWithIdAndStatus(orderId, OrderStatus.PROCESSING);
        final OrderFulfillment fulfillment = fulfillmentWithIdForOrder(fulfillmentId, order().build());
        stubber.updateFulfillmentLineQuantities()
                .order(order)
                .fulfillment(fulfillment)
                .fulfillmentLinesToUpdate(fulfillmentLinesWithIdsForFulfillment(lines, fulfillment))
                .stub();

        final ServiceException exception = assertThrows(
                ServiceException.class,
                () -> service.updateFulfillmentLineQuantities(orderId, fulfillmentId, lines)
        );
        assertTrue(exception.getMessage().matches(String.format(FULFILLMENT_DOES_NOT_BELONG_TO_ORDER, fulfillmentId, orderId)));
    }

    @Test
    public void updateFulfillmentLineQuantities_AnyLineDoesNotBelongToFulfillment_ShouldThrowServiceException() {
        final Long orderId = 1L;
        final Long fulfillmentId = 1L;
        final List<Line> lines = hpLines();

        final Order order = orderWithIdAndStatus(orderId, OrderStatus.PROCESSING);
        final OrderFulfillment fulfillment = fulfillmentWithIdForOrder(fulfillmentId, order);
        stubber.updateFulfillmentLineQuantities()
                .order(order)
                .fulfillment(fulfillment)
                .fulfillmentLinesToUpdate(fulfillmentLinesWithIdsForFulfillment(lines, orderFulfillment().build()))
                .stub();

        final ServiceException exception = assertThrows(
                ServiceException.class,
                () -> service.updateFulfillmentLineQuantities(orderId, fulfillmentId, lines)
        );
        assertTrue(exception.getMessage().matches(String.format(LINE_DOES_NOT_BELONG_TO_FULFILLMENT, "[0-9]+", fulfillmentId)));
    }

    @Test
    public void updateFulfillmentLineQuantities_OrderIsNotProcessable_ShouldThrowServiceException() {
        final Long orderId = 1L;
        final Long fulfillmentId = 1L;
        final List<Line> lines = hpLines();

        final Order order = orderWithIdAndStatus(orderId, OrderStatus.CREATED);
        final OrderFulfillment fulfillment = fulfillmentWithIdForOrder(fulfillmentId, order);
        stubber.updateFulfillmentLineQuantities()
                .order(order)
                .fulfillment(fulfillment)
                .fulfillmentLinesToUpdate(fulfillmentLinesWithIdsForFulfillment(lines, fulfillment))
                .stub();

        final ServiceException exception = assertThrows(
                ServiceException.class,
                () -> service.updateFulfillmentLineQuantities(orderId, fulfillmentId, lines)
        );
        assertTrue(exception.getMessage().matches(String.format(ORDER_NOT_PROCESSABLE, orderId, "[A-Z]+")));
    }

    @Test
    public void updateFulfillmentLineQuantities_FulfillmentIsAlreadyCompleted_ShouldThrowServiceException() {
        final Long orderId = 1L;
        final Long fulfillmentId = 1L;
        final List<Line> lines = hpLines();

        final Order order = orderWithIdAndStatus(orderId, OrderStatus.PROCESSING);
        final OrderFulfillment fulfillment = orderFulfillment()
                .id(fulfillmentId)
                .order(order)
                .completed(true)
                .build();
        stubber.updateFulfillmentLineQuantities()
                .order(order)
                .fulfillment(fulfillment)
                .fulfillmentLinesToUpdate(fulfillmentLinesWithIdsForFulfillment(lines, fulfillment))
                .stub();

        final ServiceException exception = assertThrows(
                ServiceException.class,
                () -> service.updateFulfillmentLineQuantities(orderId, fulfillmentId, lines)
        );
        assertTrue(exception.getMessage().matches(String.format(FULFILLMENT_ALREADY_COMPLETED, fulfillmentId)));
    }

    @Test
    public void updateFulfillmentLineQuantities_AnyLineQuantityIsGreaterThanAssignableQuantity_ShouldThrowServiceException() {
        final Long orderId = 1L;
        final Long fulfillmentId = 1L;
        final List<Line> lines = hpLines();

        final Order order = orderWithIdAndStatus(orderId, OrderStatus.PROCESSING);
        final OrderFulfillment fulfillment = fulfillmentWithIdForOrder(fulfillmentId, order);
        stubber.updateFulfillmentLineQuantities()
                .order(order)
                .fulfillment(fulfillment)
                .fulfillmentLinesToUpdate(transformFirst(fulfillmentLinesWithIdsForFulfillment(lines, fulfillment), l -> setAlreadyFulfilled(l.getOrderLine())))
                .stub();

        final ServiceException exception = assertThrows(
                ServiceException.class,
                () -> service.updateFulfillmentLineQuantities(orderId, fulfillmentId, lines)
        );
        assertTrue(exception.getMessage().matches(String.format(INSUFFICIENT_ORDER_LINE_ASSIGNABLE_QUANTITY, "[0-9]+", "[0-9]+")));
    }

    @Test
    public void updateFulfillmentLineQuantities_HappyPath_ShouldReturnUpdatedOrderLinesAndFulfillmentLines() {
        final Long orderId = 1L;
        final Long fulfillmentId = 1L;
        final List<Line> lines = hpLines();

        final Order order = orderWithIdAndStatus(orderId, OrderStatus.PROCESSING);
        final OrderFulfillment fulfillment = fulfillmentWithIdForOrder(fulfillmentId, order);
        stubber.updateFulfillmentLineQuantities()
                .order(order)
                .fulfillment(fulfillment)
                .fulfillmentLinesToUpdate(fulfillmentLinesWithIdsForFulfillment(lines, fulfillment))
                .stub();

        final OrderDTO orderDTO = service.updateFulfillmentLineQuantities(orderId, fulfillmentId, lines);

        assertNotNull(orderDTO);
        assertEquals(lines.size(), orderDTO.getUpdatedLines().size());
        assertEquals(lines.size(), orderDTO.getFulfillment().getUpdatedLines().size());
    }

    @Test
    public void deleteFulfillmentLines_NullOrderId_ShouldThrowInvalidInputException() {
        final Long fulfillmentId = 1L;
        final Set<Long> lineIds = hpLineIds();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.deleteFulfillmentLines(null, fulfillmentId, lineIds)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("order id must not be null"))
        );
    }

    @Test
    public void deleteFulfillmentLines_NullFulfillmentId_ShouldThrowInvalidInputException() {
        final Long orderId = 1L;
        final Set<Long> lineIds = hpLineIds();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.deleteFulfillmentLines(orderId, null, lineIds)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("fulfillment id must not be null"))
        );
    }

    @Test
    public void deleteFulfillmentLines_NullLines_ShouldThrowInvalidInputException() {
        final Long orderId = 1L;
        final Long fulfillmentId = 1L;

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.deleteFulfillmentLines(orderId, fulfillmentId, null)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("line ids must not be empty"))
        );
    }

    @Test
    public void deleteFulfillmentLines_AnyLineIdIsNull_ShouldThrowInvalidInputException() {
        final Long orderId = 1L;
        final Long fulfillmentId = 1L;
        final Set<Long> lineIds = hpLineIds();

        lineIds.add(null);

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.deleteFulfillmentLines(orderId, fulfillmentId, lineIds)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("line ids must not contain null elements"))
        );
    }

    @Test
    public void deleteFulfillmentLines_OrderDoesNotExist_ShouldThrowResourceNotFoundException() {
        final Long orderId = 1L;
        final Long fulfillmentId = 1L;
        final Set<Long> lineIds = hpLineIds();

        stubber.deleteFulfillmentLines()
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.deleteFulfillmentLines(orderId, fulfillmentId, lineIds)
        );
        assertTrue(exception.getMessage().contains(String.format(RESOURCE_NOT_FOUND, "order", orderId)));
    }

    @Test
    public void deleteFulfillmentLines_FulfillmentDoesNotExist_ShouldThrowResourceNotFoundException() {
        final Long orderId = 1L;
        final Long fulfillmentId = 1L;
        final Set<Long> lineIds = hpLineIds();

        stubber.deleteFulfillmentLines()
                .order(orderWithIdAndStatus(orderId, OrderStatus.PROCESSING))
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.deleteFulfillmentLines(orderId, fulfillmentId, lineIds)
        );
        assertTrue(exception.getMessage().contains(String.format(RESOURCE_NOT_FOUND, "order fulfillment", fulfillmentId)));
    }

    @Test
    public void deleteFulfillmentLines_OrderIsNotProcessable_ShouldThrowServiceException() {
        final Long orderId = 1L;
        final Long fulfillmentId = 1L;
        final Set<Long> lineIds = hpLineIds();

        final Order order = orderWithIdAndStatus(orderId, OrderStatus.FULFILLED);
        final OrderFulfillment fulfillment = fulfillmentWithIdForOrder(fulfillmentId, order);
        stubber.deleteFulfillmentLines()
                .order(order)
                .fulfillment(fulfillment)
                .fulfillmentLinesToDelete(fulfillmentLinesWithIdsForFulfillment(lineIds, fulfillment))
                .stub();

        final ServiceException exception = assertThrows(
                ServiceException.class,
                () -> service.deleteFulfillmentLines(orderId, fulfillmentId, lineIds)
        );
        assertTrue(exception.getMessage().matches(String.format(ORDER_NOT_PROCESSABLE, orderId, "[A-Z]+")));
    }

    @Test
    public void deleteFulfillmentLines_FulfillmentIsAlreadyCompleted_ShouldThrowServiceException() {
        final Long orderId = 1L;
        final Long fulfillmentId = 1L;
        final Set<Long> lineIds = hpLineIds();

        final Order order = orderWithIdAndStatus(orderId, OrderStatus.PROCESSING);
        final OrderFulfillment fulfillment = orderFulfillment()
                .id(fulfillmentId)
                .order(order)
                .completed(true)
                .build();
        stubber.deleteFulfillmentLines()
                .order(order)
                .fulfillment(fulfillment)
                .fulfillmentLinesToDelete(fulfillmentLinesWithIdsForFulfillment(lineIds, fulfillment))
                .stub();

        final ServiceException exception = assertThrows(
                ServiceException.class,
                () -> service.deleteFulfillmentLines(orderId, fulfillmentId, lineIds)
        );
        assertTrue(exception.getMessage().matches(String.format(FULFILLMENT_ALREADY_COMPLETED, fulfillmentId)));
    }

    @Test
    public void deleteFulfillmentLines_HappyPath_ShouldReturnUpdatedOrderLines() {
        final Long orderId = 1L;
        final Long fulfillmentId = 1L;
        final Set<Long> lineIds = hpLineIds();

        final Order order = orderWithIdAndStatus(orderId, OrderStatus.PROCESSING);
        final OrderFulfillment fulfillment = fulfillmentWithIdForOrder(fulfillmentId, order);
        stubber.deleteFulfillmentLines()
                .order(order)
                .fulfillment(fulfillment)
                .fulfillmentLinesToDelete(fulfillmentLinesWithIdsForFulfillment(lineIds, fulfillment))
                .stub();

        final OrderDTO orderDTO = service.deleteFulfillmentLines(orderId, fulfillmentId, lineIds);

        assertNotNull(orderDTO);
        assertEquals(lineIds.size(), orderDTO.getUpdatedLines().size());
    }

    @Test
    public void deleteFulfillmentLines_AnyFulfillmentLineDoesNotExist_ShouldIgnoreAndSkip() {
        final Long orderId = 1L;
        final Long fulfillmentId = 1L;
        final Set<Long> lineIds = hpLineIds();

        final Order order = orderWithIdAndStatus(orderId, OrderStatus.PROCESSING);
        final OrderFulfillment fulfillment = fulfillmentWithIdForOrder(fulfillmentId, order);
        stubber.deleteFulfillmentLines()
                .order(order)
                .fulfillment(fulfillment)
                .fulfillmentLinesToDelete(removeFirst(fulfillmentLinesWithIdsForFulfillment(lineIds, fulfillment)))
                .stub();

        final OrderDTO orderDTO = service.deleteFulfillmentLines(orderId, fulfillmentId, lineIds);

        assertNotNull(orderDTO);
        assertEquals(lineIds.size() - 1, orderDTO.getUpdatedLines().size());
    }

    @Test
    public void deleteFulfillmentLines_AnyFulfillmentLineDoesNotBelongToFulfillment_ShouldIgnoreAndSkip() {
        final Long orderId = 1L;
        final Long fulfillmentId = 1L;
        final Set<Long> lineIds = hpLineIds();

        final Order order = orderWithIdAndStatus(orderId, OrderStatus.PROCESSING);
        final OrderFulfillment fulfillment = fulfillmentWithIdForOrder(fulfillmentId, order);
        stubber.deleteFulfillmentLines()
                .order(order)
                .fulfillment(fulfillment)
                .fulfillmentLinesToDelete(transformFirst(fulfillmentLinesWithIdsForFulfillment(lineIds, fulfillment), this::setToDifferentFulfillment))
                .stub();

        final OrderDTO orderDTO = service.deleteFulfillmentLines(orderId, fulfillmentId, lineIds);

        assertNotNull(orderDTO);
        assertEquals(lineIds.size() - 1, orderDTO.getUpdatedLines().size());
    }

    @Test
    public void completeFulfillment_NullOrderId_ShouldThrowInvalidInputException() {
        final Long fulfillmentId = 1L;
        final CompleteOrderFulfillmentForm form = hpCompleteOrderFulfillmentForm().build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.completeFulfillment(null, fulfillmentId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("order id must not be null"))
        );
    }

    @Test
    public void completeFulfillment_NullFulfillmentId_ShouldThrowInvalidInputException() {
        final Long orderId = 1L;
        final CompleteOrderFulfillmentForm form = hpCompleteOrderFulfillmentForm().build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.completeFulfillment(orderId, null, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("fulfillment id must not be null"))
        );
    }

    @Test
    public void completeFulfillment_NullForm_ShouldThrowInvalidInputException() {
        final Long orderId = 1L;
        final Long fulfillmentId = 1L;

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.completeFulfillment(orderId, fulfillmentId, null)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("form must not be null"))
        );
    }

    @Test
    public void completeFulfillment_BlankTrackingNumber_ShouldThrowInvalidInputException() {
        final Long orderId = 1L;
        final Long fulfillmentId = 1L;
        final CompleteOrderFulfillmentForm form = hpCompleteOrderFulfillmentForm()
                .trackingNumber("   ")
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.completeFulfillment(orderId, fulfillmentId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("tracking number must not be blank"))
        );
    }

    @Test
    public void completeFulfillment_OrderDoesNotExist_ShouldThrowResourceNotFoundException() {
        final Long orderId = 1L;
        final Long fulfillmentId = 1L;
        final CompleteOrderFulfillmentForm form = hpCompleteOrderFulfillmentForm().build();

        stubber.completeFulfillment()
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.completeFulfillment(orderId, fulfillmentId, form)
        );
        assertTrue(exception.getMessage().contains(String.format(RESOURCE_NOT_FOUND, "order", orderId)));
    }

    @Test
    public void completeFulfillment_FulfillmentDoesNotExist_ShouldThrowResourceNotFoundException() {
        final Long orderId = 1L;
        final Long fulfillmentId = 1L;
        final CompleteOrderFulfillmentForm form = hpCompleteOrderFulfillmentForm().build();

        final Order order = orderWithIdAndStatus(orderId, OrderStatus.PARTIALLY_FULFILLED);
        stubber.completeFulfillment()
                .order(order)
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.completeFulfillment(orderId, fulfillmentId, form)
        );
        assertTrue(exception.getMessage().contains(String.format(RESOURCE_NOT_FOUND, "order fulfillment", fulfillmentId)));
    }

    @Test
    public void completeFulfillment_FulfillmentDoesNotBelongToOrder_ShouldThrowServiceException() {
        final Long orderId = 1L;
        final Long fulfillmentId = 1L;
        final CompleteOrderFulfillmentForm form = hpCompleteOrderFulfillmentForm().build();

        final Order order = orderWithIdAndStatus(orderId, OrderStatus.PARTIALLY_FULFILLED);
        stubber.completeFulfillment()
                .order(order)
                .fulfillmentToComplete(fulfillmentWithIdForOrder(fulfillmentId, order().build()))
                .stub();

        final ServiceException exception = assertThrows(
                ServiceException.class,
                () -> service.completeFulfillment(orderId, fulfillmentId, form)
        );
        assertTrue(exception.getMessage().matches(String.format(FULFILLMENT_DOES_NOT_BELONG_TO_ORDER, fulfillmentId, orderId)));
    }

    @Test
    public void completeFulfillment_OrderIsNotProcessable_ShouldThrowServiceException() {
        final Long orderId = 1L;
        final Long fulfillmentId = 1L;
        final CompleteOrderFulfillmentForm form = hpCompleteOrderFulfillmentForm().build();

        final Order order = orderWithIdAndStatus(orderId, OrderStatus.CREATED);
        stubber.completeFulfillment()
                .order(order)
                .fulfillmentToComplete(fulfillmentWithIdForOrder(fulfillmentId, order))
                .stub();

        final ServiceException exception = assertThrows(
                ServiceException.class,
                () -> service.completeFulfillment(orderId, fulfillmentId, form)
        );
        assertTrue(exception.getMessage().matches(String.format(ORDER_NOT_PROCESSABLE, orderId, "[A-Z]+")));
    }

    @Test
    public void completeFulfillment_FulfillmentIsAlreadyCompleted_ShouldThrowServiceException() {
        final Long orderId = 1L;
        final Long fulfillmentId = 1L;
        final CompleteOrderFulfillmentForm form = hpCompleteOrderFulfillmentForm().build();

        final Order order = orderWithIdAndStatus(orderId, OrderStatus.PARTIALLY_FULFILLED);
        stubber.completeFulfillment()
                .order(order)
                .fulfillmentToComplete(
                        orderFulfillment()
                                .id(fulfillmentId)
                                .order(order)
                                .completed(true)
                                .build()
                )
                .stub();

        final ServiceException exception = assertThrows(
                ServiceException.class,
                () -> service.completeFulfillment(orderId, fulfillmentId, form)
        );
        assertTrue(exception.getMessage().matches(String.format(FULFILLMENT_ALREADY_COMPLETED, fulfillmentId)));
    }

    @Test
    public void completeFulfillment_HappyPath_ShouldReturnUpdatedFulfillmentAndOrderLines() {
        final Long orderId = 1L;
        final Long fulfillmentId = 1L;
        final CompleteOrderFulfillmentForm form = hpCompleteOrderFulfillmentForm().build();

        final Order order = orderWithIdAndStatus(orderId, OrderStatus.PARTIALLY_FULFILLED);
        stubber.completeFulfillment()
                .order(order)
                .fulfillmentToComplete(fulfillmentWithIdForOrder(fulfillmentId, order))
                .stub();

        final OrderDTO orderDTO = service.completeFulfillment(orderId, fulfillmentId, form);

        assertNotNull(orderDTO);
        assertNotNull(orderDTO.getUpdatedLines());
        assertNotNull(orderDTO.getFulfillment());
        assertEquals(form.getTrackingNumber(), orderDTO.getFulfillment().getTrackingNumber());
    }

    @Test
    public void completeFulfillment_OrderStatusIsPROCESSING_ShouldTransitionOrderStatusToPARTIALLYFULFILLED() {
        final Long orderId = 1L;
        final Long fulfillmentId = 1L;
        final CompleteOrderFulfillmentForm form = hpCompleteOrderFulfillmentForm().build();

        final Order order = orderWithIdAndStatus(orderId, OrderStatus.PROCESSING);
        stubber.completeFulfillment()
                .order(order)
                .fulfillmentToComplete(fulfillmentWithIdForOrder(fulfillmentId, order))
                .stub();

        final OrderDTO orderDTO = service.completeFulfillment(orderId, fulfillmentId, form);

        assertEquals(OrderStatus.PARTIALLY_FULFILLED, orderDTO.getStatus());
    }

    @Test
    public void updateTrackingNumber_NullOrderId_ShouldThrowInvalidInputException() {
        final Long fulfillmentId = 1L;
        final UpdateOrderFulfillmentTrackingNumberForm form = hpUpdateOrderFulfillmentTrackingNumberForm().build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateTrackingNumber(null, fulfillmentId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("order id must not be null"))
        );
    }

    @Test
    public void updateTrackingNumber_NullFulfillmentId_ShouldThrowInvalidInputException() {
        final Long orderId = 1L;
        final UpdateOrderFulfillmentTrackingNumberForm form = hpUpdateOrderFulfillmentTrackingNumberForm().build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateTrackingNumber(orderId, null, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("fulfillment id must not be null"))
        );
    }

    @Test
    public void updateTrackingNumber_NullForm_ShouldThrowInvalidInputException() {
        final Long orderId = 1L;
        final Long fulfillmentId = 1L;

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateTrackingNumber(orderId, fulfillmentId, null)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("form must not be null"))
        );
    }

    @Test
    public void updateTrackingNumber_NullTrackingNumber_ShouldThrowInvalidInputException() {
        final Long orderId = 1L;
        final Long fulfillmentId = 1L;
        final UpdateOrderFulfillmentTrackingNumberForm form = hpUpdateOrderFulfillmentTrackingNumberForm()
                .trackingNumber(null)
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateTrackingNumber(orderId, fulfillmentId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("tracking number must not be blank"))
        );
    }

    @Test
    public void updateTrackingNumber_BlankTrackingNumber_ShouldThrowInvalidInputException() {
        final Long orderId = 1L;
        final Long fulfillmentId = 1L;
        final UpdateOrderFulfillmentTrackingNumberForm form = hpUpdateOrderFulfillmentTrackingNumberForm()
                .trackingNumber("   ")
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateTrackingNumber(orderId, fulfillmentId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("tracking number must not be blank"))
        );
    }

    @Test
    public void updateTrackingNumber_OrderDoesNotExist_ShouldThrowResourceNotFoundException() {
        final Long orderId = 1L;
        final Long fulfillmentId = 1L;
        final UpdateOrderFulfillmentTrackingNumberForm form = hpUpdateOrderFulfillmentTrackingNumberForm().build();

        stubber.completeFulfillment()
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.updateTrackingNumber(orderId, fulfillmentId, form)
        );
        assertTrue(exception.getMessage().contains(String.format(RESOURCE_NOT_FOUND, "order", orderId)));
    }

    @Test
    public void updateTrackingNumber_FulfillmentDoesNotExist_ShouldThrowResourceNotFoundException() {
        final Long orderId = 1L;
        final Long fulfillmentId = 1L;
        final UpdateOrderFulfillmentTrackingNumberForm form = hpUpdateOrderFulfillmentTrackingNumberForm().build();

        stubber.completeFulfillment()
                .order(orderWithIdAndStatus(orderId, OrderStatus.FULFILLED))
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.updateTrackingNumber(orderId, fulfillmentId, form)
        );
        assertTrue(exception.getMessage().contains(String.format(RESOURCE_NOT_FOUND, "order fulfillment", fulfillmentId)));
    }

    @Test
    public void updateTrackingNumber_FulfillmentDoesNotBelongToOrder_ShouldThrowServiceException() {
        final Long orderId = 1L;
        final Long fulfillmentId = 1L;
        final UpdateOrderFulfillmentTrackingNumberForm form = hpUpdateOrderFulfillmentTrackingNumberForm().build();

        final Order order = orderWithIdAndStatus(orderId, OrderStatus.FULFILLED);
        stubber.completeFulfillment()
                .order(order)
                .fulfillmentToComplete(fulfillmentWithIdForOrder(fulfillmentId, order().build()))
                .stub();

        final ServiceException exception = assertThrows(
                ServiceException.class,
                () -> service.updateTrackingNumber(orderId, fulfillmentId, form)
        );
        assertTrue(exception.getMessage().matches(String.format(FULFILLMENT_DOES_NOT_BELONG_TO_ORDER, fulfillmentId, orderId)));
    }

    @Test
    public void updateTrackingNumber_OrderStatusIsCANCELLED_ShouldThrowServiceException() {
        final Long orderId = 1L;
        final Long fulfillmentId = 1L;
        final UpdateOrderFulfillmentTrackingNumberForm form = hpUpdateOrderFulfillmentTrackingNumberForm().build();

        final Order order = orderWithIdAndStatus(orderId, OrderStatus.CANCELLED);
        stubber.completeFulfillment()
                .order(order)
                .fulfillmentToComplete(
                        orderFulfillment()
                                .id(orderId)
                                .order(order)
                                .completed(true)
                                .build()
                )
                .stub();

        final ServiceException exception = assertThrows(
                ServiceException.class,
                () -> service.updateTrackingNumber(orderId, fulfillmentId, form)
        );
        assertTrue(exception.getMessage().matches(String.format(ORDER_ALREADY_CANCELLED, orderId)));
    }

    @Test
    public void updateTrackingNumber_FulfillmentIsNotCompleted_ShouldThrowServiceException() {
        final Long orderId = 1L;
        final Long fulfillmentId = 1L;
        final UpdateOrderFulfillmentTrackingNumberForm form = hpUpdateOrderFulfillmentTrackingNumberForm().build();

        final Order order = orderWithIdAndStatus(orderId, OrderStatus.FULFILLED);
        stubber.completeFulfillment()
                .order(order)
                .fulfillmentToComplete(
                        orderFulfillment()
                                .id(orderId)
                                .order(order)
                                .completed(false)
                                .build()
                )
                .stub();

        final ServiceException exception = assertThrows(
                ServiceException.class,
                () -> service.updateTrackingNumber(orderId, fulfillmentId, form)
        );
        assertTrue(exception.getMessage().matches(String.format(FULFILLMENT_NOT_COMPLETED, fulfillmentId)));
    }

    @Test
    public void updateTrackingNumber_HappyPath_ShouldReturnUpdatedFulfillment() {
        final Long orderId = 1L;
        final Long fulfillmentId = 1L;
        final UpdateOrderFulfillmentTrackingNumberForm form = hpUpdateOrderFulfillmentTrackingNumberForm().build();

        final Order order = orderWithIdAndStatus(orderId, OrderStatus.FULFILLED);
        stubber.completeFulfillment()
                .order(order)
                .fulfillmentToComplete(
                        orderFulfillment()
                                .id(orderId)
                                .order(order)
                                .completed(true)
                                .build()
                )
                .stub();

        final OrderFulfillmentDTO fulfillmentDTO = service.updateTrackingNumber(orderId, fulfillmentId, form);

        assertNotNull(fulfillmentDTO);
        assertEquals(form.getTrackingNumber(), fulfillmentDTO.getTrackingNumber());
    }

    @Test
    public void deleteFulfillment_NullOrderId_ShouldThrowInvalidInputException() {
        final Long fulfillmentId = 1L;

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.deleteFulfillment(null, fulfillmentId)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("order id must not be null"))
        );
    }

    @Test
    public void deleteFulfillment_NullFulfillmentId_ShouldThrowInvalidInputException() {
        final Long orderId = 1L;

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.deleteFulfillment(orderId, null)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("fulfillment id must not be null"))
        );
    }

    @Test
    public void deleteFulfillment_OrderDoesNotExist_ShouldThrowResourceNotFoundException() {
        final Long orderId = 1L;
        final Long fulfillmentId = 1L;

        stubber.deleteFulfillment()
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.deleteFulfillment(orderId, fulfillmentId)
        );
        assertTrue(exception.getMessage().contains(String.format(RESOURCE_NOT_FOUND, "order", orderId)));
    }

    @Test
    public void deleteFulfillment_FulfillmentDoesNotExist_ShouldThrowResourceNotFoundException() {
        final Long orderId = 1L;
        final Long fulfillmentId = 1L;

        stubber.deleteFulfillment()
                .order(orderWithIdAndStatus(orderId, OrderStatus.PROCESSING))
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.deleteFulfillment(orderId, fulfillmentId)
        );
        assertTrue(exception.getMessage().contains(String.format(RESOURCE_NOT_FOUND, "order fulfillment", fulfillmentId)));
    }

    @Test
    public void deleteFulfillment_FulfillmentDoesNotBelongToOrder_ShouldThrowServiceException() {
        final Long orderId = 1L;
        final Long fulfillmentId = 1L;

        final Order order = orderWithIdAndStatus(orderId, OrderStatus.PROCESSING);
        stubber.deleteFulfillment()
                .order(order)
                .fulfillmentToDelete(
                        orderFulfillment()
                                .id(fulfillmentId)
                                .order(order().build())
                                .build()
                )
                .stub();

        final ServiceException exception = assertThrows(
                ServiceException.class,
                () -> service.deleteFulfillment(orderId, fulfillmentId)
        );
        assertTrue(exception.getMessage().matches(String.format(FULFILLMENT_DOES_NOT_BELONG_TO_ORDER, fulfillmentId, orderId)));
    }

    @Test
    public void deleteFulfillment_OrderIsNotProcessable_ShouldThrowServiceException() {
        final Long orderId = 1L;
        final Long fulfillmentId = 1L;

        final Order order = orderWithIdAndStatus(orderId, OrderStatus.FULFILLED);
        stubber.deleteFulfillment()
                .order(order)
                .fulfillmentToDelete(fulfillmentWithIdForOrder(fulfillmentId, order))
                .stub();

        final ServiceException exception = assertThrows(
                ServiceException.class,
                () -> service.deleteFulfillment(orderId, fulfillmentId)
        );
        assertTrue(exception.getMessage().matches(String.format(ORDER_NOT_PROCESSABLE, orderId, "[A-Z]+")));
    }

    @Test
    public void deleteFulfillment_HappyPath_ShouldReturnUpdatedOrderLines() {
        final Long orderId = 1L;
        final Long fulfillmentId = 1L;

        final Order order = orderWithIdAndStatus(orderId, OrderStatus.PROCESSING);
        stubber.deleteFulfillment()
                .order(order)
                .fulfillmentToDelete(fulfillmentWithIdForOrder(fulfillmentId, order))
                .stub();

        final OrderDTO orderDTO = service.deleteFulfillment(orderId, fulfillmentId);

        assertNotNull(orderDTO);
        assertNotNull(orderDTO.getUpdatedLines());
    }

    @Test
    public void deleteFulfillment_FulfillmentIsAlreadyCompleted_ShouldReturnUpdatedOrder() {
        final Long orderId = 1L;
        final Long fulfillmentId = 1L;

        final Order order = orderWithIdAndStatus(orderId, OrderStatus.PROCESSING);
        stubber.deleteFulfillment()
                .order(order)
                .fulfillmentToDelete(
                        orderFulfillment()
                                .id(fulfillmentId)
                                .order(order)
                                .completed(true)
                                .build()
                )
                .stub();

        final OrderDTO orderDTO = service.deleteFulfillment(orderId, fulfillmentId);

        assertNotNull(orderDTO);
    }

    @Test
    public void fulfill_NullOrderId_ShouldThrowInvalidInputException() {
        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.fulfill(null)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("order id must not be null"))
        );
    }

    @Test
    public void fulfill_OrderDoesNotExist_ShouldThrowResourceNotFoundException() {
        final Long orderId = 1L;

        stubber.fulfill()
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.fulfill(orderId)
        );
        assertTrue(exception.getMessage().contains(String.format(RESOURCE_NOT_FOUND, "order", orderId)));
    }

    @Test
    public void fulfill_OrderIsNotProcessable_ShouldThrowServiceException() {
        final Long orderId = 1L;

        stubber.fulfill()
                .orderToFulfill(orderWithIdAndStatus(orderId, OrderStatus.FULFILLED))
                .stub();

        final ServiceException exception = assertThrows(
                ServiceException.class,
                () -> service.fulfill(orderId)
        );
        assertTrue(exception.getMessage().matches(String.format(ORDER_NOT_PROCESSABLE, orderId, "[A-Z]+")));
    }

    @Test
    public void fulfill_AnyLineIsNotFulfilled_ShouldThrowServiceException() {
        final Long orderId = 1L;

        final Order order = orderWithIdAndStatus(orderId, OrderStatus.PROCESSING);
        transformFirst(addFulfilledLines(order), this::setUnfulfilled);
        stubber.fulfill()
                .orderToFulfill(order)
                .stub();

        final ServiceException exception = assertThrows(
                ServiceException.class,
                () -> service.fulfill(orderId)
        );
        assertTrue(exception.getMessage().matches(String.format(CANNOT_FULFILL_ORDER_WITH_UNFULFILLED_LINES, orderId)));
    }

    @Test
    public void fulfill_HappyPath_ShouldReturnFulfilledOrder() {
        final Long orderId = 1L;

        final Order order = orderWithIdAndStatus(orderId, OrderStatus.PROCESSING);
        addFulfilledLines(order);
        stubber.fulfill()
                .orderToFulfill(order)
                .stub();

        final OrderDTO orderDTO = service.fulfill(orderId);

        assertNotNull(orderDTO);
        assertEquals(OrderStatus.FULFILLED, orderDTO.getStatus());
    }

    @Test
    public void fulfill_UnfulfilledLineButDoesNotRequireShipping_ShouldIgnoreAndNotThrow() {
        final Long orderId = 1L;

        final Order order = orderWithIdAndStatus(orderId, OrderStatus.PROCESSING);
        transformFirst(addFulfilledLines(order), firstLine -> {
            setUnfulfilled(firstLine);
            setNoShippingRequired(firstLine);
        });
        stubber.fulfill()
                .orderToFulfill(order)
                .stub();

        assertDoesNotThrow(() -> service.fulfill(orderId));
    }

    @Test
    public void undoFulfill_NullOrderId_ShouldThrowServiceException() {
        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.undoFulfill(null)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("order id must not be null"))
        );
    }

    @Test
    public void undoFulfill_OrderDoesNotExist_ShouldThrowResourceNotFoundException() {
        final Long orderId = 1L;

        stubber.undoFulfill()
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.undoFulfill(orderId)
        );
        assertTrue(exception.getMessage().contains(String.format(RESOURCE_NOT_FOUND, "order", orderId)));
    }

    @Test
    public void undoFulfill_OrderStatusIsNotFULFILLED_ShouldThrowServiceException() {
        final Long orderId = 1L;

        final Order order = order()
                .id(orderId)
                .status(OrderStatus.PARTIALLY_FULFILLED)
                .previousStatus(OrderStatus.PROCESSING)
                .build();
        stubber.undoFulfill()
                .orderToUnfulfill(order)
                .stub();

        final ServiceException exception = assertThrows(
                ServiceException.class,
                () -> service.undoFulfill(orderId)
        );
        assertTrue(exception.getMessage().matches(String.format(ORDER_NOT_FULFILLED, orderId)));
    }

    @Test
    public void undoFulfill_HappyPath_ShouldReturnOrderInPreviousStatus() {
        final Long orderId = 1L;

        final Order order = order()
                .id(orderId)
                .status(OrderStatus.FULFILLED)
                .previousStatus(OrderStatus.PARTIALLY_FULFILLED)
                .build();
        stubber.undoFulfill()
                .orderToUnfulfill(order)
                .stub();

        final OrderDTO orderDTO = service.undoFulfill(orderId);

        assertNotEquals(OrderStatus.FULFILLED, orderDTO.getStatus());
    }

    @Test
    public void cancel_NullOrderId_ShouldThrowInvalidInputException() {
        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.cancel(null)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("order id must not be null"))
        );
    }

    @Test
    public void cancel_OrderDoesNotExist_ShouldThrowResourceNotFoundException() {
        final Long orderId = 1L;

        stubber.cancel()
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.cancel(orderId)
        );
        assertTrue(exception.getMessage().contains(String.format(RESOURCE_NOT_FOUND, "order", orderId)));
    }

    @Test
    public void cancel_OrderStatusIsAlreadyCANCELLED_ShouldThrowServiceException() {
        final Long orderId = 1L;

        stubber.cancel()
                .orderToCancel(orderWithIdAndStatus(orderId, OrderStatus.CANCELLED))
                .stub();

        final ServiceException exception = assertThrows(
                ServiceException.class,
                () -> service.cancel(orderId)
        );
        assertTrue(exception.getMessage().matches(String.format(ORDER_ALREADY_CANCELLED, orderId)));
    }

    @Test
    public void cancel_HappyPath_ShouldReturnCancelledOrder() {
        final Long orderId = 1L;

        stubber.cancel()
                .orderToCancel(orderWithIdAndStatus(orderId, OrderStatus.FULFILLED))
                .stub();

        final OrderDTO orderDTO = service.cancel(orderId);

        assertEquals(OrderStatus.CANCELLED, orderDTO.getStatus());
    }

    @Test
    public void reinstate_NullOrderId_ShouldThrowServiceException() {
        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.reinstate(null)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("order id must not be null"))
        );
    }

    @Test
    public void reinstate_OrderDoesNotExist_ShouldThrowResourceNotFoundException() {
        final Long orderId = 1L;

        stubber.reinstate()
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.reinstate(orderId)
        );
        assertTrue(exception.getMessage().contains(String.format(RESOURCE_NOT_FOUND, "order", orderId)));
    }

    @Test
    public void reinstate_OrderStatusIsNotCANCELLED_ShouldThrowServiceException() {
        final Long orderId = 1L;

        final Order order = order()
                .id(orderId)
                .status(OrderStatus.FULFILLED)
                .previousStatus(OrderStatus.PARTIALLY_FULFILLED)
                .build();
        stubber.reinstate()
                .orderToReinstate(order)
                .stub();

        final ServiceException exception = assertThrows(
                ServiceException.class,
                () -> service.reinstate(orderId)
        );
        assertTrue(exception.getMessage().matches(String.format(ORDER_NOT_CANCELLED, orderId)));
    }

    @Test
    public void reinstate_HappyPath_ShouldReturnOrderInPreviousStatus() {
        final Long orderId = 1L;

        final Order order = order()
                .id(orderId)
                .status(OrderStatus.CANCELLED)
                .previousStatus(OrderStatus.PARTIALLY_FULFILLED)
                .build();
        stubber.reinstate()
                .orderToReinstate(order)
                .stub();

        final OrderDTO orderDTO = service.reinstate(orderId);

        assertNotEquals(OrderStatus.CANCELLED, orderDTO.getStatus());
    }

    private void setToDifferentOrder(final OrderLine line) {
        setField(line, "order", order().build());
    }

    private void setNoShippingRequired(final OrderLine line) {
        setField(line, "shippingRequired", false);
    }

    private void setAlreadyFulfilled(final OrderLine line) {
        setField(line, "fulfilledQuantity", line.getQuantity());
    }

    private void setToDifferentFulfillment(final OrderFulfillmentLine fulfillmentLine) {
        setField(fulfillmentLine, "fulfillment", orderFulfillment().build());
    }

    private void setUnfulfilled(final OrderLine line) {
        setField(line, "fulfilledQuantity", line.getQuantity() - 1);
    }

    private static Order orderWithIdAndStatus(final Long id, final OrderStatus status) {
        return order()
                .id(id)
                .status(status)
                .build();
    }

    private static OrderFulfillment fulfillmentWithIdForOrder(final Long id, final Order order) {
        return orderFulfillment()
                .id(id)
                .order(order)
                .completed(false)
                .build();
    }

    private static List<OrderLine> addFulfilledLines(final Order order) {
        return List.of(
                fulfilledOrderLineForOrder(order),
                fulfilledOrderLineForOrder(order),
                fulfilledOrderLineForOrder(order)
        );
    }

    private static OrderLine fulfilledOrderLineForOrder(final Order order) {
        return orderLine()
                .order(order)
                .shippingRequired(true)
                .quantity(100)
                .fulfilledQuantity(100)
                .build();
    }

    private static OrderLine orderLineWithIdForOrder(final Long id, final Order order) {
        return orderLine()
                .id(id)
                .order(order)
                .shippingRequired(true)
                .quantity(100)
                .build();
    }

    private static List<OrderLine> orderLinesWithIdsForOrder(
            final List<Line> lines,
            final Order order
    ) {
        return nullable(lines)
                .map(line -> orderLineWithIdForOrder(line.getId(), order))
                .collect(Collectors.toList());
    }

    private static OrderFulfillmentLine fulfillmentLineWithIdForFulfillment(final Long id, final OrderFulfillment fulfillment) {
        return orderFulfillmentLine()
                .id(id)
                .orderLine(orderLineWithIdForOrder(null, fulfillment.getOrder()))
                .fulfillment(fulfillment)
                .quantity(1)
                .build();
    }

    private static List<OrderFulfillmentLine> fulfillmentLinesWithIdsForFulfillment(final List<Line> lines, final OrderFulfillment fulfillment) {
        return nullable(lines)
                .map(line -> fulfillmentLineWithIdForFulfillment(line.getId(), fulfillment))
                .collect(Collectors.toList());
    }

    private static List<OrderFulfillmentLine> fulfillmentLinesWithIdsForFulfillment(final Set<Long> ids, final OrderFulfillment fulfillment) {
        return nullable(ids)
                .map(id -> fulfillmentLineWithIdForFulfillment(id, fulfillment))
                .collect(Collectors.toList());
    }

    private static List<Line> hpLines() {
        final List<Line> lines = List.of(
                Line.builder().id(1L).quantity(3).build(),
                Line.builder().id(2L).quantity(2).build(),
                Line.builder().id(3L).quantity(4).build()
        );
        return new ArrayList<>(lines);
    }

    private static Set<Long> hpLineIds() {
        final Set<Long> lineIds = Set.of(1L, 2L, 3L);
        return new HashSet<>(lineIds);
    }

    private static CompleteOrderFulfillmentForm.CompleteOrderFulfillmentFormBuilder hpCompleteOrderFulfillmentForm() {
        return CompleteOrderFulfillmentForm.builder()
                .trackingNumber("abc123");
    }

    private static UpdateOrderFulfillmentTrackingNumberForm.UpdateOrderFulfillmentTrackingNumberFormBuilder hpUpdateOrderFulfillmentTrackingNumberForm() {
        return UpdateOrderFulfillmentTrackingNumberForm.builder()
                .trackingNumber("abc123");
    }

}
