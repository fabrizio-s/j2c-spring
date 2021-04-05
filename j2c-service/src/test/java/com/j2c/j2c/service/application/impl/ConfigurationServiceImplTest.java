package com.j2c.j2c.service.application.impl;

import com.j2c.j2c.domain.entity.Configuration;
import com.j2c.j2c.domain.enums.MassUnit;
import com.j2c.j2c.service.dto.ConfigurationDTO;
import com.j2c.j2c.service.exception.InvalidInputException;
import com.j2c.j2c.service.input.ConfigurationForm;
import com.j2c.j2c.service.test.BaseServiceTest;
import com.j2c.j2c.service.test.ConfigurationServiceStubber;
import com.j2c.j2c.service.test.MockBeanProvider;
import com.j2c.j2c.service.test.MockEntity;
import com.neovisionaries.i18n.CurrencyCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class ConfigurationServiceImplTest extends BaseServiceTest {

    @Autowired
    private ConfigurationServiceImpl service;

    @Autowired
    private ConfigurationServiceStubber stubber;

    @Autowired
    private MockBeanProvider mockBeanProvider;

    @Test
    void configure_NullForm_ShouldThrowInvalidInputException() {
        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.configure(null)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("form must not be null"))
        );
    }

    @Test
    void configure_HappyPath_ShouldReturnUpdatedConfiguration() {
        final ConfigurationForm form = hpConfigurationForm().build();

        stubber.configure()
                .configuration(defaultConfiguration())
                .stub();

        final ConfigurationDTO configurationDTO = service.configure(form);

        assertNotNull(configurationDTO);
        assertEquals(form.getCurrency(), configurationDTO.getCurrency());
        assertEquals(form.getMassUnit(), configurationDTO.getMassUnit());
    }

    private static ConfigurationForm.ConfigurationFormBuilder hpConfigurationForm() {
        return ConfigurationForm.builder()
                .currency(CurrencyCode.USD)
                .massUnit(MassUnit.g);
    }

    private static Configuration defaultConfiguration() {
        return MockEntity.configuration()
                .massUnit(MassUnit.g)
                .currency(CurrencyCode.EUR)
                .build();
    }

}
