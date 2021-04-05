package com.j2c.j2c.service.mapper;

import com.j2c.j2c.domain.entity.Role;
import com.j2c.j2c.service.dto.RoleDTO;
import com.j2c.j2c.service.dto.RoleDTO.RoleDTOBuilder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
abstract class RoleDTOMapper
        extends BaseDTOMapper<RoleDTO, RoleDTOBuilder, Role> {

    @Override
    @Mapping(target = "authorities", ignore = true)
    public abstract RoleDTOBuilder fromEntity(Role role);

    @Override
    protected RoleDTOBuilder builder() {
        return RoleDTO.builder();
    }

}
