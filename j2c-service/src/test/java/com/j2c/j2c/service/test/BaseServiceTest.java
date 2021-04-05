package com.j2c.j2c.service.test;

import com.j2c.j2c.domain.entity.Entity;
import com.j2c.j2c.service.application.impl.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static com.j2c.j2c.service.test.MockRepositoryUtils.mockRepositorySave;
import static com.j2c.j2c.service.test.MockRepositoryUtils.mockRepositorySaveAll;

@ExtendWith(SpringExtension.class)
@ComponentScan({
        "com.j2c.j2c.domain.repository",
        "com.j2c.j2c.service.mapper",
        "com.j2c.j2c.service.domain",
        "com.j2c.j2c.service.test"
})
@Import({
        ApplicationExceptionHandler.class,
        ConfigurationServiceImpl.class,
        ShippingServiceImpl.class,
        ProductServiceImpl.class,
        OrderServiceImpl.class,
        CheckoutServiceImpl.class,
        UserServiceImpl.class,
        NoOpPasswordEncoder.class,
})
@ImportAutoConfiguration({
        ValidationAutoConfiguration.class,
        AopAutoConfiguration.class
})
public abstract class BaseServiceTest {

    @BeforeEach
    protected void setUp() {
        MockBeanProvider.getRepositories()
                .forEach(this::mockSaveRepository);
    }

    private <T extends Entity<?>> void mockSaveRepository(final JpaRepository<T, ?> repository) {
        mockRepositorySave(repository);
        mockRepositorySaveAll(repository);
    }

}
