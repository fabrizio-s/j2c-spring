package com.j2c.j2c.service.mapper;

import com.j2c.j2c.domain.entity.UserVerificationToken;
import com.j2c.j2c.service.dto.UserVerificationTokenDTO;
import com.j2c.j2c.service.test.BaseMapperTest;
import org.junit.jupiter.api.Test;

import static com.j2c.j2c.service.test.TestUtils.nextObject;
import static org.junit.jupiter.api.Assertions.*;
import static org.mapstruct.factory.Mappers.getMapper;

class UserVerificationTokenDTOMapperTest extends BaseMapperTest {

    @Test
    void fromEntity() {
        final UserVerificationToken entity = nextObject(UserVerificationToken.class);

        final UserVerificationTokenDTO dto = getMapper(UserVerificationTokenDTOMapper.class).fromEntity(entity).build();

        assertMappings(entity, dto, "userId");
        assertEquals(entity.getUser().getId(), dto.getUserId());
    }

}
