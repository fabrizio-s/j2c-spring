package com.j2c.j2c.service.mapper;

import com.j2c.j2c.domain.entity.Order;
import com.j2c.j2c.domain.entity.OrderFulfillment;
import com.j2c.j2c.domain.entity.OrderFulfillmentLine;
import com.j2c.j2c.domain.entity.OrderLine;
import com.j2c.j2c.service.domain.order.*;
import com.j2c.j2c.service.dto.OrderDTO;
import com.j2c.j2c.service.dto.OrderDTO.OrderDTOBuilder;
import com.j2c.j2c.service.dto.OrderFulfillmentDTO;
import com.j2c.j2c.service.dto.OrderFulfillmentDTO.OrderFulfillmentDTOBuilder;
import com.j2c.j2c.service.dto.OrderFulfillmentLineDTO;
import com.j2c.j2c.service.dto.OrderLineDTO;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderServiceMapper {

    private final OrderDTOMapper orderDTOMapper;
    private final OrderLineDTOMapper lineDTOMapper;
    private final OrderFulfillmentDTOMapper fulfillmentDTOMapper;
    private final OrderFulfillmentLineDTOMapper fulfillmentLineDTOMapper;

    public OrderLineDTO toLineDTO(@NonNull final OrderLine line) {
        return lineDTOMapper.fromEntity(line).build();
    }

    public Page<OrderLineDTO> toLineDTO(final Page<OrderLine> lines) {
        return lineDTOMapper.fromEntities(lines);
    }

    public OrderFulfillmentDTO toFulfillmentDTO(@NonNull final OrderFulfillment fulfillment) {
        return fulfillmentDTOMapper.fromEntity(fulfillment).build();
    }

    public Page<OrderFulfillmentDTO> toFulfillmentDTO(final Page<OrderFulfillment> fulfillments) {
        return fulfillmentDTOMapper.fromEntities(fulfillments);
    }

    public OrderFulfillmentLineDTO toFulfillmentLineDTO(@NonNull final OrderFulfillmentLine fulfillmentLine) {
        return fulfillmentLineDTOMapper.fromEntity(fulfillmentLine).build();
    }

    public Page<OrderFulfillmentLineDTO> toFulfillmentLineDTO(final Page<OrderFulfillmentLine> fulfillmentLines) {
        return fulfillmentLineDTOMapper.fromEntities(fulfillmentLines);
    }

    public OrderDTO toOrderDTO(@NonNull final Order order) {
        return orderDTOMapper.fromEntity(order).build();
    }

    public Page<OrderDTO> toOrderDTO(final Page<Order> orders) {
        return orderDTOMapper.fromEntities(orders);
    }

    public OrderDTO toOrderDTO(@NonNull final CreateFulfillmentResult result) {
        final OrderFulfillment fulfillment = result.getCreatedFulfillment();
        final OrderFulfillmentDTOBuilder fulfillmentDTOBuilder = fulfillmentDTOMapper.fromEntity(fulfillment);
        fulfillmentDTOBuilder.lines(fulfillmentLineDTOMapper.fromEntities(result.getCreatedFulfillmentLines()));

        return orderWithFulfillmentAndUpdatedLines(result.getOrder(), fulfillmentDTOBuilder.build(), result.getUpdatedLines());
    }

    public OrderDTO toOrderDTO(@NonNull final AddFulfillmentLinesResult result) {
        final OrderFulfillmentDTOBuilder fulfillmentDTOBuilder = fulfillmentDTOMapper.fromEntity(result.getFulfillment());
        fulfillmentDTOBuilder.addedLines(fulfillmentLineDTOMapper.fromEntities(result.getAddedFulfillmentLines()));

        return orderWithFulfillmentAndUpdatedLines(result.getOrder(), fulfillmentDTOBuilder.build(), result.getUpdatedLines());
    }

    public OrderDTO toOrderDTO(@NonNull final UpdateFulfillmentLineQuantitiesResult result) {
        final OrderFulfillmentDTOBuilder fulfillmentDTOBuilder = fulfillmentDTOMapper.fromEntity(result.getFulfillment());
        fulfillmentDTOBuilder.updatedLines(fulfillmentLineDTOMapper.fromEntities(result.getUpdatedFulfillmentLines()));

        return orderWithFulfillmentAndUpdatedLines(result.getOrder(), fulfillmentDTOBuilder.build(), result.getUpdatedLines());
    }

    public OrderDTO toOrderDTO(@NonNull final DeleteFulfillmentLinesResult result) {
        return orderWithFulfillmentAndUpdatedLines(result.getOrder(), null, result.getUpdatedLines());
    }

    public OrderDTO toOrderDTO(@NonNull final CompleteFulfillmentResult result) {
        final OrderFulfillmentDTOBuilder fulfillmentDTOBuilder = fulfillmentDTOMapper.fromEntity(result.getCompletedFulfillment());

        return orderWithFulfillmentAndUpdatedLines(result.getOrder(), fulfillmentDTOBuilder.build(), result.getUpdatedLines());
    }

    public OrderDTO toOrderDTO(@NonNull final DeleteFulfillmentResult result) {
        return orderWithFulfillmentAndUpdatedLines(result.getOrder(), null, result.getUpdatedLines());
    }

    private OrderDTO orderWithFulfillmentAndUpdatedLines(
            final Order order,
            final OrderFulfillmentDTO fulfillmentDTO,
            final List<OrderLine> updatedLines
    ) {
        final OrderDTOBuilder orderDTOBuilder = orderDTOMapper.fromEntity(order);
        orderDTOBuilder.fulfillment(fulfillmentDTO);
        orderDTOBuilder.updatedLines(lineDTOMapper.fromEntities(updatedLines));
        return orderDTOBuilder.build();
    }

}
