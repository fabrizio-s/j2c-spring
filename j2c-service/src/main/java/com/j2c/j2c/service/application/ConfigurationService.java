package com.j2c.j2c.service.application;

import com.j2c.j2c.service.dto.ConfigurationDTO;
import com.j2c.j2c.service.input.ConfigurationForm;

public interface ConfigurationService {

    ConfigurationDTO getConfiguration();

    ConfigurationDTO configure(ConfigurationForm form);

}
