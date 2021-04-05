package com.j2c.j2c.service.mapper;

import com.j2c.j2c.domain.entity.Configuration;
import com.j2c.j2c.service.dto.ConfigurationDTO;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConfigurationServiceMapper {

    private final ConfigurationDTOMapper configurationDTOMapper;

    public ConfigurationDTO toConfigurationDTO(@NonNull final Configuration configuration) {
        return configurationDTOMapper.fromEntity(configuration).build();
    }

}
