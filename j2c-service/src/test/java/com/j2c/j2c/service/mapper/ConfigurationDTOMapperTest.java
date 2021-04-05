package com.j2c.j2c.service.mapper;

import com.j2c.j2c.domain.entity.Configuration;
import com.j2c.j2c.service.dto.ConfigurationDTO;
import com.j2c.j2c.service.test.BaseMapperTest;
import org.junit.jupiter.api.Test;

import static com.j2c.j2c.service.test.TestUtils.nextObject;
import static org.mapstruct.factory.Mappers.getMapper;

class ConfigurationDTOMapperTest extends BaseMapperTest {

    @Test
    void fromEntity() {
        final Configuration entity = nextObject(Configuration.class);

        final ConfigurationDTO dto = getMapper(ConfigurationDTOMapper.class).fromEntity(entity).build();

        assertMappings(entity, dto);
    }

}
