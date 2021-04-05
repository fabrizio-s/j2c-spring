package com.j2c.j2c.service.mapper;

import com.j2c.j2c.domain.entity.User;
import com.j2c.j2c.service.dto.UserDTO;
import com.j2c.j2c.service.test.BaseMapperTest;
import org.junit.jupiter.api.Test;

import static com.j2c.j2c.service.test.TestUtils.nextObject;
import static org.junit.jupiter.api.Assertions.*;
import static org.mapstruct.factory.Mappers.getMapper;

class UserDTOMapperTest extends BaseMapperTest {

    @Test
    void fromEntity() {
        final User entity = nextObject(User.class);

        final UserDTO dto = getMapper(UserDTOMapper.class).fromEntity(entity).build();

        assertMappings(entity, dto, "password", "role");
        assertNull(dto.getPassword());
        assertNull(dto.getRole());
    }

}
