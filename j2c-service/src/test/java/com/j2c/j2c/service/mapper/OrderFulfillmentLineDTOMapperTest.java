package com.j2c.j2c.service.mapper;

import com.j2c.j2c.domain.entity.OrderFulfillmentLine;
import com.j2c.j2c.service.dto.OrderFulfillmentLineDTO;
import com.j2c.j2c.service.test.BaseMapperTest;
import org.junit.jupiter.api.Test;

import static com.j2c.j2c.service.test.TestUtils.nextObject;
import static org.junit.jupiter.api.Assertions.*;
import static org.mapstruct.factory.Mappers.getMapper;

class OrderFulfillmentLineDTOMapperTest extends BaseMapperTest {

    @Test
    void fromEntity() {
        final OrderFulfillmentLine entity = nextObject(OrderFulfillmentLine.class);

        final OrderFulfillmentLineDTO dto = getMapper(OrderFulfillmentLineDTOMapper.class).fromEntity(entity).build();

        assertMappings(entity, dto, "fulfillmentId", "orderLineId");
        assertEquals(entity.getFulfillment().getId(), dto.getFulfillmentId());
        assertEquals(entity.getOrderLine().getId(), dto.getOrderLineId());
    }

}
