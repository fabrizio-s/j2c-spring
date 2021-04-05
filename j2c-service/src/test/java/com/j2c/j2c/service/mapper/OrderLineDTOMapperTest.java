package com.j2c.j2c.service.mapper;

import com.j2c.j2c.domain.entity.OrderLine;
import com.j2c.j2c.service.dto.OrderLineDTO;
import com.j2c.j2c.service.test.BaseMapperTest;
import org.junit.jupiter.api.Test;

import static com.j2c.j2c.service.test.TestUtils.nextObject;
import static org.junit.jupiter.api.Assertions.*;
import static org.mapstruct.factory.Mappers.getMapper;

class OrderLineDTOMapperTest extends BaseMapperTest {

    @Test
    void fromEntity() {
        final OrderLine entity = nextObject(OrderLine.class);

        final OrderLineDTO dto = getMapper(OrderLineDTOMapper.class).fromEntity(entity).build();

        assertMappings(entity, dto, "orderId", "productId");
        assertEquals(entity.getOrder().getId(), dto.getOrderId());
        assertEquals(entity.getProduct().getId(), dto.getProductId());
    }

}
