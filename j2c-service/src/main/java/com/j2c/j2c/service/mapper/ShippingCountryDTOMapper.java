package com.j2c.j2c.service.mapper;

import com.j2c.j2c.domain.entity.ShippingCountry;
import com.j2c.j2c.service.dto.ShippingCountryDTO;
import com.j2c.j2c.service.dto.ShippingCountryDTO.ShippingCountryDTOBuilder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
abstract class ShippingCountryDTOMapper
        extends BaseDTOMapper<ShippingCountryDTO, ShippingCountryDTOBuilder, ShippingCountry> {

    @Override
    @Mapping(source = "zone.id", target = "zoneId")
    public abstract ShippingCountryDTOBuilder fromEntity(ShippingCountry country);

    @Override
    protected ShippingCountryDTOBuilder builder() {
        return ShippingCountryDTO.builder();
    }

}
