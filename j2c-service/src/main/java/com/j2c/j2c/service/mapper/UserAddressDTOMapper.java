package com.j2c.j2c.service.mapper;

import com.j2c.j2c.domain.entity.UserAddress;
import com.j2c.j2c.service.dto.UserAddressDTO;
import com.j2c.j2c.service.dto.UserAddressDTO.UserAddressDTOBuilder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
        uses = AddressDTOMapper.class)
abstract class UserAddressDTOMapper
        extends BaseDTOMapper<UserAddressDTO, UserAddressDTOBuilder, UserAddress> {

    @Override
    @Mapping(source = "user.id", target = "userId")
    public abstract UserAddressDTOBuilder fromEntity(final UserAddress userAddress);

    @Override
    protected UserAddressDTOBuilder builder() {
        return UserAddressDTO.builder();
    }

}
