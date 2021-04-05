package com.j2c.j2c.service.mapper;

import com.j2c.j2c.domain.entity.OrderFulfillmentLine;
import com.j2c.j2c.service.dto.OrderFulfillmentLineDTO;
import com.j2c.j2c.service.dto.OrderFulfillmentLineDTO.OrderFulfillmentLineDTOBuilder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
abstract class OrderFulfillmentLineDTOMapper
        extends BaseDTOMapper<OrderFulfillmentLineDTO, OrderFulfillmentLineDTOBuilder, OrderFulfillmentLine> {

    @Override
    @Mapping(source = "fulfillment.id", target = "fulfillmentId")
    @Mapping(source = "orderLine.id", target = "orderLineId")
    public abstract OrderFulfillmentLineDTOBuilder fromEntity(OrderFulfillmentLine fulfillmentLine);

    @Override
    protected OrderFulfillmentLineDTOBuilder builder() {
        return OrderFulfillmentLineDTO.builder();
    }

}
