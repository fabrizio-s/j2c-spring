package com.j2c.j2c.service.application.impl;

import com.j2c.j2c.domain.entity.Configuration;
import com.j2c.j2c.domain.repository.ConfigurationRepository;
import com.j2c.j2c.service.application.ConfigurationService;
import com.j2c.j2c.service.domain.configuration.DomainConfigurationService;
import com.j2c.j2c.service.dto.ConfigurationDTO;
import com.j2c.j2c.service.input.ConfigurationForm;
import com.j2c.j2c.service.mapper.ConfigurationServiceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
public class ConfigurationServiceImpl implements ConfigurationService {

    private final ConfigurationServiceMapper mapper;
    private final DomainConfigurationService domainService;
    private final ConfigurationRepository configurationRepository;

    @Override
    public ConfigurationDTO getConfiguration() {
        final Configuration configuration = configurationRepository.getConfiguration();
        return mapper.toConfigurationDTO(configuration);
    }

    @Override
    public ConfigurationDTO configure(final ConfigurationForm form) {
        final Configuration configuration = domainService.configure(form);
        return mapper.toConfigurationDTO(configuration);
    }

}
