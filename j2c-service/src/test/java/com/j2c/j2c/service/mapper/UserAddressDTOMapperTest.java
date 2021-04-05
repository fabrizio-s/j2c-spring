package com.j2c.j2c.service.mapper;

import com.j2c.j2c.domain.entity.UserAddress;
import com.j2c.j2c.service.dto.UserAddressDTO;
import com.j2c.j2c.service.test.BaseMapperTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.j2c.j2c.service.test.TestUtils.nextObject;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mapstruct.factory.Mappers.getMapper;
import static org.springframework.test.util.ReflectionTestUtils.setField;

class UserAddressDTOMapperTest extends BaseMapperTest {

    private UserAddressDTOMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = getMapper(UserAddressDTOMapper.class);
        setField(mapper, "addressDTOMapper", getMapper(AddressDTOMapper.class));
    }

    @Test
    void fromEntity() {
        final UserAddress entity = nextObject(UserAddress.class);

        final UserAddressDTO dto = mapper.fromEntity(entity).build();

        assertMappings(entity, dto, "userId", "address");
        assertEquals(entity.getUser().getId(), dto.getUserId());

        assertMappings(entity.getAddress(), dto.getAddress());
    }

}
