package com.j2c.j2c.service.mapper;

import com.j2c.j2c.domain.entity.Checkout;
import com.j2c.j2c.service.dto.CheckoutDTO;
import com.j2c.j2c.service.dto.CheckoutDTO.CheckoutDTOBuilder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
        uses = {
            ShippingMethodDetailsDTOMapper.class,
            AddressDTOMapper.class
        }
)
abstract class CheckoutDTOMapper
        extends BaseDTOMapper<CheckoutDTO, CheckoutDTOBuilder, Checkout> {

    @Override
    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(target = "lines", ignore = true)
    @Mapping(target = "paymentToken", ignore = true)
    public abstract CheckoutDTOBuilder fromEntity(Checkout checkout);

    @Override
    protected CheckoutDTOBuilder builder() {
        return CheckoutDTO.builder();
    }

}
