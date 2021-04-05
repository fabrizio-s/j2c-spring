package com.j2c.j2c.web.config;

import com.j2c.j2c.domain.enums.RoleType;
import com.j2c.j2c.service.application.UserService;
import com.j2c.j2c.service.input.CreateUserForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;

@Slf4j
@Configuration
@Profile("production")
public class ProductionConfig {

    @Value("${j2c.web.create-default-admin:false}")
    private boolean createDefaultAdmin;

    @Autowired
    private UserService userService;

    @EventListener
    @Order(0)
    public void onApplicationReadyEvent(final ApplicationReadyEvent event) {
        createAdminIfNoUsersExist();
    }

    private void createAdminIfNoUsersExist() {
        if (createDefaultAdmin && userService.total() == 0) {
            final String email = "admin@j2c.com";
            final String password = "admin";
            log.info("Creating default admin user with email '" + email + "' and password '" + password + "'");
            userService.create(
                    CreateUserForm.builder()
                            .email(email)
                            .password(password)
                            .enabled(true)
                            .verified(true)
                            .role(RoleType.Admin)
                            .build()
            );
        }
    }

}
