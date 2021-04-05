package com.j2c.j2c.service.mapper;

import com.j2c.j2c.domain.entity.ShippingMethodDetails;
import com.j2c.j2c.service.dto.ShippingMethodDetailsDTO;
import com.j2c.j2c.service.test.BaseMapperTest;
import org.junit.jupiter.api.Test;

import static com.j2c.j2c.service.test.TestUtils.nextObject;
import static org.mapstruct.factory.Mappers.getMapper;

class ShippingMethodDetailsDTOMapperTest extends BaseMapperTest {

    @Test
    void fromValue() {
        final ShippingMethodDetails value = nextObject(ShippingMethodDetails.class);

        final ShippingMethodDetailsDTO dto = getMapper(ShippingMethodDetailsDTOMapper.class).fromValue(value);

        assertMappings(value, dto);
    }

}
