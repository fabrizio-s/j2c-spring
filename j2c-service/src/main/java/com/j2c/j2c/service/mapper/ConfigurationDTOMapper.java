package com.j2c.j2c.service.mapper;

import com.j2c.j2c.domain.entity.Configuration;
import com.j2c.j2c.service.dto.ConfigurationDTO;
import com.j2c.j2c.service.dto.ConfigurationDTO.ConfigurationDTOBuilder;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
abstract class ConfigurationDTOMapper
        extends BaseDTOMapper<ConfigurationDTO, ConfigurationDTOBuilder, Configuration> {

    @Override
    public abstract ConfigurationDTOBuilder fromEntity(Configuration configuration);

    @Override
    protected ConfigurationDTOBuilder builder() {
        return ConfigurationDTO.builder();
    }

}
