package com.j2c.j2c.domain.entity;

import com.j2c.j2c.domain.test.MockEntity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoleTest {

    @Test
    void getAuthorities_HappyPath_ShouldReturnImmutableCopy() {
        final Role role = MockEntity.role().build();

        assertThrows(
                UnsupportedOperationException.class,
                () -> role.getAuthorities().add(new Authority())
        );
    }

}
