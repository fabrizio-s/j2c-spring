package com.j2c.j2c.service.mapper;

import com.j2c.j2c.domain.entity.UserVerificationToken;
import com.j2c.j2c.service.dto.UserVerificationTokenDTO;
import com.j2c.j2c.service.dto.UserVerificationTokenDTO.UserVerificationTokenDTOBuilder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
abstract class UserVerificationTokenDTOMapper
        extends BaseDTOMapper<UserVerificationTokenDTO, UserVerificationTokenDTOBuilder, UserVerificationToken> {

    @Override
    @Mapping(source = "user.id", target = "userId")
    public abstract UserVerificationTokenDTOBuilder fromEntity(UserVerificationToken order);

    @Override
    protected UserVerificationTokenDTOBuilder builder() {
        return UserVerificationTokenDTO.builder();
    }

}
