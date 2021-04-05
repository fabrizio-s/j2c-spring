package com.j2c.j2c.domain.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserVerificationTokenTest {

    @Test
    void new_NullUser_ShouldThrowIllegalArgumentException() {
        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> UserVerificationToken.builder()
                        .user(null)
                        .build()
        );
        assertEquals("user must not be null", exception.getMessage());
    }

}
