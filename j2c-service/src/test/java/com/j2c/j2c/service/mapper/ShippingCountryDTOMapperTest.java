package com.j2c.j2c.service.mapper;

import com.j2c.j2c.domain.entity.ShippingCountry;
import com.j2c.j2c.service.dto.ShippingCountryDTO;
import com.j2c.j2c.service.test.BaseMapperTest;
import org.junit.jupiter.api.Test;

import static com.j2c.j2c.service.test.TestUtils.nextObject;
import static org.junit.jupiter.api.Assertions.*;
import static org.mapstruct.factory.Mappers.getMapper;

class ShippingCountryDTOMapperTest extends BaseMapperTest {

    @Test
    void fromEntity() {
        final ShippingCountry entity = nextObject(ShippingCountry.class);

        final ShippingCountryDTO dto = getMapper(ShippingCountryDTOMapper.class).fromEntity(entity).build();

        assertMappings(entity, dto, "zoneId");
        assertEquals(entity.getZone().getId(), dto.getZoneId());
    }

}
