package com.j2c.j2c.service.mapper;

import com.j2c.j2c.domain.entity.Product;
import com.j2c.j2c.service.dto.ProductDTO;
import com.j2c.j2c.service.test.BaseMapperTest;
import org.junit.jupiter.api.Test;

import static com.j2c.j2c.service.test.TestUtils.nextObject;
import static org.junit.jupiter.api.Assertions.*;
import static org.mapstruct.factory.Mappers.getMapper;

class ProductDTOMapperTest extends BaseMapperTest {

    @Test
    void fromEntity() {
        final Product entity = nextObject(Product.class);

        final ProductDTO dto = getMapper(ProductDTOMapper.class).fromEntity(entity).build();

        assertMappings(
                entity,
                dto,
                "defaultVariantId",
                "defaultVariant",
                "categoryId",
                "tags",
                "addedTags",
                "removedTags"
        );
        assertEquals(entity.getDefaultVariant().getId(), dto.getDefaultVariantId());
        assertNull(dto.getDefaultVariant());
        assertEquals(entity.getCategory().getId(), dto.getCategoryId());
        assertNull(dto.getTags());
        assertNull(dto.getAddedTags());
        assertNull(dto.getRemovedTags());
    }

}
