package com.j2c.j2c.domain.entity;

import com.j2c.j2c.domain.exception.DomainException;
import com.j2c.j2c.domain.test.MockEntity;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.j2c.j2c.domain.exception.DomainErrorMessages.*;
import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    @Test
    void new_NullName_ShouldThrowIllegalArgumentException() {
        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Product.builder()
                        .name(null)
                        .defaultPrice(50L)
                        .build()
        );
        assertEquals("name must not be null", exception.getMessage());
    }

    @Test
    void new_NullDefaultPrice_ShouldThrowIllegalArgumentException() {
        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Product.builder()
                        .name("awd")
                        .defaultPrice(null)
                        .build()
        );
        assertEquals("defaultPrice must not be null", exception.getMessage());
    }

    @Test
    void removeVariant_DefaultVariant_ShouldThrowDomainException() {
        final Product product = MockEntity.product().build();

        final DomainException exception = assertThrows(
                DomainException.class,
                () -> product.removeVariant(product.getDefaultVariant())
        );
        assertTrue(
                exception.getMessage().matches(String.format(CANNOT_REMOVE_DEFAULT_VARIANT, ".+")),
                "Actual: " + exception.getMessage()
        );
    }

    @Test
    void publish_AlreadyPublished_ShouldThrowDomainException() {
        final Product product = MockEntity.product()
                .published(true)
                .build();

        final DomainException exception = assertThrows(
                DomainException.class,
                product::publish
        );
        assertTrue(
                exception.getMessage().matches(String.format(ALREADY_PUBLISHED, ".+")),
                "Actual: " + exception.getMessage()
        );
    }

    @Test
    void publish_NoDefaultVariant_ShouldThrowDomainException() {
        final Product product = MockEntity.product()
                .published(false)
                .nullDefaultVariant(true)
                .build();

        final DomainException exception = assertThrows(
                DomainException.class,
                product::publish
        );
        assertTrue(
                exception.getMessage().matches(String.format(CANNOT_PUBLISH_WITHOUT_DEFAULT_VARIANT, ".+")),
                "Actual: " + exception.getMessage()
        );
    }

    @Test
    void publish_LessThanXMinutesHavePassedSinceLastUnpublish_ShouldThrowDomainException() {
        final Product product = MockEntity.product()
                .published(false)
                .lastUnpublished(LocalDateTime.now())
                .build();

        final DomainException exception = assertThrows(
                DomainException.class,
                product::publish
        );
        assertTrue(
                exception.getMessage().matches(String.format(CANNOT_PUBLISH_BEFORE_X_MINUTES, ".+", ".+")),
                "Actual: " + exception.getMessage()
        );
    }

    @Test
    void unpublish_AlreadyUnpublished_ShouldThrowDomainException() {
        final Product product = MockEntity.product()
                .published(false)
                .build();

        final DomainException exception = assertThrows(
                DomainException.class,
                product::unpublish
        );
        assertTrue(
                exception.getMessage().matches(String.format(ALREADY_UNPUBLISHED, ".+")),
                "Actual: " + exception.getMessage()
        );
    }

    @Test
    void setDefaultVariant_Null_ShouldThrowIllegalArgumentException() {
        final Product product = MockEntity.product().build();

        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> product.setDefaultVariant(null)
        );
        assertEquals("defaultVariant must not be null", exception.getMessage());
    }

    @Test
    void setDefaultVariant_CurrentDefaultVariantHasNoName_ShouldThrowDomainException() {
        final Product product = MockEntity.product()
                .defaultVariant(
                        MockEntity.productVariant()
                                .nullName(true)
                                .build()
                )
                .build();

        final DomainException exception = assertThrows(
                DomainException.class,
                () -> product.setDefaultVariant(
                        MockEntity.productVariant()
                                .build()
                )
        );
        assertTrue(
                exception.getMessage().matches(String.format(CANNOT_SET_DEFAULT_VARIANT_IF_CURRENT_HAS_NO_NAME, ".+")),
                "Actual: " + exception.getMessage()
        );
    }

    @Test
    void newVariant_CurrentDefaultVariantHasNoNameAndNoNameIsProvided_ShouldThrowDomainException() {
        final Product product = MockEntity.product()
                .digital(true)
                .defaultVariant(
                        MockEntity.productVariant()
                                .nullName(true)
                                .build()
                )
                .build();

        final DomainException exception = assertThrows(
                DomainException.class,
                () -> product.newVariant()
                        .name("awd")
                        .mass(50)
                        .add()
        );
        assertTrue(
                exception.getMessage().matches(String.format(DEFAULT_VARIANT_NAME_REQUIRED, ".+")),
                "Actual: " + exception.getMessage()
        );
    }

    @Test
    void setName_Null_ShouldThrowIllegalArgumentException() {
        final Product product = MockEntity.product().build();

        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> product.setName(null)
        );
        assertEquals("name must not be null", exception.getMessage());
    }

    @Test
    void setDefaultPrice_Null_ShouldThrowIllegalArgumentException() {
        final Product product = MockEntity.product().build();

        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> product.setDefaultPrice(null)
        );
        assertEquals("defaultPrice must not be null", exception.getMessage());
    }

}
