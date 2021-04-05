package com.j2c.j2c.service.mapper;

import com.j2c.j2c.domain.entity.Address;
import com.j2c.j2c.service.dto.AddressDTO;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
abstract class AddressDTOMapper {

    public abstract AddressDTO fromVO(final Address address);

}
