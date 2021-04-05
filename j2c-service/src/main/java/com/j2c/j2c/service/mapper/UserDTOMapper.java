package com.j2c.j2c.service.mapper;

import com.j2c.j2c.domain.entity.User;
import com.j2c.j2c.service.dto.UserDTO;
import com.j2c.j2c.service.dto.UserDTO.UserDTOBuilder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
abstract class UserDTOMapper
        extends BaseDTOMapper<UserDTO, UserDTOBuilder, User> {

    @Override
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    public abstract UserDTOBuilder fromEntity(User user);

    @Override
    protected UserDTOBuilder builder() {
        return UserDTO.builder();
    }

}
