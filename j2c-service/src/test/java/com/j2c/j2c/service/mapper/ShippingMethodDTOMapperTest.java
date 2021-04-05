package com.j2c.j2c.service.mapper;

import com.j2c.j2c.domain.entity.ShippingMethod;
import com.j2c.j2c.service.dto.ShippingMethodDTO;
import com.j2c.j2c.service.test.BaseMapperTest;
import org.junit.jupiter.api.Test;

import static com.j2c.j2c.service.test.TestUtils.nextObject;
import static org.junit.jupiter.api.Assertions.*;
import static org.mapstruct.factory.Mappers.getMapper;

class ShippingMethodDTOMapperTest extends BaseMapperTest {

    @Test
    void fromEntity() {
        final ShippingMethod entity = nextObject(ShippingMethod.class);

        final ShippingMethodDTO dto = getMapper(ShippingMethodDTOMapper.class).fromEntity(entity).build();

        assertMappings(entity, dto, "zoneId");
        assertEquals(entity.getZone().getId(), dto.getZoneId());
    }

}
