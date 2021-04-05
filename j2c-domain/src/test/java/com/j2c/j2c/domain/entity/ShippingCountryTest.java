package com.j2c.j2c.domain.entity;

import com.j2c.j2c.domain.exception.DomainException;
import com.j2c.j2c.domain.test.MockEntity;
import org.junit.jupiter.api.Test;

import static com.j2c.j2c.domain.exception.DomainErrorMessages.COUNTRY_ALREADY_BELONGS_TO_ZONE;
import static org.junit.jupiter.api.Assertions.*;

class ShippingCountryTest {

    @Test
    void setZone_AlreadyBelongsToAZone_ShouldThrowDomainException() {
        final ShippingCountry shippingCountry = MockEntity.shippingCountry().build();

        final DomainException exception = assertThrows(
                DomainException.class,
                () -> shippingCountry.setZone(
                        MockEntity.shippingZone()
                                .build()
                )
        );
        assertTrue(
                exception.getMessage().matches(String.format(COUNTRY_ALREADY_BELONGS_TO_ZONE, ".+", ".+")),
                "Actual: " + exception.getMessage()
        );
    }

}
