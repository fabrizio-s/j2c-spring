package com.j2c.j2c.service.mapper;

import com.j2c.j2c.domain.entity.Order;
import com.j2c.j2c.service.dto.OrderDTO;
import com.j2c.j2c.service.dto.OrderDTO.OrderDTOBuilder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
        uses = {
                ShippingMethodDetailsDTOMapper.class,
                AddressDTOMapper.class
        }
)
abstract class OrderDTOMapper
        extends BaseDTOMapper<OrderDTO, OrderDTOBuilder, Order> {

    @Override
    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(target = "lines", ignore = true)
    @Mapping(target = "fulfillment", ignore = true)
    public abstract OrderDTOBuilder fromEntity(Order order);

    @Override
    protected OrderDTOBuilder builder() {
        return OrderDTO.builder();
    }

}
