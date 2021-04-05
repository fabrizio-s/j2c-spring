package com.j2c.j2c.service.mapper;

import com.j2c.j2c.domain.entity.Order;
import com.j2c.j2c.service.dto.OrderDTO;
import com.j2c.j2c.service.test.BaseMapperTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.j2c.j2c.service.test.TestUtils.nextObject;
import static org.junit.jupiter.api.Assertions.*;
import static org.mapstruct.factory.Mappers.getMapper;
import static org.springframework.test.util.ReflectionTestUtils.setField;

class OrderDTOMapperTest extends BaseMapperTest {

    private OrderDTOMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = getMapper(OrderDTOMapper.class);
        setField(mapper, "shippingMethodDetailsDTOMapper", getMapper(ShippingMethodDetailsDTOMapper.class));
        setField(mapper, "addressDTOMapper", getMapper(AddressDTOMapper.class));
    }

    @Test
    public void fromEntity() {
        final Order entity = nextObject(Order.class);

        final OrderDTO dto = mapper.fromEntity(entity).build();

        assertMappings(
                entity,
                dto,
                "customerId",
                "address",
                "shippingMethodDetails",
                "shippingAddress",
                "fulfillment",
                "lines",
                "updatedLines"
        );
        assertEquals(entity.getCustomer().getId(), dto.getCustomerId());
        assertNull(dto.getFulfillment());
        assertNull(dto.getLines());
        assertNull(dto.getUpdatedLines());

        assertMappings(entity.getAddress(), dto.getAddress());
        assertMappings(entity.getShippingAddress(), dto.getShippingAddress());
        assertMappings(entity.getShippingMethodDetails(), dto.getShippingMethodDetails());
    }

}
