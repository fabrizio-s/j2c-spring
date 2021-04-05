package com.j2c.j2c.service.mapper;

import com.j2c.j2c.domain.entity.ShippingMethodDetails;
import com.j2c.j2c.service.dto.ShippingMethodDetailsDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
abstract class ShippingMethodDetailsDTOMapper {

    public abstract ShippingMethodDetailsDTO fromValue(ShippingMethodDetails details);

}
