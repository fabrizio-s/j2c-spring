package com.j2c.j2c.domain.entity;

import com.j2c.j2c.domain.test.MockEntity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ShippingZoneTest {

    @Test
    void new_NullName_ShouldThrowIllegalArgumentException() {
        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ShippingZone.builder()
                        .name(null)
                        .build()
        );
        assertEquals("name must not be null", exception.getMessage());
    }

    @Test
    void hasCountry_Null_ShouldReturnFalse() {
        final ShippingZone shippingZone = MockEntity.shippingZone().build();

        assertFalse(shippingZone.hasCountry(null));
    }

    @Test
    void hasCountry_HappyPath_ShouldReturnTrue() {
        final ShippingZone shippingZone = MockEntity.shippingZone().build();

        final ShippingCountry shippingCountry = MockEntity.shippingCountry()
                .zone(shippingZone)
                .build();

        assertTrue(shippingZone.hasCountry(shippingCountry.getCode()));
    }

    @Test
    void setName() {
        final ShippingZone shippingZone = MockEntity.shippingZone().build();

        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> shippingZone.setName(null)
        );
        assertEquals("name must not be null", exception.getMessage());
    }

}
