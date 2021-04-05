package com.j2c.j2c.service.mapper;

import com.j2c.j2c.domain.entity.Checkout;
import com.j2c.j2c.service.dto.CheckoutDTO;
import com.j2c.j2c.service.test.BaseMapperTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.j2c.j2c.service.test.TestUtils.nextObject;
import static org.junit.jupiter.api.Assertions.*;
import static org.mapstruct.factory.Mappers.getMapper;
import static org.springframework.test.util.ReflectionTestUtils.setField;

class CheckoutDTOMapperTest extends BaseMapperTest {

    private CheckoutDTOMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = getMapper(CheckoutDTOMapper.class);
        setField(mapper, "shippingMethodDetailsDTOMapper", getMapper(ShippingMethodDetailsDTOMapper.class));
        setField(mapper, "addressDTOMapper", getMapper(AddressDTOMapper.class));
    }

    @Test
    void fromEntity() {
        final Checkout entity = nextObject(Checkout.class);

        final CheckoutDTO dto = mapper.fromEntity(entity).build();

        assertMappings(
                entity,
                dto,
                "customerId",
                "paymentToken",
                "address",
                "shippingMethodDetails",
                "shippingAddress",
                "lines"
        );
        assertEquals(entity.getCustomer().getId(), dto.getCustomerId());
        assertNull(dto.getPaymentToken());
        assertNull(dto.getLines());

        assertMappings(entity.getAddress(), dto.getAddress());
        assertMappings(entity.getShippingAddress(), dto.getShippingAddress());
        assertMappings(entity.getShippingMethodDetails(), dto.getShippingMethodDetails());
    }

}
