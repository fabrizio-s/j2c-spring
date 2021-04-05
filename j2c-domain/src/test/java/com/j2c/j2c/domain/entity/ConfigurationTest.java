package com.j2c.j2c.domain.entity;

import com.j2c.j2c.domain.test.MockEntity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConfigurationTest {

    @Test
    void setCurrency_Null_ShouldThrowIllegalArgumentException() {
        final Configuration configuration = MockEntity.configuration().build();

        assertThrows(
                IllegalArgumentException.class,
                () -> configuration.setCurrency(null)
        );
    }

    @Test
    void setMassUnit_Null_ShouldThrowIllegalArgumentException() {
        final Configuration configuration = MockEntity.configuration().build();

        assertThrows(
                IllegalArgumentException.class,
                () -> configuration.setMassUnit(null)
        );
    }

}
