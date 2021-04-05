package com.j2c.j2c.service.mapper;

import com.j2c.j2c.domain.entity.ProductCategory;
import com.j2c.j2c.service.dto.ProductCategoryDTO;
import com.j2c.j2c.service.test.BaseMapperTest;
import org.junit.jupiter.api.Test;

import static com.j2c.j2c.service.test.TestUtils.nextObject;
import static org.junit.jupiter.api.Assertions.*;
import static org.mapstruct.factory.Mappers.getMapper;

class ProductCategoryDTOMapperTest extends BaseMapperTest {

    @Test
    void fromEntity() {
        final ProductCategory entity = nextObject(ProductCategory.class);

        final ProductCategoryDTO dto = getMapper(ProductCategoryDTOMapper.class).fromEntity(entity).build();

        assertMappings(entity, dto, "rootId", "parentId");
        assertEquals(entity.getRoot().getId(), dto.getRootId());
        assertEquals(entity.getParent().getId(), dto.getParentId());
    }

}
