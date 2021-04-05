package com.j2c.j2c.service.application.impl;

import com.j2c.j2c.domain.entity.Order;
import com.j2c.j2c.domain.entity.OrderFulfillment;
import com.j2c.j2c.domain.entity.OrderFulfillmentLine;
import com.j2c.j2c.domain.entity.OrderLine;
import com.j2c.j2c.domain.repository.OrderFulfillmentLineRepository;
import com.j2c.j2c.domain.repository.OrderFulfillmentRepository;
import com.j2c.j2c.domain.repository.OrderLineRepository;
import com.j2c.j2c.domain.repository.OrderRepository;
import com.j2c.j2c.service.application.OrderService;
import com.j2c.j2c.service.domain.order.*;
import com.j2c.j2c.service.dto.OrderDTO;
import com.j2c.j2c.service.dto.OrderFulfillmentDTO;
import com.j2c.j2c.service.dto.OrderFulfillmentLineDTO;
import com.j2c.j2c.service.dto.OrderLineDTO;
import com.j2c.j2c.service.input.CompleteOrderFulfillmentForm;
import com.j2c.j2c.service.input.Line;
import com.j2c.j2c.service.input.UpdateOrderFulfillmentTrackingNumberForm;
import com.j2c.j2c.service.mapper.OrderServiceMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.*;
import java.util.List;
import java.util.Set;

@Service
@Validated
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderServiceMapper mapper;
    private final DomainOrderService domainService;
    private final OrderRepository orderRepository;
    private final OrderLineRepository lineRepository;
    private final OrderFulfillmentRepository fulfillmentRepository;
    private final OrderFulfillmentLineRepository fulfillmentLineRepository;

    @Override
    public OrderDTO find(@NotNull final Long orderId) {
        final Order order = orderRepository.findById(orderId);
        return mapper.toOrderDTO(order);
    }

    @Override
    public Page<OrderDTO> findAll(@NotNull final Pageable pageable) {
        final Page<Order> orders = orderRepository.findAll(pageable);
        return mapper.toOrderDTO(orders);
    }

    @Override
    public Page<OrderLineDTO> findLines(@NotNull final Long orderId, @NotNull final Pageable pageable) {
        orderRepository.verifyExistsById(orderId);
        final Page<OrderLine> lines = lineRepository.findAllByOrderId(orderId, pageable);
        return mapper.toLineDTO(lines);
    }

    @Override
    public OrderLineDTO findLine(@NotNull final Long orderId, @NotNull final Long lineId) {
        final Order order = orderRepository.findById(orderId);
        final OrderLine line = lineRepository.findById(lineId).verifyBelongsToOrder(order);
        return mapper.toLineDTO(line);
    }

    @Override
    public Page<OrderFulfillmentDTO> findFulfillments(@NotNull final Long orderId, @NotNull final Pageable pageable) {
        orderRepository.verifyExistsById(orderId);
        final Page<OrderFulfillment> fulfillments = fulfillmentRepository.findAllByOrderId(orderId, pageable);
        return mapper.toFulfillmentDTO(fulfillments);
    }

    @Override
    public OrderFulfillmentDTO findFulfillment(@NotNull final Long orderId, @NotNull final Long fulfillmentId) {
        final Order order = orderRepository.findById(orderId);
        final OrderFulfillment fulfillment = fulfillmentRepository.findById(fulfillmentId).verifyBelongsToOrder(order);
        return mapper.toFulfillmentDTO(fulfillment);
    }

    @Override
    public Page<OrderFulfillmentLineDTO> findFulfillmentLines(
            @NotNull final Long orderId,
            @NotNull final Long fulfillmentId,
            @NotNull final Pageable pageable
    ) {
        final Order order = orderRepository.findById(orderId);
        fulfillmentRepository.findById(fulfillmentId).verifyBelongsToOrder(order);
        final Page<OrderFulfillmentLine> fulfillmentLines = fulfillmentLineRepository.findAllByFulfillmentId(fulfillmentId, pageable);
        return mapper.toFulfillmentLineDTO(fulfillmentLines);
    }

    @Override
    public OrderFulfillmentLineDTO findFulfillmentLine(
            @NotNull final Long orderId,
            @NotNull final Long fulfillmentId,
            @NotNull final Long fulfillmentLineId
    ) {
        final Order order = orderRepository.findById(orderId);
        final OrderFulfillment fulfillment = fulfillmentRepository.findById(fulfillmentId).verifyBelongsToOrder(order);
        final OrderFulfillmentLine fulfillmentLine = fulfillmentLineRepository.findById(fulfillmentLineId).verifyBelongsToFulfillment(fulfillment);
        return mapper.toFulfillmentLineDTO(fulfillmentLine);
    }

    @Override
    public OrderDTO confirm(final Long orderId) {
        final Order confirmedOrder = domainService.confirm(orderId);
        return mapper.toOrderDTO(confirmedOrder);
    }

    @Override
    public OrderDTO createFulfillment(final Long orderId, final List<Line> lines) {
        final CreateFulfillmentResult result = domainService.createFulfillment(orderId, lines);
        return mapper.toOrderDTO(result);
    }

    @Override
    public OrderDTO addFulfillmentLines(final Long orderId, final Long fulfillmentId, final List<Line> lines) {
        final AddFulfillmentLinesResult result = domainService.addFulfillmentLines(orderId, fulfillmentId, lines);
        return mapper.toOrderDTO(result);
    }

    @Override
    public OrderDTO updateFulfillmentLineQuantities(final Long orderId, final Long fulfillmentId, final List<Line> lines) {
        final UpdateFulfillmentLineQuantitiesResult result = domainService.updateFulfillmentLineQuantities(orderId, fulfillmentId, lines);
        return mapper.toOrderDTO(result);
    }

    @Override
    public OrderDTO deleteFulfillmentLines(final Long orderId, final Long fulfillmentId, final Set<Long> lineIds) {
        final DeleteFulfillmentLinesResult result = domainService.deleteFulfillmentLines(orderId, fulfillmentId, lineIds);
        return mapper.toOrderDTO(result);
    }

    @Override
    public OrderDTO completeFulfillment(final Long orderId, final Long fulfillmentId, final CompleteOrderFulfillmentForm form) {
        final CompleteFulfillmentResult result = domainService.completeFulfillment(orderId, fulfillmentId, form);
        return mapper.toOrderDTO(result);
    }

    @Override
    public OrderFulfillmentDTO updateTrackingNumber(final Long orderId, final Long fulfillmentId, final UpdateOrderFulfillmentTrackingNumberForm form) {
        final OrderFulfillment updatedFulfillment = domainService.updateTrackingNumber(orderId, fulfillmentId, form);
        return mapper.toFulfillmentDTO(updatedFulfillment);
    }

    @Override
    public OrderDTO deleteFulfillment(final Long orderId, final Long fulfillmentId) {
        final DeleteFulfillmentResult result = domainService.deleteFulfillment(orderId, fulfillmentId);
        return mapper.toOrderDTO(result);
    }

    @Override
    public OrderDTO fulfill(final Long orderId) {
        final Order fulfilledOrder = domainService.fulfill(orderId);
        return mapper.toOrderDTO(fulfilledOrder);
    }

    @Override
    public OrderDTO undoFulfill(final Long orderId) {
        final Order unfulfilledOrder = domainService.undoFulfill(orderId);
        return mapper.toOrderDTO(unfulfilledOrder);
    }

    @Override
    public OrderDTO cancel(final Long orderId) {
        final Order cancelledOrder = domainService.cancel(orderId);
        return mapper.toOrderDTO(cancelledOrder);
    }

    @Override
    public OrderDTO reinstate(final Long orderId) {
        final Order reinstatedOrder = domainService.reinstate(orderId);
        return mapper.toOrderDTO(reinstatedOrder);
    }

}
