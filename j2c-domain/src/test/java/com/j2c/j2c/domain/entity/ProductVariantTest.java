package com.j2c.j2c.domain.entity;

import com.j2c.j2c.domain.exception.DomainException;
import com.j2c.j2c.domain.test.MockEntity;
import org.junit.jupiter.api.Test;

import static com.j2c.j2c.domain.exception.DomainErrorMessages.VARIANT_REQUIRES_MASS;
import static org.junit.jupiter.api.Assertions.*;

class ProductVariantTest {

    @Test
    void new_NullProduct_ShouldThrowIllegalArgumentException() {
        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ProductVariant.builder()
                        .product(null)
                        .build()
        );
        assertEquals("product must not be null", exception.getMessage());
    }

    @Test
    void new_NotDigitalAndNullMass_ShouldThrowDomainException() {
        final DomainException exception = assertThrows(
                DomainException.class,
                () -> ProductVariant.builder()
                        .product(
                                MockEntity.product()
                                        .digital(false)
                                        .build()
                        )
                        .build()
        );
        assertTrue(
                exception.getMessage().matches(String.format(VARIANT_REQUIRES_MASS, ".+")),
                "Actual: " + exception.getMessage()
        );
    }

    @Test
    void setName_Null_ShouldThrowIllegalArgumentException() {
        final ProductVariant variant = MockEntity.productVariant().build();

        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> variant.setName(null)
        );
        assertEquals("name must not be null", exception.getMessage());
    }

    @Test
    void setMass_NotDigitalAndNullMass_ShouldThrowDomainException() {
        final ProductVariant variant = MockEntity.productVariant()
                .product(
                        MockEntity.product()
                                .digital(false)
                                .build()
                )
                .build();

        final DomainException exception = assertThrows(
                DomainException.class,
                () -> variant.setMass(null)
        );
        assertTrue(
                exception.getMessage().matches(String.format(VARIANT_REQUIRES_MASS, ".+")),
                "Actual: " + exception.getMessage()
        );
    }

    @Test
    void getEffectivePrice_OverridesProductPrice_ShouldReturnPrice() {
        final ProductVariant variant = MockEntity.productVariant().build();

        assertEquals(variant.getPrice(), variant.getEffectivePrice());
    }

    @Test
    void getEffectivePrice_DoesNotOverrideProductPrice_ShouldReturnProductPrice() {
        final ProductVariant variant = MockEntity.productVariant()
                .nullPrice(true)
                .build();

        assertEquals(variant.getProduct().getDefaultPrice(), variant.getEffectivePrice());
    }

}
