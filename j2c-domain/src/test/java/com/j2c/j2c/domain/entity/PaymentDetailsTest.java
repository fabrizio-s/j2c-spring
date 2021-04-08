package com.j2c.j2c.domain.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class PaymentDetailsTest {

    @Test
    void new_NullId_ShouldThrowIllegalArgumentException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> PaymentDetails.builder()
                        .id(null)
                        .token("ABC123")
                        .build()
        );
    }

    @Test
    void new_NullToken_ShouldThrowIllegalArgumentException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> PaymentDetails.builder()
                        .id("ABC123")
                        .token(null)
                        .build()
        );
    }

}
