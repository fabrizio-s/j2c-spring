package com.j2c.j2c.service.mapper;

import com.j2c.j2c.domain.entity.CheckoutLine;
import com.j2c.j2c.service.dto.CheckoutLineDTO;
import com.j2c.j2c.service.dto.CheckoutLineDTO.CheckoutLineDTOBuilder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
abstract class CheckoutLineDTOMapper
        extends BaseDTOMapper<CheckoutLineDTO, CheckoutLineDTOBuilder, CheckoutLine> {

    @Override
    @Mapping(source = "checkout.id", target = "checkoutId")
    @Mapping(source = "product.id", target = "productId")
    public abstract CheckoutLineDTOBuilder fromEntity(CheckoutLine line);

    @Override
    protected CheckoutLineDTOBuilder builder() {
        return CheckoutLineDTO.builder();
    }

}
