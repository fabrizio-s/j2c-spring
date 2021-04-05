package com.j2c.j2c.service.domain.configuration;

import com.j2c.j2c.domain.entity.Configuration;
import com.j2c.j2c.domain.repository.ConfigurationRepository;
import com.j2c.j2c.service.input.ConfigurationForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import static com.j2c.j2c.domain.util.J2cUtils.optional;

@Service
@Validated
@Transactional
@RequiredArgsConstructor
public class DomainConfigurationService {

    private final ConfigurationRepository configurationRepository;

    public Configuration configure(@NotNull @Valid final ConfigurationForm form) {
        final Configuration configuration = configurationRepository.getConfiguration();

        optional(form.getCurrency()).ifPresent(configuration::setCurrency);
        optional(form.getMassUnit()).ifPresent(configuration::setMassUnit);

        return configuration;
    }

}
