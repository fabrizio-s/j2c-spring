package com.j2c.j2c.service.mapper;

import com.j2c.j2c.service.dto.ShippingMethodDTO;
import com.j2c.j2c.domain.entity.ShippingMethod;
import com.j2c.j2c.service.dto.ShippingMethodDTO.ShippingMethodDTOBuilder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
abstract class ShippingMethodDTOMapper
        extends BaseDTOMapper<ShippingMethodDTO, ShippingMethodDTOBuilder, ShippingMethod> {

    @Override
    @Mapping(source = "zone.id", target = "zoneId")
    public abstract ShippingMethodDTOBuilder fromEntity(ShippingMethod method);

    @Override
    protected ShippingMethodDTOBuilder builder() {
        return ShippingMethodDTO.builder();
    }

}
