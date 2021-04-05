package com.j2c.j2c.service.mapper;

import com.j2c.j2c.domain.entity.ShippingZone;
import com.j2c.j2c.service.dto.ShippingZoneDTO;
import com.j2c.j2c.service.test.BaseMapperTest;
import org.junit.jupiter.api.Test;

import static com.j2c.j2c.service.test.TestUtils.nextObject;
import static org.junit.jupiter.api.Assertions.*;
import static org.mapstruct.factory.Mappers.getMapper;

class ShippingZoneDTOMapperTest extends BaseMapperTest {

    @Test
    void fromEntity() {
        final ShippingZone entity = nextObject(ShippingZone.class);

        final ShippingZoneDTO dto = getMapper(ShippingZoneDTOMapper.class).fromEntity(entity).build();

        assertMappings(entity, dto, "countries", "addedCountries", "removedCountries");
        assertNull(dto.getCountries());
        assertNull(dto.getAddedCountries());
        assertNull(dto.getRemovedCountries());
    }

}
