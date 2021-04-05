package com.j2c.j2c.service.test;

import com.j2c.j2c.domain.entity.*;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestComponent;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@TestComponent
@RequiredArgsConstructor
public class ConfigurationServiceStubber {

    private final MockBeanProvider mockBeanProvider;

    @Builder(builderClassName = "MockConfigure",
            builderMethodName = "configure",
            buildMethodName = "stub")
    private void _configure(final Configuration configuration) {
        mockGetConfiguration(configuration);
    }

    private void mockGetConfiguration(final Configuration configuration) {
        if (configuration != null) {
            when(mockBeanProvider.getConfigurationRepository().findByProfile(isNotNull()))
                    .thenReturn(Optional.of(configuration));
        }
    }

}
