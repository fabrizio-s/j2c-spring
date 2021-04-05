package com.j2c.j2c.service.mapper;

import com.j2c.j2c.domain.entity.Address;
import com.j2c.j2c.service.input.CreateAddressForm;
import com.j2c.j2c.service.input.UpdateAddressForm;
import com.j2c.j2c.service.test.BaseMapperTest;
import org.junit.jupiter.api.Test;

import static com.j2c.j2c.service.test.TestUtils.nextObject;
import static org.mapstruct.factory.Mappers.getMapper;

class AddressVOMapperTest extends BaseMapperTest {

    private final AddressVOMapper mapper = getMapper(AddressVOMapper.class);

    @Test
    void createAddress() {
        final CreateAddressForm form = nextObject(CreateAddressForm.class);

        final Address address = mapper.fromCreateForm(form);

        assertMappings(form, address);
    }

    @Test
    void updateAddress() {
        final Address address = nextObject(Address.class);
        final UpdateAddressForm form = nextObject(UpdateAddressForm.class);

        final Address updatedAddress = mapper.fromUpdateForm(address, form);

        assertMappings(form, updatedAddress);
    }

}
