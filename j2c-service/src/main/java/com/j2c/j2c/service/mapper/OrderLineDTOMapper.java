package com.j2c.j2c.service.mapper;

import com.j2c.j2c.domain.entity.OrderLine;
import com.j2c.j2c.service.dto.OrderLineDTO;
import com.j2c.j2c.service.dto.OrderLineDTO.OrderLineDTOBuilder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
abstract class OrderLineDTOMapper
        extends BaseDTOMapper<OrderLineDTO, OrderLineDTOBuilder, OrderLine> {

    @Override
    @Mapping(source = "order.id", target = "orderId")
    @Mapping(source = "product.id", target = "productId")
    public abstract OrderLineDTOBuilder fromEntity(OrderLine line);

    @Override
    protected OrderLineDTOBuilder builder() {
        return OrderLineDTO.builder();
    }

}
