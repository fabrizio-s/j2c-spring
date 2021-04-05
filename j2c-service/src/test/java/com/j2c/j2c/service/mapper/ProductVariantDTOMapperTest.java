package com.j2c.j2c.service.mapper;

import com.j2c.j2c.domain.entity.ProductVariant;
import com.j2c.j2c.service.dto.ProductVariantDTO;
import com.j2c.j2c.service.test.BaseMapperTest;
import org.junit.jupiter.api.Test;

import static com.j2c.j2c.service.test.TestUtils.nextObject;
import static org.junit.jupiter.api.Assertions.*;
import static org.mapstruct.factory.Mappers.getMapper;

class ProductVariantDTOMapperTest extends BaseMapperTest {

    @Test
    void fromEntity() {
        final ProductVariant entity = nextObject(ProductVariant.class);

        final ProductVariantDTO dto = getMapper(ProductVariantDTOMapper.class).fromEntity(entity).build();

        assertMappings(entity, dto, "productId", "images", "addedImages");
        assertEquals(entity.getProduct().getId(), dto.getProductId());
        assertNull(dto.getImages());
        assertNull(dto.getAddedImages());
    }

}
