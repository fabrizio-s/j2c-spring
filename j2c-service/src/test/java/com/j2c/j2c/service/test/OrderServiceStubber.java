package com.j2c.j2c.service.test;

import com.j2c.j2c.domain.entity.*;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestComponent;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.when;

@TestComponent
@RequiredArgsConstructor
public class OrderServiceStubber {

    private final MockBeanProvider mockBeanProvider;

    @Builder(builderClassName = "MockConfirm",
            builderMethodName = "confirm",
            buildMethodName = "stub")
    private void _confirm(final Order orderToConfirm) {
        mockFindOrderById(orderToConfirm);
    }

    @Builder(builderClassName = "MockCreateFulfillment",
            builderMethodName = "createFulfillment",
            buildMethodName = "stub")
    private void _createFulfillment(
            final Order order,
            final List<OrderLine> lines
    ) {
        mockFindOrderById(order);
        mockFindAllLinesById(lines);
    }

    @Builder(builderClassName = "MockAddFulfillmentLines",
            builderMethodName = "addFulfillmentLines",
            buildMethodName = "stub")
    private void _addFulfillmentLines(
            final Order order,
            final List<OrderLine> lines,
            final OrderFulfillment fulfillment
    ) {
        mockFindOrderById(order);
        mockFindAllLinesById(lines);
        mockFindFulfillmentById(fulfillment);
    }

    @Builder(builderClassName = "MockUpdateFulfillmentLineQuantities",
            builderMethodName = "updateFulfillmentLineQuantities",
            buildMethodName = "stub")
    private void _updateFulfillmentLineQuantities(
            final Order order,
            final OrderFulfillment fulfillment,
            final List<OrderFulfillmentLine> fulfillmentLinesToUpdate
    ) {
        mockFindOrderById(order);
        mockFindFulfillmentById(fulfillment);
        mockFindAllFulfillmentLinesById(fulfillmentLinesToUpdate);
    }

    @Builder(builderClassName = "MockDeleteFulfillmentLines",
            builderMethodName = "deleteFulfillmentLines",
            buildMethodName = "stub")
    private void _deleteFulfillmentLines(
            final Order order,
            final OrderFulfillment fulfillment,
            final List<OrderFulfillmentLine> fulfillmentLinesToDelete
    ) {
        mockFindOrderById(order);
        mockFindFulfillmentById(fulfillment);
        mockFindAllFulfillmentLinesById(fulfillmentLinesToDelete);
    }

    @Builder(builderClassName = "MockCompleteFulfillment",
            builderMethodName = "completeFulfillment",
            buildMethodName = "stub")
    private void _completeFulfillment(
            final Order order,
            final OrderFulfillment fulfillmentToComplete
    ) {
        mockFindOrderById(order);
        mockFindFulfillmentById(fulfillmentToComplete);
    }

    @Builder(builderClassName = "MockUpdateTrackingNumber",
            builderMethodName = "updateTrackingNumber",
            buildMethodName = "stub")
    private void _updateTrackingNumber(
            final Order order,
            final OrderFulfillment fulfillmentToUpdate
    ) {
        mockFindOrderById(order);
        mockFindFulfillmentById(fulfillmentToUpdate);
    }

    @Builder(builderClassName = "MockDeleteFulfillment",
            builderMethodName = "deleteFulfillment",
            buildMethodName = "stub")
    private void _deleteFulfillment(
            final Order order,
            final OrderFulfillment fulfillmentToDelete
    ) {
        mockFindOrderById(order);
        mockFindFulfillmentById(fulfillmentToDelete);
    }

    @Builder(builderClassName = "MockFulfill",
            builderMethodName = "fulfill",
            buildMethodName = "stub")
    private void _fulfill(final Order orderToFulfill) {
        mockFindOrderById(orderToFulfill);
    }

    @Builder(builderClassName = "MockUndoFulfill",
            builderMethodName = "undoFulfill",
            buildMethodName = "stub")
    private void _undoFulfill(final Order orderToUnfulfill) {
        mockFindOrderById(orderToUnfulfill);
    }

    @Builder(builderClassName = "MockCancel",
            builderMethodName = "cancel",
            buildMethodName = "stub")
    private void _cancel(final Order orderToCancel) {
        mockFindOrderById(orderToCancel);
    }

    @Builder(builderClassName = "MockReinstate",
            builderMethodName = "reinstate",
            buildMethodName = "stub")
    private void _reinstate(final Order orderToReinstate) {
        mockFindOrderById(orderToReinstate);
    }

    private void mockFindOrderById(final Order order) {
        if (order != null) {
            when(mockBeanProvider.getOrderRepository().findById(order.getId()))
                    .thenReturn(Optional.of(order));
        }
    }

    private void mockFindFulfillmentById(final OrderFulfillment fulfillment) {
        if (fulfillment != null) {
            when(mockBeanProvider.getOrderFulfillmentRepository().findById(fulfillment.getId()))
                    .thenReturn(Optional.of(fulfillment));
        }
    }

    private void mockFindAllLinesById(final List<OrderLine> lines) {
        if (lines != null) {
            when(mockBeanProvider.getOrderLineRepository().findAllById(anySet()))
                    .thenReturn(lines);
        }
    }

    private void mockFindAllFulfillmentLinesById(final List<OrderFulfillmentLine> fulfillmentLines) {
        if (fulfillmentLines != null) {
            when(mockBeanProvider.getOrderFulfillmentLineRepository().findAllById(anySet()))
                    .thenReturn(fulfillmentLines);
        }
    }

}
