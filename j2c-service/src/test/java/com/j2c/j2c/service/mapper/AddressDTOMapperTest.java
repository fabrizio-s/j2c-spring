package com.j2c.j2c.service.mapper;

import com.j2c.j2c.domain.entity.Address;
import com.j2c.j2c.service.dto.AddressDTO;
import com.j2c.j2c.service.test.BaseMapperTest;
import org.junit.jupiter.api.Test;

import static com.j2c.j2c.service.test.TestUtils.nextObject;
import static org.mapstruct.factory.Mappers.getMapper;

class AddressDTOMapperTest extends BaseMapperTest {

    private final AddressDTOMapper mapper = getMapper(AddressDTOMapper.class);

    @Test
    void fromValue() {
        final Address value = nextObject(Address.class);

        final AddressDTO dto = mapper.fromVO(value);

        assertMappings(value, dto);
    }

}
