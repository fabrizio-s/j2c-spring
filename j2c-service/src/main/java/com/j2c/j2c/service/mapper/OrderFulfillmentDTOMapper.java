package com.j2c.j2c.service.mapper;

import com.j2c.j2c.domain.entity.OrderFulfillment;
import com.j2c.j2c.service.dto.OrderFulfillmentDTO;
import com.j2c.j2c.service.dto.OrderFulfillmentDTO.OrderFulfillmentDTOBuilder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
abstract class OrderFulfillmentDTOMapper
        extends BaseDTOMapper<OrderFulfillmentDTO, OrderFulfillmentDTOBuilder, OrderFulfillment> {

    @Override
    @Mapping(source = "order.id", target = "orderId")
    @Mapping(target = "lines", ignore = true)
    public abstract OrderFulfillmentDTOBuilder fromEntity(OrderFulfillment fulfillment);

    @Override
    protected OrderFulfillmentDTOBuilder builder() {
        return OrderFulfillmentDTO.builder();
    }

}
