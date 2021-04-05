package com.j2c.j2c.service.mapper;

import com.j2c.j2c.domain.entity.OrderFulfillment;
import com.j2c.j2c.service.dto.OrderFulfillmentDTO;
import com.j2c.j2c.service.test.BaseMapperTest;
import org.junit.jupiter.api.Test;

import static com.j2c.j2c.service.test.TestUtils.nextObject;
import static org.junit.jupiter.api.Assertions.*;
import static org.mapstruct.factory.Mappers.getMapper;

class OrderFulfillmentDTOMapperTest extends BaseMapperTest {

    @Test
    void fromEntity() {
        final OrderFulfillment entity = nextObject(OrderFulfillment.class);

        final OrderFulfillmentDTO dto = getMapper(OrderFulfillmentDTOMapper.class).fromEntity(entity).build();

        assertMappings(entity, dto, "orderId", "lines", "addedLines", "updatedLines");
        assertEquals(entity.getOrder().getId(), dto.getOrderId());
        assertNull(dto.getLines());
        assertNull(dto.getAddedLines());
        assertNull(dto.getUpdatedLines());
    }

}
