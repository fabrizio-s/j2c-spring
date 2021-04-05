package com.j2c.j2c.service.mapper;

import com.j2c.j2c.domain.entity.ProductTag;
import com.j2c.j2c.service.dto.ProductTagDTO;
import com.j2c.j2c.service.test.BaseMapperTest;
import org.junit.jupiter.api.Test;

import static com.j2c.j2c.service.test.TestUtils.nextObject;
import static org.mapstruct.factory.Mappers.getMapper;

class ProductTagDTOMapperTest extends BaseMapperTest {

    @Test
    void fromEntity() {
        final ProductTag entity = nextObject(ProductTag.class);

        final ProductTagDTO dto = getMapper(ProductTagDTOMapper.class).fromEntity(entity).build();

        assertMappings(entity, dto);
    }

}
