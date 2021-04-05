package com.j2c.j2c.web.controller;

import com.j2c.j2c.service.application.ConfigurationService;
import com.j2c.j2c.service.dto.ConfigurationDTO;
import com.j2c.j2c.service.input.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.j2c.j2c.domain.enums.Authorities.CONFIG;

@RestController
@RequiredArgsConstructor
@Tag(name = "Configuration", description = "Endpoints related to global application configuration")
public class ConfigurationController {

    private final ConfigurationService configurationService;

    @GetMapping(value = "/api/configuration",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Retrieves global application configuration settings")
    public ConfigurationDTO get() {
        return configurationService.getConfiguration();
    }

    @PatchMapping(value = "/api/configuration",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("authenticated and hasAuthority('" + CONFIG + "')")
    @Operation(security = @SecurityRequirement(name = "JWT"),
            summary = "Change global application configuration settings",
            description = "Requires " + CONFIG + " authority (Admin).")
    public ConfigurationDTO configure(@RequestBody final ConfigurationForm payload) {
        return configurationService.configure(payload);
    }

}
