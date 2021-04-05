package com.j2c.j2c.service.mapper;

import com.j2c.j2c.domain.entity.ProductVariantImage;
import com.j2c.j2c.service.dto.ProductVariantImageDTO;
import com.j2c.j2c.service.test.BaseMapperTest;
import org.junit.jupiter.api.Test;

import static com.j2c.j2c.service.test.TestUtils.nextObject;
import static org.junit.jupiter.api.Assertions.*;
import static org.mapstruct.factory.Mappers.getMapper;

class ProductVariantImageDTOMapperTest extends BaseMapperTest {

    @Test
    void fromEntity() {
        final ProductVariantImage entity = nextObject(ProductVariantImage.class);

        final ProductVariantImageDTO dto = getMapper(ProductVariantImageDTOMapper.class).fromEntity(entity).build();

        assertMappings(entity, dto, "variantId");
        assertEquals(entity.getVariant().getId(), dto.getVariantId());
    }

}
