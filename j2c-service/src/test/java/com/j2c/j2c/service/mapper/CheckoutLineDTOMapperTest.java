package com.j2c.j2c.service.mapper;

import com.j2c.j2c.domain.entity.CheckoutLine;
import com.j2c.j2c.service.dto.CheckoutLineDTO;
import com.j2c.j2c.service.test.BaseMapperTest;
import org.junit.jupiter.api.Test;

import static com.j2c.j2c.service.test.TestUtils.nextObject;
import static org.junit.jupiter.api.Assertions.*;
import static org.mapstruct.factory.Mappers.getMapper;

class CheckoutLineDTOMapperTest extends BaseMapperTest {

    @Test
    void fromEntity() {
        final CheckoutLine entity = nextObject(CheckoutLine.class);

        final CheckoutLineDTO dto = getMapper(CheckoutLineDTOMapper.class).fromEntity(entity).build();

        assertMappings(entity, dto, "checkoutId", "productId");
        assertEquals(entity.getCheckout().getId(), dto.getCheckoutId());
        assertEquals(entity.getProduct().getId(), dto.getProductId());
    }

}
