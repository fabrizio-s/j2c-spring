package com.j2c.j2c.service.mapper;

import com.j2c.j2c.service.dto.ShippingZoneDTO;
import com.j2c.j2c.domain.entity.ShippingZone;
import com.j2c.j2c.service.dto.ShippingZoneDTO.ShippingZoneDTOBuilder;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
abstract class ShippingZoneDTOMapper
        extends BaseDTOMapper<ShippingZoneDTO, ShippingZoneDTOBuilder, ShippingZone> {

    @Override
    public abstract ShippingZoneDTOBuilder fromEntity(ShippingZone zone);

    @Override
    protected ShippingZoneDTOBuilder builder() {
        return ShippingZoneDTO.builder();
    }

}
