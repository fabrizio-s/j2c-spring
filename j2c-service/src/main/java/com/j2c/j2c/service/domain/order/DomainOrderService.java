package com.j2c.j2c.service.domain.order;

import com.j2c.j2c.domain.entity.Order;
import com.j2c.j2c.domain.entity.OrderFulfillment;
import com.j2c.j2c.domain.entity.OrderFulfillmentLine;
import com.j2c.j2c.domain.entity.OrderLine;
import com.j2c.j2c.domain.repository.OrderFulfillmentLineRepository;
import com.j2c.j2c.domain.repository.OrderFulfillmentRepository;
import com.j2c.j2c.domain.repository.OrderLineRepository;
import com.j2c.j2c.domain.repository.OrderRepository;
import com.j2c.j2c.service.input.CompleteOrderFulfillmentForm;
import com.j2c.j2c.service.input.Line;
import com.j2c.j2c.service.input.UpdateOrderFulfillmentTrackingNumberForm;
import com.j2c.j2c.service.util.MergedLines;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

import static com.j2c.j2c.domain.util.J2cUtils.optional;

@Service
@Validated
@Transactional
@RequiredArgsConstructor
public class DomainOrderService {

    private final OrderRepository orderRepository;
    private final OrderLineRepository orderLineRepository;
    private final OrderFulfillmentRepository fulfillmentRepository;
    private final OrderFulfillmentLineRepository fulfillmentLineRepository;

    public Order confirm(@NotNull final Long orderId) {
        final Order order = orderRepository.findById(orderId);

        order.confirm();

        return order;
    }

    public CreateFulfillmentResult createFulfillment(
            @NotNull final Long orderId,
            @NotEmpty final List<@NotNull @Valid Line> lines
    ) {
        final Order order = orderRepository.findById(orderId);

        final OrderFulfillment fulfillment = fulfillmentRepository.save(order.newFulfillment());

        final List<OrderFulfillmentLine> fulfillmentLines = addLines(fulfillment, lines);

        final List<OrderLine> orderLines = getOrderLines(fulfillmentLines);

        return CreateFulfillmentResult.builder()
                .order(order)
                .updatedLines(orderLines)
                .createdFulfillment(fulfillment)
                .createdFulfillmentLines(fulfillmentLines)
                .build();
    }

    public AddFulfillmentLinesResult addFulfillmentLines(
            @NotNull final Long orderId,
            @NotNull final Long fulfillmentId,
            @NotEmpty final List<@NotNull @Valid Line> lines
    ) {
        final Order order = orderRepository.findById(orderId);

        final OrderFulfillment fulfillment = fulfillmentRepository.findById(fulfillmentId)
                .verifyBelongsToOrder(order);

        final List<OrderFulfillmentLine> fulfillmentLines = addLines(fulfillment, lines);

        final List<OrderLine> orderLines = getOrderLines(fulfillmentLines);

        return AddFulfillmentLinesResult.builder()
                .order(order)
                .updatedLines(orderLines)
                .fulfillment(fulfillment)
                .addedFulfillmentLines(fulfillmentLines)
                .build();
    }

    public UpdateFulfillmentLineQuantitiesResult updateFulfillmentLineQuantities(
            @NotNull final Long orderId,
            @NotNull final Long fulfillmentId,
            @NotEmpty final List<@NotNull @Valid Line> lines
    ) {
        final Order order = orderRepository.findById(orderId);

        final OrderFulfillment fulfillment = fulfillmentRepository.findById(fulfillmentId)
                .verifyBelongsToOrder(order);

        final List<OrderFulfillmentLine> fulfillmentLines = setQuantities(fulfillment, lines);

        final List<OrderLine> orderLines = getOrderLines(fulfillmentLines);

        return UpdateFulfillmentLineQuantitiesResult.builder()
                .order(order)
                .updatedLines(orderLines)
                .fulfillment(fulfillment)
                .updatedFulfillmentLines(fulfillmentLines)
                .build();
    }

    public DeleteFulfillmentLinesResult deleteFulfillmentLines(
            @NotNull final Long orderId,
            @NotNull final Long fulfillmentId,
            @NotEmpty final Set<@NotNull Long> lineIds
    ) {
        final Order order = orderRepository.findById(orderId);

        final OrderFulfillment fulfillment = fulfillmentRepository.findById(fulfillmentId)
                .verifyBelongsToOrder(order);

        final List<OrderLine> orderLines = removeLines(fulfillment, lineIds);

        return DeleteFulfillmentLinesResult.builder()
                .order(order)
                .updatedLines(orderLines)
                .build();
    }

    public CompleteFulfillmentResult completeFulfillment(
            @NotNull final Long orderId,
            @NotNull final Long fulfillmentId,
            @NotNull @Valid final CompleteOrderFulfillmentForm form
    ) {
        final Order order = orderRepository.findById(orderId);

        final OrderFulfillment fulfillment = fulfillmentRepository.findById(fulfillmentId)
                .verifyBelongsToOrder(order);

        preLoadOrderLines(fulfillmentId);

        final List<OrderLine> orderLines = fulfillment.complete();

        optional(form.getTrackingNumber())
                .ifPresent(fulfillment::setTrackingNumber);

        return CompleteFulfillmentResult.builder()
                .order(order)
                .updatedLines(orderLines)
                .completedFulfillment(fulfillment)
                .build();
    }

    public OrderFulfillment updateTrackingNumber(
            @NotNull final Long orderId,
            @NotNull final Long fulfillmentId,
            @NotNull @Valid final UpdateOrderFulfillmentTrackingNumberForm form
    ) {
        final Order order = orderRepository.findById(orderId);

        final OrderFulfillment fulfillment = fulfillmentRepository.findById(fulfillmentId)
                .verifyBelongsToOrder(order);

        fulfillment.setTrackingNumber(form.getTrackingNumber());

        return fulfillment;
    }

    public DeleteFulfillmentResult deleteFulfillment(@NotNull final Long orderId, @NotNull final Long fulfillmentId) {
        final Order order = orderRepository.findById(orderId);

        final OrderFulfillment fulfillment = fulfillmentRepository.findById(fulfillmentId)
                .verifyBelongsToOrder(order);

        preLoadOrderLines(fulfillmentId);

        final List<OrderLine> orderLines = order.removeFulfillment(fulfillment);

        return DeleteFulfillmentResult.builder()
                .order(order)
                .updatedLines(orderLines)
                .build();
    }

    public Order fulfill(@NotNull final Long orderId) {
        final Order order = orderRepository.findById(orderId);

        order.fulfill();

        return order;
    }

    public Order undoFulfill(@NotNull final Long orderId) {
        final Order order = orderRepository.findById(orderId);

        order.undoFulfill();

        return order;
    }

    public Order cancel(@NotNull final Long orderId) {
        final Order order = orderRepository.findById(orderId);

        order.cancel();

        return order;
    }

    public Order reinstate(@NotNull final Long orderId) {
        final Order order = orderRepository.findById(orderId);

        order.reinstate();

        return order;
    }

    private void preLoadOrderLines(final Long fulfillmentId) {
        final List<OrderFulfillmentLine> fulfillmentLines = fulfillmentLineRepository.findAllByFulfillmentId(fulfillmentId);
        preLoadOrderLines(fulfillmentLines);
    }

    private void preLoadOrderLines(final List<OrderFulfillmentLine> fulfillmentLines) {
        final Set<Long> orderLineIds = fulfillmentLines.stream()
                .map(OrderFulfillmentLine::getOrderLine)
                .map(OrderLine::getId)
                .collect(Collectors.toSet());
        orderLineRepository.findAllByIdDoNotThrow(orderLineIds);
    }

    private List<OrderLine> getOrderLines(final List<OrderFulfillmentLine> fulfillmentLines) {
        return fulfillmentLines.stream()
                .map(OrderFulfillmentLine::getOrderLine)
                .collect(Collectors.toList());
    }

    private List<OrderFulfillmentLine> addLines(
            final OrderFulfillment fulfillment,
            final List<Line> lines
    ) {
        final MergedLines mergedLines = MergedLines.merge(lines);

        final List<OrderLine> orderLines = orderLineRepository.findAllById(mergedLines.getIds()).stream()
                .map(ol -> ol.verifyBelongsToOrder(fulfillment.getOrder()))
                .collect(Collectors.toList());

        return fulfillmentLineRepository.saveAll(
                orderLines.stream()
                        .map(
                                ol -> fulfillment.newLine()
                                        .orderLine(ol)
                                        .quantity(mergedLines.getQuantity(ol.getId()))
                                        .addLine()
                        )
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList())
        );
    }

    private List<OrderFulfillmentLine> setQuantities(final OrderFulfillment fulfillment, final List<Line> lines) {
        final MergedLines mergedLines = MergedLines.merge(lines);

        final List<OrderFulfillmentLine> fulfillmentLines = fulfillmentLineRepository.findAllById(mergedLines.getIds()).stream()
                .map(fl -> fl.verifyBelongsToFulfillment(fulfillment))
                .collect(Collectors.toList());

        preLoadOrderLines(fulfillmentLines);

        fulfillmentLines.forEach(fl -> fl.setQuantity(mergedLines.getQuantity(fl.getId())));

        return fulfillmentLines;
    }

    private List<OrderLine> removeLines(final OrderFulfillment fulfillment, final Set<Long> lineIds) {
        final List<OrderFulfillmentLine> fulfillmentLines = fulfillmentLineRepository.findAllByIdDoNotThrow(lineIds).stream()
                .filter(fl -> fl.belongsToFulfillment(fulfillment))
                .collect(Collectors.toList());

        preLoadOrderLines(fulfillmentLines);

        return fulfillmentLines.stream()
                .map(fulfillment::removeLine)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

}
